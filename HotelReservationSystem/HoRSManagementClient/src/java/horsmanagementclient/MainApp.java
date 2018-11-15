/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import Entity.EmployeeEntity;
import Entity.GuestRelationOfficer;
import Entity.OperationManager;
import Entity.RoomTypeEntity;
import Entity.RoomTypeRanking;
import Entity.SalesManager;
import Entity.SystemAdministrator;
import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.CustomerControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.PartnerControllerRemote;
import ejb.session.stateless.ReservationControllerRemote;
import ejb.session.stateless.RoomAllocationExceptionControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import ejb.session.stateless.RoomTypeRankingControllerRemote;
import ejb.session.stateless.SystemTimerSessionBeanRemote;
import ejb.session.stateless.TransactionControllerRemote;
import java.util.Scanner;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author Bryan
 */
public class MainApp {

    private EmployeeControllerRemote employeeControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private EmployeeEntity loggedInUser;
    private RoomRateControllerRemote roomRateControllerRemote;
    private BookingControllerRemote bookingControllerRemote;
    private PartnerControllerRemote partnerControllerRemote;
    private CustomerControllerRemote customerControllerRemote;
    private ReservationControllerRemote reservationControllerRemote;

    private RoomTypeRankingControllerRemote roomTypeRankingControllerRemote;
    private TransactionControllerRemote transactionControllerRemote;
    private RoomAllocationExceptionControllerRemote roomAllocationExceptionControllerRemote;
    private SystemTimerSessionBeanRemote systemTimerSessionBeanRemote;

    public MainApp() {
    }

    public MainApp(EmployeeControllerRemote employeeControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, BookingControllerRemote bookingControllerRemote, PartnerControllerRemote partnerControllerRemote, CustomerControllerRemote customerControllerRemote, ReservationControllerRemote reservationControllerRemote, SystemTimerSessionBeanRemote systemTimerSessionBeanRemote, RoomTypeRankingControllerRemote roomTypeRankingControllerRemote, TransactionControllerRemote transactionControllerRemote, RoomAllocationExceptionControllerRemote roomAllocationExceptionControllerRemote) {

        this();
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
        this.partnerControllerRemote = partnerControllerRemote;
        this.customerControllerRemote = customerControllerRemote;
        this.reservationControllerRemote = reservationControllerRemote;

        this.systemTimerSessionBeanRemote = systemTimerSessionBeanRemote;

        this.roomAllocationExceptionControllerRemote = roomAllocationExceptionControllerRemote;

        this.roomTypeRankingControllerRemote = roomTypeRankingControllerRemote;
        this.transactionControllerRemote = transactionControllerRemote;
    }

    public void test() {

//        RoomTypeRanking roomTypeRank = roomTypeRankingControllerRemote.getRoomTypeRanking();
//        int index = 1;
//        for(RoomTypeEntity roomType : roomTypeRank.getRoomTypes()){
//            
//            System.out.println(index + " room Type : " + roomType.getRoomTypeName());
//            index++;
//        }
//        
        // System.out.println(roomControllerRemote.checkAvailabilityOfRoomByRoomTypeId(1l));
//       List<RoomRatesEntity> roomRateList = roomTypeControllerRemote.findPricierRoomType(2l);
//       for(RoomRatesEntity roomRate : roomRateList){
//           System.out.println(roomRate.getName());
//       }
//    System.out.println(roomTypeControllerRemote.findPricierAvailableRoomType(2l).getRoomName());
//        BigDecimal rate = roomTypeControllerRemote.findPricierRoomType(1l);
//        System.out.println(rate);
//        List<RoomRatesEntity> roomRateList = roomRateControllerRemote.retrieveRoomRateListExcludeRoomType(1l);
//        for(RoomRatesEntity roomRate : roomRateList){
//           System.out.println(roomRate.getName());
//       }
//       System.out.println("It works");
//        List<RoomEntity> roomList = roomControllerRemote.retrieveRoomListByTypeId(7l);
//        for(RoomEntity room : roomList){
//            System.out.println(room.getRoomNumber());
//        }
    }

    public void runApp() {

        systemTimerSessionBeanRemote.init();

        Scanner sc = new Scanner(System.in);
        int input = 0;
        while (true) {
            System.out.println("==== Welcome to the HoRS Management Client ====");
            if (loggedInUser == null) {
                System.out.println("1: Employee Login");

                System.out.println("2: Exit");
            }

            if (loggedInUser != null) {
                System.out.println("1: Do your duties");
                System.out.println("2: Employee Logout");
                System.out.println("3: Exit");
                System.out.println("4: Simulate 2AM Room Allocation");
                System.out.println("5: Delete all disabled rooms");
                System.out.println("6: Delete all disabled roomtypes");
                System.out.println("7: Delete all disabled roomrates");

            } else {
                System.out.println("3: Simulate 2AM Room Allocation");
            }
            input = 0;
            if (loggedInUser == null) {
                input = 0;
                while (input < 1 || input > 2) {
                    System.out.println(">");
                    input = sc.nextInt();
                    if (input == 1) {
                        doLogin(sc);

                        //System.out.println("Employee Already Logged In !");
                    } else if (input == 2) {
                        break;
                    } else {
                        System.out.println("Invalid response, please try again!");
                    }
                }
            } else if (loggedInUser != null) {
                input = 0;
                while (input < 1 || input > 7) {
                    System.out.println(">");
                    input = sc.nextInt();
                    if (input == 1) {

                        doStreamEmployee();
                    } else if (input == 2) {
                        doLogout(sc);
                    } else if (input == 3) {
                        break;
                    } else if (input == 4) {
                        systemTimerSessionBeanRemote.roomAllocation();
                    } else if (input == 5) {
                        roomControllerRemote.deleteAllDisabledRooms();
                    } else if (input == 6) {
                        roomTypeControllerRemote.deleteAllDisabledRoomType();
                    } else if (input == 7) {
                        roomRateControllerRemote.deleteAllDisabledRoomRates();
                    } else {
                        System.out.println("Invalid response, please try again!");
                    }
                }

            }
            if (loggedInUser == null && input == 2 || loggedInUser != null && input == 3) {

                break;
            }

        }
    }

    public void doLogin(Scanner sc) {
        sc.nextLine();
        System.out.println("==== Employee Login Page ====");
        System.out.println("Enter email");
        String email = sc.nextLine();
        System.out.println("Enter password");
        String password = sc.nextLine();
        EmployeeEntity employee;
        try {
            employee = employeeControllerRemote.retrieveEmployeeByEmail(email);
            if (employee.getPassword().equals(password)) {
                System.out.println("Welcome " + employee.getName() + ".");
                loggedInUser = employee;
            } else {
                System.out.println("Incorrect Password !");
            }
        } catch (EmployeeNotFoundException ex) {
            System.out.println("Employee does not exist !");
        }

    }

    public void doLogout(Scanner sc) {
        System.out.println(loggedInUser.getName() + " has been logged out.");
        loggedInUser = null;
    }

    public void doStreamEmployee() {
        if (loggedInUser instanceof SystemAdministrator) {
            //System.out.println("Hello System Admin");
            SystemAdministrationModule systemAdministratorModule = new SystemAdministrationModule(loggedInUser, employeeControllerRemote, partnerControllerRemote);
            systemAdministratorModule.runModule();
        } else if (loggedInUser instanceof GuestRelationOfficer) {
            FrontOfficeModule frontOfficeModule = new FrontOfficeModule(loggedInUser, employeeControllerRemote, roomControllerRemote, roomTypeControllerRemote, customerControllerRemote, reservationControllerRemote, bookingControllerRemote, roomRateControllerRemote, transactionControllerRemote);
            frontOfficeModule.runModule();

        } else if (loggedInUser instanceof OperationManager) {
            HotelOperationModule hotelOperationModule = new HotelOperationModule(loggedInUser, employeeControllerRemote, roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote, bookingControllerRemote, roomTypeRankingControllerRemote, roomAllocationExceptionControllerRemote);
            hotelOperationModule.runOperationManagerModuleModule();
        } else if (loggedInUser instanceof SalesManager) {
            HotelOperationModule hotelOperationModule = new HotelOperationModule(loggedInUser, employeeControllerRemote, roomControllerRemote, roomRateControllerRemote, roomTypeControllerRemote, bookingControllerRemote, roomTypeRankingControllerRemote, roomAllocationExceptionControllerRemote);
            hotelOperationModule.runSalesManagerModule();
        } else {
            System.out.println("This employee type does not exist!");
        }
    }

}
