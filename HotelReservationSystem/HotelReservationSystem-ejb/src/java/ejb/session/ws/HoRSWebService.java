/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import Entity.BookingEntity;
import Entity.PartnerEntity;
import Entity.ReservationEntity;
import Entity.RoomRatesEntity;
import Entity.RoomTypeEntity;
import Entity.TransactionEntity;
import ejb.session.stateless.BookingControllerLocal;
import ejb.session.stateless.CustomerControllerLocal;
import ejb.session.stateless.PartnerControllerLocal;
import ejb.session.stateless.ReservationControllerLocal;
import ejb.session.stateless.RoomControllerLocal;
import ejb.session.stateless.RoomRateControllerLocal;
import ejb.session.stateless.RoomTypeControllerLocal;
import ejb.session.stateless.TransactionControllerLocal;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import util.enumeration.RateType;
import util.enumeration.ReservationType;
import util.exception.NoReservationFoundException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author Bryan
 */
@WebService(serviceName = "HoRSWebService")
@Stateless

public class HoRSWebService {

    @EJB
    private RoomControllerLocal roomControllerLocal;
    @EJB
    private CustomerControllerLocal customerControllerLocal;
    @EJB
    private BookingControllerLocal bookingControllerLocal;
    @EJB
    private RoomRateControllerLocal roomRateControllerLocal;
    @EJB
    private RoomTypeControllerLocal roomTypeControllerLocal;
    @EJB
    private ReservationControllerLocal reservationControllerLocal;
    @EJB
    private PartnerControllerLocal partnerControllerLocal;
    @EJB
    private TransactionControllerLocal transactionControllerLocal;
    
    
    
    

    
    @WebMethod(operationName = "partnerLogin") 
    public PartnerEntity partnerLogin(@WebParam(name ="email") String email, @WebParam(name = "password") String password) throws PartnerNotFoundException {
        PartnerEntity partner = partnerControllerLocal.partnerLogin(email, password);
        if(partner == null) {
            throw new PartnerNotFoundException("Partner not found");
        }
        return partner;
    }
    
    @WebMethod(operationName = "partnerSearchRoom")
    public List<RoomTypeEntity> partnerSearchRoom(@WebParam(name ="email") String email, @WebParam(name ="password") String password,
           @WebParam(name ="checkInDate") Date checkInDate,@WebParam(name ="checkOutDate") Date checkOutDate) throws PartnerNotFoundException{
        PartnerEntity partner = partnerControllerLocal.partnerLogin(email, password);
        if(partner == null) {
            throw new PartnerNotFoundException("Partner not found");
        }
        List<RoomTypeEntity> availRoomTypes = getAvailableRoomTypes(checkInDate);
        return availRoomTypes;
    }
    
    public List<RoomTypeEntity> getAvailableRoomTypes(@WebParam(name ="checkInDate") Date checkInDate) {
        List<RoomTypeEntity> availRoomTypes = new ArrayList<>();
        List<RoomTypeEntity> onlineRoomTypes = roomTypeControllerLocal.retrieveRoomTypesByRateType(RateType.NORMAL);
        for(RoomTypeEntity roomType : onlineRoomTypes) {
            if(roomControllerLocal.checkAvailabilityOfRoomByRoomTypeId(roomType.getRoomTypeId(), checkInDate)) {
                availRoomTypes.add(roomType);
            }
        }   
        return availRoomTypes;
        
    }
    
    @WebMethod(operationName = "partnerReserveRoom")
    public ReservationEntity partnerReserveRoom(@WebParam(name ="email")String email, @WebParam(name ="password")String password, @WebParam(name ="checkInDate")Date checkInDate, @WebParam(name ="checkOutDate")Date checkOutDate,@WebParam(name ="desiredRoomTypes") List<RoomTypeEntity> desiredRoomTypes, @WebParam(name ="nights")int nights) throws PartnerNotFoundException {
        try {
            PartnerEntity partner = partnerLogin(email, password);
            BigDecimal totalPrice = calculateTotalPrice(desiredRoomTypes, nights);
            ReservationEntity reservation;
            reservation = new ReservationEntity(new Date(), checkInDate, checkOutDate, false, partner, ReservationType.Partner);
            reservation = reservationControllerLocal.createNewReservation(reservation);

            //create individual room bookings
            for(RoomTypeEntity roomType : desiredRoomTypes) {
                BookingEntity booking = new BookingEntity(roomType, reservation);
                booking = bookingControllerLocal.createBooking(booking);
                reservationControllerLocal.addBookings(reservation.getReservationId(), booking);
            }

            //create unpaid transaction
            TransactionEntity transaction = new TransactionEntity(totalPrice, reservation);
            transaction = transactionControllerLocal.createNewTransaction(transaction);
            reservationControllerLocal.addTransaction(reservation.getReservationId(), transaction);

            return reservation;
        } catch(PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner not found");
        }
    }
    
    @WebMethod(operationName = "viewPartnerReservationDetails")
    public ReservationEntity viewPartnerReservationDetails(@WebParam(name ="email") String email, @WebParam(name ="password") String password, @WebParam(name ="reservationId")Long reservationId) throws PartnerNotFoundException, NoReservationFoundException {
        try {
            PartnerEntity partner = partnerLogin(email, password);
            ReservationEntity reservation = reservationControllerLocal.retrieveReservationById(reservationId);
            return reservation;
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner not found");
        } catch(NoResultException ex) {
            throw new NoReservationFoundException("no reservation found");
        }
        
    }
    
    @WebMethod(operationName = "viewAllPartnerReservations")
    public List<ReservationEntity> viewAllPartnerReservations(@WebParam(name ="email")String email, @WebParam(name ="password")String password) throws PartnerNotFoundException, NoReservationFoundException {
        try {
            PartnerEntity partner = partnerLogin(email, password);
            List<ReservationEntity> reservations = reservationControllerLocal.retrieveReservationByPartnerId(partner.getPartnerId());
            return reservations;
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner not found");
        } catch (NoResultException ex) {
            throw new NoReservationFoundException("No reservations found");
        }
        
    }
    
    @WebMethod(operationName = "calculateTotalPrice")
    public BigDecimal calculateTotalPrice(@WebParam(name ="roomTypes")List<RoomTypeEntity> roomTypes,@WebParam(name ="nights") int nights) {
        BigDecimal totalAmount = new BigDecimal(0.00);
        for (int i = 0; i < nights; i++) {
            for (RoomTypeEntity roomType : roomTypes) {
                List<RoomRatesEntity> roomRateList = roomTypeControllerLocal.retrieveRoomRateListById(roomType.getRoomTypeId());
                for (RoomRatesEntity roomRate : roomRateList) {
                    if (roomRate.getRateType() == RateType.PUBLISHED) {
                        //System.out.println("Rate per night: "+roomRate.getRatePerNight());
                        totalAmount = totalAmount.add(roomRate.getRatePerNight());
                        
                    }
                }
            }
        }
        //System.out.println("Total: "+totalAmount);
        return totalAmount;
    }


    
}
