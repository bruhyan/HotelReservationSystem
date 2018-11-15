/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import ws.session.holiday.NoReservationFoundException_Exception;
import ws.session.holiday.PartnerEntity;
import ws.session.holiday.PartnerNotFoundException;
import ws.session.holiday.PartnerNotFoundException_Exception;
import ws.session.holiday.ReservationEntity;
import ws.session.holiday.RoomTypeEntity;

/**
 *
 * @author mdk12
 */
public class MainApp {

    protected PartnerEntity partner = null;

    public MainApp() {

    }

    public void run() {

        Scanner sc = new Scanner(System.in);
        
        while (true) {
            int input = 0;

            System.out.println("==== Welcome to the Holiday Reservation System ====");
            if (partner == null) {
                System.out.println("1: Partner Login ");
                System.out.println("2: Exit ");
            } else {
                System.out.println("1: Partner Search Room ");
                System.out.println("2: View Partner Reservation Details ");
                System.out.println("3: View All Partner Reservations");
                System.out.println("4: Logout");
                System.out.println("5: Exit \n");
            }

            if (partner != null) {
               

                while (input < 1 || input > 6) {
                     input = sc.nextInt();
                sc.nextLine();
                    if (input == 1) {
                        doPartnerSearchRoom(sc);
                    } else if (input == 2) {
                        doViewPartnerReservationDetails(sc);
                    } else if (input == 3) {
                        doViewAllPartnerReservation(sc);
                    } else if (input == 4) {
                        doPartnerLogout();
                    } else if (input == 5) {
                        break;
                    } else if (input == 6) {
                        System.out.println("Invalid response! Please try again. \n");
                    }
                }
            } else {
               

                while (input < 1 || input > 2) {
                     input = sc.nextInt();
                sc.nextLine();
                    if (input == 1) {
                        doPartnerLogin(sc);
                    } else if (input == 2) {
                        break;
                    } else {
                        System.out.println("Invalid response! Please try again. \n");
                    }
                }

            }
            if (input == 5 && partner != null || input == 2 && partner == null) {
                break;
            }
        }
    }

    public void doPartnerLogin(Scanner sc) {
        System.out.println("Please enter your email: ");
        System.out.print(">");
        String email = sc.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print(">");
        String password = sc.nextLine();

        try {
            this.partner = partnerLogin(email, password);
        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Sorry, the credentials you have typed are either invalid or no such partner exist! \n");
        }

    }

    public void doPartnerSearchRoom(Scanner sc) {
        System.out.println("Please enter your email: ");
        System.out.print(">");
        String email = sc.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print(">");
        String password = sc.nextLine();

        System.out.println("Enter check in year: [YYYY]");
        int year = sc.nextInt();
        year -= 1900;
        System.out.println("Enter check in month: [ 1(January)~12(December) ]");
        int month = sc.nextInt();
        month -= 1;
        System.out.println("Enter check in date [1 ~ 31]"); //2pm check in
        int day = sc.nextInt();
        Date checkInDate = new Date(year, month, day, 14, 0);
        Calendar cal = Calendar.getInstance();
        cal.setTime(checkInDate);
        System.out.println("Enter how many nights of stay"); //12pm checkout
        int nights = sc.nextInt();
        sc.nextLine();
        cal.add(Calendar.DATE, nights);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        Date checkOutDate = cal.getTime();

        try {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(checkInDate);
            XMLGregorianCalendar date1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
//            XMLGregorianCalendar date1 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
            c.setTime(checkOutDate);
            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);

//            XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
            
            //DatatypeFactory.newInstance().newXMLGregorianCalendar(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, 
            //c.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED,
            //DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED)
            List<RoomTypeEntity> desiredRoomTypes = new ArrayList<>();
            while (true) {
                List<RoomTypeEntity> roomTypeList = partnerSearchRoom(email, password, date1, date2);
                int index = 1;
                System.out.println("==== Room Types with available rooms =====");
                for (RoomTypeEntity roomType : roomTypeList) {
                    System.out.println("#" + index + " RoomType: " + roomType.getRoomTypeName());
                    index++;
                }
                System.out.println("==========================================");
                System.out.println("Select desired room type by index");
                int choice = sc.nextInt();
                sc.nextLine();
                desiredRoomTypes.add(roomTypeList.get(choice -= 1));
                System.out.println("Enter 1 to continue adding more room types");
                if (sc.nextInt() != 1) {
                    break;
                }
            }
                BigDecimal totalPrice = calculateTotalPrice(desiredRoomTypes, nights);
                System.out.println("Total price: $" + totalPrice);
                //initiate reserve room
                System.out.println("Do you want to reserve rooms?");
                System.out.println("Enter 1 to reserve, Enter 2 to exit");
                int reply = sc.nextInt();
                sc.nextLine();
                if (reply == 1) {
                    ReservationEntity reservation = partnerReserveRoom(email, password, date1, date2, desiredRoomTypes, nights);
                    System.out.println("A new reservation has been made at Merlion Hotel! Your reservation ID is : " + reservation.getReservationId());
                }
            

        } catch (PartnerNotFoundException_Exception ex) {
            System.out.println("Sorry, the credentials you have typed are either invalid or no such partner exist! \n");
        } catch (DatatypeConfigurationException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void doViewPartnerReservationDetails(Scanner sc) {
        System.out.println("Please enter your email: ");
        System.out.print(">");
        String email = sc.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print(">");
        String password = sc.nextLine();

        System.out.println("Please enter your Reservation ID :  \n");
        System.out.print(">");

        Long reservationId = sc.nextLong();
        sc.nextLine();
        try {

            ReservationEntity reservation = viewPartnerReservationDetails(email, password, reservationId);
            System.out.println("Your Reservation details are as follows : \n");
            System.out.println("Reservation ID: " + reservationId);
            System.out.println("Date of Reservation: " + reservation.getDateOfReservation());
            //System.out.println("Check in Date & Time : " + reservation.getCheckInDateTime());
            System.out.println("Check out Date & Time : " + reservation.getCheckOutDateTime());
            System.out.println("Reservation Type: " + reservation.getReservationType());

        } catch (PartnerNotFoundException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoReservationFoundException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doViewAllPartnerReservation(Scanner sc) {

        System.out.println("Please enter your email: ");
        System.out.print(">");
        String email = sc.nextLine();
        System.out.println("Please enter your password: ");
        System.out.print(">");
        String password = sc.nextLine();

        try {
            List<ReservationEntity> reservations = viewAllPartnerReservations(email, password);
            for (ReservationEntity reservation : reservations) {
                System.out.println("Reservation ID: " + reservation.getReservationId());
            }
        } catch (NoReservationFoundException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PartnerNotFoundException_Exception ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void doPartnerLogout() {

        partner = null;

    }

    private static BigDecimal calculateTotalPrice(java.util.List<ws.session.holiday.RoomTypeEntity> roomTypes, int nights) {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.calculateTotalPrice(roomTypes, nights);
    }

    private static java.util.List<ws.session.holiday.RoomTypeEntity> getAvailableRoomTypes(javax.xml.datatype.XMLGregorianCalendar checkInDate) {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.getAvailableRoomTypes(checkInDate);
    }

    private static PartnerEntity partnerLogin(java.lang.String email, java.lang.String password) throws PartnerNotFoundException_Exception {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.partnerLogin(email, password);
    }

    private static ReservationEntity partnerReserveRoom(java.lang.String email, java.lang.String password, javax.xml.datatype.XMLGregorianCalendar checkInDate, javax.xml.datatype.XMLGregorianCalendar checkOutDate, java.util.List<ws.session.holiday.RoomTypeEntity> desiredRoomTypes, int nights) throws PartnerNotFoundException_Exception {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.partnerReserveRoom(email, password, checkInDate, checkOutDate, desiredRoomTypes, nights);
    }

    private static java.util.List<ws.session.holiday.RoomTypeEntity> partnerSearchRoom(java.lang.String email, java.lang.String password, javax.xml.datatype.XMLGregorianCalendar checkInDate, javax.xml.datatype.XMLGregorianCalendar checkOutDate) throws PartnerNotFoundException_Exception {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.partnerSearchRoom(email, password, checkInDate, checkOutDate);
    }

    private static java.util.List<ws.session.holiday.ReservationEntity> viewAllPartnerReservations(java.lang.String email, java.lang.String password) throws PartnerNotFoundException_Exception, NoReservationFoundException_Exception {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.viewAllPartnerReservations(email, password);
    }

    private static ReservationEntity viewPartnerReservationDetails(java.lang.String email, java.lang.String password, java.lang.Long reservationId) throws PartnerNotFoundException_Exception, NoReservationFoundException_Exception {
        ws.session.holiday.HoRSWebService_Service service = new ws.session.holiday.HoRSWebService_Service();
        ws.session.holiday.HoRSWebService port = service.getHoRSWebServicePort();
        return port.viewPartnerReservationDetails(email, password, reservationId);
    }




}
