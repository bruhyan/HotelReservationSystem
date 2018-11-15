/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.RoomRatesEntity;
import Entity.RoomTypeEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RateType;

/**
 *
 * @author mdk12
 */
@Stateless

@Remote(RoomRateControllerRemote.class)
@Local(RoomRateControllerLocal.class)

public class RoomRateController implements RoomRateControllerRemote, RoomRateControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    @Override
    public RoomRatesEntity createNewRoomRate(RoomRatesEntity roomRates) {
        em.persist(roomRates);
        em.flush();
        
        
        return em.find(RoomRatesEntity.class, roomRates.getRoomRatesId()); 
    }


    @Override
    public void deleteRoomRatesById(Long id) {
        RoomRatesEntity roomRates = retrieveRoomRatesById(id);
        //call room controller local to find list by Type
        List<RoomTypeEntity> roomTypes = roomTypeControllerLocal.retrieveRoomTypeListByRates(roomRates);
        if (!roomTypes.isEmpty()) {
//            //set all to be disabled
//            for (RoomTypeEntity roomType : roomTypes) {
//                roomType.setIsDisabled(true); //set all the type disable
//
//            }

//Revamped : Just check if room rate is disabled when displaying room types to choose.
            roomRates.setIsDisabled(true);

        } else {
            em.remove(roomRates);
        }
    }

    @Override
    public void addRoomTypeById(Long roomRateId, Long roomTypeId) {
        RoomRatesEntity roomRate = em.find(RoomRatesEntity.class, roomRateId);
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);

        roomRate.addRoomType(roomType);


    }

    @Override
    public RoomRatesEntity retrieveRoomRatesById(Long id) {
        return em.find(RoomRatesEntity.class, id);

    }

    @Override
    public List<RoomRatesEntity> retrieveRoomRatesList() {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r");
        return query.getResultList();
    }

    @Override
    public List<RoomRatesEntity> retrieveCompulsoryRoomRatesList() {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r WHERE r.rateType = :type1 OR r.rateType = :type2");
        query.setParameter("type1", RateType.PUBLISHED);
        query.setParameter("type2", RateType.NORMAL);
        return query.getResultList();
    }

    //heavyUpdateRoomRate(roomRate.getRoomRatesId(), roomRateName, ratePerNight, date2, date3);
    public RoomRatesEntity heavyUpdateRoomRate(Long roomRateId, String roomRateName, BigDecimal ratePerNight, Date dateStart, Date dateEnd) {
        RoomRatesEntity roomRates = retrieveRoomRatesById(roomRateId);
        roomRates.setName(roomRateName);
        roomRates.setRatePerNight(ratePerNight);
        roomRates.setValidityStart(dateStart);
        roomRates.setValidityEnd(dateEnd);

        return roomRates;

    }


    //get a list of room rates exclude a roomType

    @Override
    public List<RoomRatesEntity> retrieveRoomRateListExcludeRoomType(Long roomTypeId) {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r JOIN r.roomTypeList r1 WHERE r1.roomTypeId <> :roomTypeId");
        query.setParameter("roomTypeId", roomTypeId);
        return query.getResultList();
    }

    public RoomRatesEntity retriveRoomRateByRateType(RateType rateType) {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r WHERE r.rateType = :rateType");
        query.setParameter("rateType", rateType);
        return (RoomRatesEntity) query.getSingleResult();
    }

    //get list of room by room type.
//    public List<RoomRatesEntity> retrieveRoomRatesListByType(RoomTypeEntity roomType){
//
//        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType"); //find out if this is correct
//        query.setParameter("roomType", roomType);
//        
//        return query.getResultList();
//        
//    }
//    public List<RoomEntity> retrieveRoomListByTypeId(Long roomTypeId){
//        
//        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType  = :roomType"); //find out if this is correct
//        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
//        query.setParameter("roomType", roomType);
//        
//        return query.getResultList();
//        
//    }
    
    public void deleteAllDisabledRoomRates() {
        Query query = em.createQuery("SELECT r FROM RoomRatesEntity r WHERE r.isDisabled = true");
        List<RoomRatesEntity> roomRates = query.getResultList();
        for(RoomRatesEntity roomRate : roomRates) {
            em.remove(roomRate);
        }
    }
}
