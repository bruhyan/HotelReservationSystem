/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.BookingEntity;
import Entity.ReservationEntity;
import Entity.RoomAllocationException;
import Entity.RoomEntity;
import Entity.RoomTypeEntity;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mdk12
 */
@Stateless
@Local(SystemTimerSessionBeanLocal.class)
@Remote(SystemTimerSessionBeanRemote.class)
public class SystemTimerSessionBean implements SystemTimerSessionBeanRemote, SystemTimerSessionBeanLocal {

    @Resource
    private SessionContext sessionContext;
    @EJB
    private BookingControllerLocal bookingControllerLocal;
    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;

    @EJB
    private RoomAllocationExceptionControllerLocal roomAllocationExceptionControllerLocal;

    public SystemTimerSessionBean() {

    }

    public void init() {
        TimerService timerService = sessionContext.getTimerService();
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo("SystemRoomAllocation_Info");
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.dayOfWeek("*").hour("2");
        timerService.createCalendarTimer(schedule, timerConfig);
    }

    @Timeout
    @Override
    public void roomAllocation() {
        //business rule : booking requires room of room type, no more room type left, allocate one that is higher (More expensive? How to determine is a room type is better?
        // more expensive, higher capacity)
        //If no more, then just dont allocate.

        //new understanding : Ranking is done manually,
        //check availability on the day itself,  so needs to be available. Even if occupied and customer checking out, 
        //Check reservation for today date
        
        List<ReservationEntity> reservationList = reservationControllerLocal.retrieveTodayReservationList();
        
        for(ReservationEntity reservation : reservationList){
        
        List<BookingEntity> bookingList = reservation.getBookingList();
        RoomAllocationException exception = new RoomAllocationException();
        exception = roomAllocationExceptionControllerLocal.saveException(exception);
        for (BookingEntity booking : bookingList) {
            System.out.println("Booking found " + booking.getBookingId());
            RoomTypeEntity roomTypeNeeded = booking.getRoomType();
            //check if roomtype is available.
            Long roomTypeId = roomTypeNeeded.getRoomTypeId();

            if (roomControllerLocal.checkAvailabilityOfRoomTypeWhenAllocating(roomTypeId)) {
                //RoomType is available, and not isReserved
                            System.out.println("Regular Reservation made .");
                doRegularAllocation(booking.getBookingId(), roomTypeId, exception);
            } else {

                doUpgradeAllocation(booking.getBookingId(), roomTypeId, exception);

            }

        }
       }
    }

    public void doRegularAllocation(Long bookingId, Long roomTypeId, RoomAllocationException exception) {
        BookingEntity booking = em.find(BookingEntity.class, bookingId);
        RoomTypeEntity roomType = em.find(RoomTypeEntity.class, roomTypeId);

        RoomEntity room = roomControllerLocal.allocateRoom(roomTypeId);
        //sets to isReserved

        booking.setRoom(room);
        room.setBooking(booking);

    }

    public void doUpgradeAllocation(Long bookingId, Long roomTypeId, RoomAllocationException exception) {
        
        BookingEntity booking = em.find(BookingEntity.class, bookingId);
        RoomTypeEntity oldRoomType = em.find(RoomTypeEntity.class, roomTypeId);
        RoomTypeEntity roomType;

        roomType = roomTypeControllerLocal.findUpgradeRoomType(roomTypeId);

        if (roomType == null) {
            //cannot allocate, report exception use case 16
            System.out.println("Booking id : " + booking.getBookingId() + "'s room cannot be allocated! All upgraded room type are unavailable!");
            exception.getExceptions().add("Booking id : " + booking.getBookingId() + "'s room cannot be allocated! All upgraded room type are unavailable!");

            return;
        }
        Long upgradedId = roomType.getRoomTypeId();
        roomType = em.find(RoomTypeEntity.class, upgradedId);
        RoomEntity room = roomControllerLocal.allocateRoom(upgradedId);

        booking.setRoom(room);
        room.setBooking(booking);

        System.out.println("Booking id : " + booking.getBookingId() + "'s room type have been upgraded from " + oldRoomType.getRoomTypeName() + " to " + roomType.getRoomTypeName() + "."); //Use case 16
        exception.getExceptions().add("Booking id : " + booking.getBookingId() + "'s room type have been upgraded from " + oldRoomType.getRoomTypeName() + " to " + roomType.getRoomTypeName() + ".");

    }

}
