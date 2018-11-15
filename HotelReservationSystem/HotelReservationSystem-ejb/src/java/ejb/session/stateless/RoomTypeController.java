/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.RoomEntity;
import Entity.RoomRatesEntity;
import Entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RateType;
import util.exception.NoAvailableOnlineRoomRateException;

/**
 *
 * @author mdk12
 */
@Stateless
@Local(RoomTypeControllerLocal.class)
@Remote(RoomTypeControllerRemote.class)
public class RoomTypeController implements RoomTypeControllerRemote, RoomTypeControllerLocal {

    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private RoomControllerRemote roomControllerRemote;
    @EJB
    private RoomRateControllerRemote roomRateControllerRemote;
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public RoomTypeEntity createNewRoomType(RoomTypeEntity roomType) {

        List<RoomTypeEntity> roomTypeList = retrieveRoomTypeList();
        int rank = roomType.getRanking();
        int lowestRank = getLowestRank();

        if (rank == lowestRank + 1) {
            em.persist(roomType);
            em.flush();
        } else {
            List<RoomTypeEntity> roomTypeAdjust = getRoomTypeListToAdjust(rank);
            for (RoomTypeEntity roomTypeOld : roomTypeAdjust) {
                //System.out.println(roomTypeOld.getRanking() + " rank "+  roomTypeOld.getRoomTypeName());
                roomTypeOld.setRanking(roomTypeOld.getRanking() + 1);
            }
            em.persist(roomType);
            em.flush();
        }

        return roomType;
    }

    public List<RoomTypeEntity> getRoomTypeListToAdjust(int rank) {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.ranking >= :rank ORDER BY r.ranking DESC");
        query.setParameter("rank", rank);
        return query.getResultList();
    }

    public int getLowestRank() {
        Query query = em.createQuery("SELECT r.ranking FROM RoomTypeEntity r WHERE r.isDisabled = false ORDER BY r.ranking DESC");
        return query.getFirstResult();
    }


    public void addRoomRateById(Long roomTypeId, Long roomRateId) {
        RoomRatesEntity roomRate = em.find(RoomRatesEntity.class, roomRateId);
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);

        roomType.addRoomRate(roomRate);

        em.merge(roomType);
    }

    public RoomTypeEntity retrieveRoomTypeById(long id) {
        return em.find(RoomTypeEntity.class, id);

    }

    public RoomTypeEntity heavyUpdateRoom(long id, String name, String description, int size, String bed, String amenities, int capacity) {
        RoomTypeEntity roomType = retrieveRoomTypeById(id);
        roomType.setAmenities(amenities);
        roomType.setRoomTypeName(name);
        roomType.setDescription(description);
        roomType.setSize(size);
        roomType.setBed(bed);
        roomType.setCapacity(capacity);

        return roomType;
    }

    public void updateRoomRank(int rank, Long roomTypeId) {
        List<RoomTypeEntity> roomTypeList = retrieveRoomTypeList();
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        int currentRank = roomType.getRanking();
//
//        if (rank == lowestRank + 1) {
//            em.persist(roomType);
//            em.flush();
//        } else {
        List<RoomTypeEntity> roomTypeAdjust = getRoomTypeListToAdjust(currentRank + 1);
        for (RoomTypeEntity roomTypeOld : roomTypeAdjust) {
            roomTypeOld.setRanking(roomTypeOld.getRanking() - 1);
        }

        int lowestRank = getLowestRank() + 1;

        if (rank == lowestRank) {
           roomType.setRanking(rank);
        } else {
            List<RoomTypeEntity> roomTypeAdjustNew = getRoomTypeListToAdjust(rank);
            for (RoomTypeEntity roomTypeOld : roomTypeAdjustNew) {
                //System.out.println(roomTypeOld.getRanking() + " rank "+  roomTypeOld.getRoomTypeName());
                roomTypeOld.setRanking(roomTypeOld.getRanking() + 1);
            }
            roomType.setRanking(rank);
        }

    }

    public void deleteRoomTypeById(long id) {
        RoomTypeEntity roomType = retrieveRoomTypeById(id);
        int rank = roomType.getRanking();
        
        List<RoomTypeEntity> roomTypes = getRoomTypeListToAdjust(rank + 1);
        for(RoomTypeEntity roomTypeNew : roomTypes){
            roomTypeNew.setRanking(roomTypeNew.getRanking()-1);
        }
        //call room controller local to find list by Type
        List<RoomEntity> roomList = roomControllerLocal.retrieveRoomListByTypeId(id); //this is returning null. Maybe EJB cannot call other ejb this way?
        if (!roomList.isEmpty()) {
            //set all to be disabled
            //set roomType to be disabled as well.
            roomType.setIsDisabled(true);


        } else {
            em.remove(roomType);
        }

    }

    public RoomTypeEntity retrieveSingleRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r");
        return (RoomTypeEntity) query.getSingleResult();
    }

    public List<RoomTypeEntity> retrieveRoomTypeList() {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r");

        return query.getResultList();
    }

    public List<RoomRatesEntity> retrieveRoomRateListById(Long roomTypeId) {
        Query query = em.createQuery("SELECT r.roomRateList FROM RoomTypeEntity r WHERE r.roomTypeId = :roomTypeId");
        query.setParameter("roomTypeId", roomTypeId);

        return query.getResultList();

    }

    //Not used
    @Override
    public List<RoomTypeEntity> retrieveRoomTypeListByRates(RoomRatesEntity roomRates) {
        Long roomRateId = roomRates.getRoomRatesId();
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r JOIN r.roomRateList rl WHERE rl.roomRatesId = :roomRateId");
        query.setParameter("roomRateId", roomRateId);

        return query.getResultList();

    }

    //Not used
    @Override
    public List<RoomEntity> retrieveRoomEntityByRoomType(RoomTypeEntity roomType) {
        Long roomTypeId = roomType.getRoomTypeId();
        Query query = em.createQuery("SELECT r FROM RoomEntity r JOIN r.roomType r1 WHERE r1.roomTypeId = :roomTypeId");
        query.setParameter("roomTypeId", roomTypeId);
        return query.getResultList();

    }

    //Need to confirm with prof how to determine a upgrade
    /*@Override
    public RoomTypeEntity findPricierAvailableRoomTypeForOnlineOrPartner(Long roomTypeId) {

        try {
            RoomRatesEntity currentRate = findOnlineRateForRoomType(roomTypeId);
            try {

                List<RoomRatesEntity> roomRateListExclude = roomRateControllerRemote.retrieveRoomRateListExcludeRoomType(roomTypeId);

                //Here need to sort out the price according to prof rules
                Collections.sort(roomRateListExclude, (RoomRatesEntity r1, RoomRatesEntity r2)
                        -> r1.getRatePerNight().compareTo(r2.getRatePerNight()));

                //sorted, now compare if == or >, get first one.
                RoomRatesEntity pricierRoomRate = null;
                BigDecimal currentRoomRate = currentRate.getRatePerNight();

                for (RoomRatesEntity roomRate : roomRateListExclude) {
                    System.out.println(currentRoomRate + " " + roomRate.getRatePerNight()); //checking the prices
                    if (currentRoomRate.compareTo(roomRate.getRatePerNight()) == 0 || currentRoomRate.compareTo(roomRate.getRatePerNight()) < 0) {
                        pricierRoomRate = em.find(RoomRatesEntity.class, roomRate.getRoomRatesId());

                        //check if this list have any available roomType. Actually here maybe can use JPQL also, if free can try for fun
                        List<RoomTypeEntity> pricierRoomTypes = pricierRoomRate.getRoomTypeList();

                        for (RoomTypeEntity roomType : pricierRoomTypes) {
                            if (roomControllerLocal.checkAvailabilityOfRoomByRoomTypeId(roomType.getRoomTypeId())) {
                                return roomType;
                            }
                        }
                        //means haven't found yet in current price range.

                    }
                    //go to next higher price, repeat.
                }

            } catch (NoResultException ex) {
                System.out.println("No published room rate found for the given room type! Please create a room rate of published room type for this room type first.");
            }
        } catch (NoAvailableOnlineRoomRateException ex) {
            System.out.println("There is no room rate available for this room type in the first place! Please assign a normal rate at least.");
        }

        //Means no roomType available.
        return null;

    }
     */
    public void removeRoomRate(Long roomTypeId, Long roomRateId) {
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        RoomRatesEntity roomRate = em.find(RoomRatesEntity.class, roomRateId);
        roomType.getRoomRateList().remove(roomRate);
        roomRate.getRoomTypeList().remove(roomType);
    }

    public boolean checkValidityOfRoomRate(RoomRatesEntity roomRate) {
        Date start = roomRate.getValidityStart();
        Date end = roomRate.getValidityEnd();

        Date date = new Date();
        if (date.after(start) && date.before(end)) {
            return true;
        }

        return false;
    }

    //This method will find the final room rate to apply when given a room type id, call when making transaction
    public RoomRatesEntity findOnlineRateForRoomType(Long roomTypeId) throws NoAvailableOnlineRoomRateException {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId");
        query.setParameter("roomTypeId", roomTypeId);
        List<RoomRatesEntity> roomRates = query.getResultList();

        //Check what rate type are present
        boolean normal = false;
        boolean promo = false;
        boolean peak = false;

        for (RoomRatesEntity roomRate : roomRates) {
//            if (!checkValidityOfRoomRate(roomRate)) { //skips expired/not started rates, price is determined by check in and check out date, it becomes not considered in our final prediction
//                continue;
//            }
            if (null != roomRate.getRateType()) {
                switch (roomRate.getRateType()) {
                    case NORMAL:
                        normal = true;
                        break;
                    case PROMOTIONAL:
                        promo = true;
                        break;
                    case PEAK:
                        peak = true;
                        break;
                    default:
                        break;
                }
            }
        }

        System.out.println(normal + " " + promo + " " + peak);
        //5 rules here
        if (normal && promo && peak) {
            //find cheapest promo
            Query rule = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :p ORDER BY r.ratePerNight ASC");
            rule.setParameter("p", RateType.PROMOTIONAL);
            rule.setParameter("roomTypeId", roomTypeId);

            //cheapest first.
            return (RoomRatesEntity) rule.getResultList().get(0);
        } else if (promo && peak && !normal || normal && peak && !promo) {
            //apply peak, assume only 1
            Query rule = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :p");
            rule.setParameter("p", RateType.PEAK);
            rule.setParameter("roomTypeId", roomTypeId);

            return (RoomRatesEntity) rule.getSingleResult();
        } else if (normal && promo && !peak) {
            //apply cheapest promo
            Query rule = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :p ORDER BY r.ratePerNight ASC");
            rule.setParameter("p", RateType.PROMOTIONAL);
            rule.setParameter("roomTypeId", roomTypeId);

            //cheapest first.
            return (RoomRatesEntity) rule.getResultList().get(0);
        } else if (normal && !promo && !peak) {
            //apply normal
            Query rule = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :p");
            rule.setParameter("p", RateType.NORMAL);
            rule.setParameter("roomTypeId", roomTypeId);

            return (RoomRatesEntity) rule.getSingleResult();
        }

        throw new NoAvailableOnlineRoomRateException("There is no available room rate to be used!");

    }
    
    public RoomTypeEntity getRoomTypeByRank(int rank){
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.ranking = :rank");
        query.setParameter("rank", rank);
        
        return (RoomTypeEntity) query.getSingleResult();
        
    }
    
    public RoomTypeEntity findUpgradeRoomType(Long roomTypeId){
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        int currentRank = roomType.getRanking();
        
        //check availability of the next higher ranks
        
        for(int i = currentRank; i >= 1; i--){
            
            RoomTypeEntity nextRoomType = getRoomTypeByRank(i);
            if(roomControllerLocal.checkAvailabilityOfRoomTypeWhenAllocating(nextRoomType.getRoomTypeId())){
                return nextRoomType;
            }
            
            
        }

        return null;
    }

    //This method will get the RoomRateEntity of room type when it is a reservation for today
    public RoomRatesEntity findWalkInRateForRoomTypeToday(Long roomTypeId) {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :rateType");
        query.setParameter("roomTypeId", roomTypeId);
        query.setParameter("rateType", RateType.PUBLISHED);

        try {
            RoomRatesEntity currentRoomRate = (RoomRatesEntity) query.getSingleResult(); //gets me the published rate of a room type.
            return currentRoomRate;
        } catch (NoResultException ex) {

            System.out.println("No published room rate found for the given room type! Please create a room rate of published room type for this room type first.");
            return null;
        }
    }

    public List<RoomTypeEntity> retrieveRoomTypeByRanking() {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r ORDER BY r.ranking ASC ");
        return query.getResultList();
    }

    //This method will settle the walk in reservation for future dates in the event of unavailable room type.
    //It should be called only when walkIn is true
    //Precondition : Room type was unavailable, should be done in the system timer
    /*
    @Override
    public RoomTypeEntity findPricierAvailableRoomTypeForWalkIn(Long roomTypeId) {

        //find actual price of this roomType.
        //First find the roomrates attached to this room type .
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId = :roomTypeId AND r.rateType = :rateType");
        query.setParameter("roomTypeId", roomTypeId);
        query.setParameter("rateType", RateType.PUBLISHED);

        try {
            RoomRatesEntity currentRoomRate = (RoomRatesEntity) query.getSingleResult(); //gets me the published rate of a room type.
            BigDecimal publishedRate = currentRoomRate.getRatePerNight();

            //lowest rate is the rate in question, now find another room type that is higher.
            //sort rates first, then find the one after highest rate. 
            //one after highest rate shouldn't be same roomType.
            List<RoomRatesEntity> roomRateListExclude = roomRateControllerRemote.retrieveRoomRateListExcludeRoomType(roomTypeId);

            //now we sort the room rate exclude into ascending order, either equal pricing, or next higher.
            Collections.sort(roomRateListExclude, (RoomRatesEntity r1, RoomRatesEntity r2)
                    -> r1.getRatePerNight().compareTo(r2.getRatePerNight()));

            //sorted, now compare if == or >, get first one.
            RoomRatesEntity pricierRoomRate = null;

            for (RoomRatesEntity roomRate : roomRateListExclude) {
                System.out.println(currentRoomRate + " " + roomRate.getRatePerNight()); //checking the prices
                if (publishedRate.compareTo(roomRate.getRatePerNight()) == 0 || publishedRate.compareTo(roomRate.getRatePerNight()) < 0) {
                    pricierRoomRate = em.find(RoomRatesEntity.class, roomRate.getRoomRatesId());

                    //check if this list have any available roomType. Actually here maybe can use JPQL also, if free can try for fun
                    List<RoomTypeEntity> pricierRoomTypes = pricierRoomRate.getRoomTypeList();

                    for (RoomTypeEntity roomType : pricierRoomTypes) {
                        if (roomControllerLocal.checkAvailabilityOfRoomByRoomTypeId(roomType.getRoomTypeId())) { // this is fine as it is checking on the day itself, date is considered
                            return roomType;
                        }
                    }
                    //means haven't found yet in current price range.

                }
                //go to next higher price, repeat.
            }

        } catch (NoResultException ex) {
            System.out.println("No published room rate found for the given room type! Please create a room rate of published room type for this room type first.");
        }
        //Means no roomType available.
        return null;
    }*/
    //might be handy
    public RoomTypeEntity findPricierRoomTypeForOnline(RoomRatesEntity currentRate, Long roomTypeId) {

        //generate List of pricier room rates that is not the same room type and not published.
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId <> :roomTypeId AND r.rateType <> :rateType AND r.ratePerNight > :currentRate");
        query.setParameter("roomTypeId", roomTypeId);
        query.setParameter("rateType", RateType.PUBLISHED);

        BigDecimal currentRatePerNight = currentRate.getRatePerNight();
//        List<RoomTypeEntity> pricierRoomTypes = pricierRoomRate.getRoomTypeList();
//
//        for (RoomTypeEntity roomType : pricierRoomTypes) {
//            if (roomControllerLocal.checkAvailabilityOfRoomByRoomTypeId(roomType.getRoomTypeId())) {
//                return roomType;
//            }
//        }
        return null;
    }
    
    

    @Override
    public List<RoomTypeEntity> retrieveRoomTypesByRateType(RateType rateType) {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r JOIN r.roomRateList r1 WHERE r1.rateType = :rateType");

        query.setParameter("rateType", rateType);

        return query.getResultList();

    }
    
    @Override
    public void deleteAllDisabledRoomType() {
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r WHERE r.isDisabled = true");
        List<RoomTypeEntity> roomTypes = query.getResultList();
        for(RoomTypeEntity room : roomTypes) {
            em.remove(room);
        }
    }

}
