/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.BookingEntity;
import Entity.ReservationEntity;
import Entity.RoomEntity;
import Entity.RoomTypeEntity;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RoomStatus;

/**
 *
 * @author mdk12
 */
@Stateless
@Local(RoomControllerLocal.class)
@Remote(RoomControllerRemote.class)
public class RoomController implements RoomControllerRemote, RoomControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    public void createNewRoom(RoomEntity room) {
        em.persist(room);
        em.flush();
    }

    public List<RoomEntity> retrieveRoomList() {
        Query query = em.createQuery("SELECT r FROM RoomEntity r");
        return query.getResultList();
    }

    public void deleteRoomById(Long id) {
        RoomEntity room = retrieveRoomById(id);
        //Double check here
        //Delete previous rooms that was set to disabled, or mass deletion by checking if smt is disabled when system starts.
        if (room.getRoomStatus() == RoomStatus.OCCUPIED || room.getIsReserved() == true || room.getRoomStatus() != RoomStatus.AVAILABLE) {
            room.setIsDisabled(true);
            //actually after is disabled for room, still must delete if a room is disabled.

        } else {
            em.remove(room);
        }
    }

    public void changeIsReserved(Long roomId, boolean value) {
        RoomEntity room = em.find(RoomEntity.class, roomId);
        room.setIsReserved(value);
    }

    public RoomEntity retrieveRoomById(Long id) {
        return em.find(RoomEntity.class, id);

    }
    //Overloaded method

    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long roomTypeId, boolean isDisabled) {
        RoomEntity room = em.find(RoomEntity.class, id);
        room.setRoomNumber(roomNumber);
        room.setRoomStatus(newRoomStatus);
        room.setIsDisabled(isDisabled);
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        room.setRoomType(roomType);

        return room;
    }

    @Override
    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long bookingId, long roomTypeId, boolean isDisabled) {
        RoomEntity room = em.find(RoomEntity.class, id);
        room.setRoomNumber(roomNumber);
        room.setRoomStatus(newRoomStatus);
        room.setIsDisabled(isDisabled);
        BookingEntity booking = em.find(BookingEntity.class, bookingId);

        room.setBooking(booking);
        booking.setRoom(room);

        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        room.setRoomType(roomType);

        return room;
    }
    //get list of room by room type.

    @Override
    public List<RoomEntity> retrieveRoomListByType(RoomTypeEntity roomType) {

        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType");
        query.setParameter("roomType", roomType);

        return query.getResultList();

    }

    @Override
    public List<RoomEntity> retrieveRoomListByTypeId(Long roomTypeId) {

        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType  = :roomType");
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);
        query.setParameter("roomType", roomType);

        return query.getResultList();

    }

    //check if room type has available rooms
    @Override
    public boolean checkAvailabilityOfRoomByRoomTypeId(Long roomTypeId, Date checkInDate) {

        RoomTypeEntity roomType = em.find((RoomTypeEntity.class), roomTypeId);
        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus AND r.isReserved = false");
        query.setParameter("roomType", roomType);
        query.setParameter("roomStatus", RoomStatus.AVAILABLE);
        
        if(!query.getResultList().isEmpty()){
            return true;
        }

        //Logic : First check if available, If not, we check whether anyone is booking out 
        // A wants to checkout on 25-01, Currently occupied, + isreserved = false
        //B wants to check in on 27, although it shows occupied, actually can reserve.
        //If old checkout < new check in , isReserved = true.
        //new checkout date earlier than old check in date
        //find reservations with checkout date < check in, is occupied (dont need care about checkin date alr),
        // when found, means this roomType is available.
        Calendar cal = Calendar.getInstance();

        cal.setTime(checkInDate);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date today = new Date();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        today = cal.getTime();

        Query query2 = em.createQuery("SELECT r FROM ReservationEntity r WHERE r.checkOutDateTime < :checkInDate AND r.checkInDateTime < :today");
        query2.setParameter("checkInDate", checkInDate);
        query2.setParameter("today", today);

        List<ReservationEntity> occupiedReservationCanReserve = query2.getResultList();

        System.out.println("Query ran, now testing");

        for (ReservationEntity reservation : occupiedReservationCanReserve) {
            System.out.println("Found one, " + reservation.getReservationId());

            List<BookingEntity> bookings = reservation.getBookingList();
            for (BookingEntity booking : bookings) {
                            System.out.println("Room Type =  " + booking.getRoomType().getRoomTypeId() + " comparing with " + roomTypeId);
                if (        Objects.equals(booking.getRoomType().getRoomTypeId(), roomTypeId)) {
                    System.out.println("Success true");
                    return true;
                }
            }
        }
        return false;

    }

//check if room type has available rooms
    @Override
    public boolean checkAvailabilityOfRoomTypeWhenAllocating(Long roomTypeId) {

        RoomTypeEntity roomType = em.find((RoomTypeEntity.class), roomTypeId);
        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus AND r.isReserved = false AND r.isDisabled = false");
        query.setParameter("roomType", roomType);
        query.setParameter("roomStatus", RoomStatus.AVAILABLE);

        List<RoomTypeEntity> roomTypes = query.getResultList();

        if (roomTypes.isEmpty()) {
            return false;
        }
        return true;

    }

    public RoomEntity
            allocateRoom(Long roomTypeId) {
        RoomTypeEntity roomType = em.find((RoomTypeEntity.class), roomTypeId);
        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus AND r.isReserved = false");
        query.setParameter("roomType", roomType);
        query.setParameter("roomStatus", RoomStatus.AVAILABLE);

        List<RoomEntity> roomList = query.getResultList();
        RoomEntity room = roomList.get(0);

        room.setIsReserved(true);

        return room;

    }

    public RoomEntity
            walkInAllocateRoom(Long roomTypeId) {
        RoomTypeEntity roomType = em.find((RoomTypeEntity.class), roomTypeId);
        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.roomType = :roomType AND r.roomStatus = :roomStatus AND r.isReserved = false");
        query.setParameter("roomType", roomType);
        query.setParameter("roomStatus", RoomStatus.AVAILABLE);

        List<RoomEntity> roomList = query.getResultList();
        RoomEntity room = roomList.get(0);

        room.setIsReserved(true);

        return room;
    }

    public void changeRoomStatus(Long roomEntityId, RoomStatus status) {
        RoomEntity room = em.find(RoomEntity.class,
                 roomEntityId);
        room.setRoomStatus(status);
    }
    
    public void deleteAllDisabledRooms() {
        Query query = em.createQuery("SELECT r FROM RoomEntity r WHERE r.isDisabled = true");
        List<RoomEntity> rooms = query.getResultList();
        for(RoomEntity room : rooms) {
            if(room.getRoomStatus() == RoomStatus.AVAILABLE) {
                em.remove(room);
            }
        }
    }
    
    
    
   
}
