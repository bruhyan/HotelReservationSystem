/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import Entity.BookingEntity;
import Entity.EmployeeEntity;
import Entity.RoomEntity;
import Entity.RoomRatesEntity;
import Entity.RoomTypeEntity;
import ejb.session.stateless.BookingControllerRemote;
import ejb.session.stateless.EmployeeControllerRemote;
import ejb.session.stateless.RoomControllerRemote;
import ejb.session.stateless.RoomRateControllerRemote;
import ejb.session.stateless.RoomTypeControllerRemote;
import java.util.List;
import java.util.Scanner;
import util.enumeration.RoomStatus;

/**
 *
 * @author mdk12
 */
public class OperationManagerModule {

    private EmployeeEntity loggedInUser;
    private EmployeeControllerRemote employeeControllerRemote;
    private RoomRateControllerRemote roomRateControllerRemote;
    private RoomControllerRemote roomControllerRemote;
    private RoomTypeControllerRemote roomTypeControllerRemote;
    private BookingControllerRemote bookingControllerRemote;

    public OperationManagerModule(EmployeeEntity loggedInUser, EmployeeControllerRemote employeeControllerRemote, RoomControllerRemote roomControllerRemote, RoomRateControllerRemote roomRateControllerRemote, RoomTypeControllerRemote roomTypeControllerRemote, BookingControllerRemote bookingControllerRemote) {
        this.loggedInUser = loggedInUser;
        this.employeeControllerRemote = employeeControllerRemote;
        this.roomRateControllerRemote = roomRateControllerRemote;
        this.roomControllerRemote = roomControllerRemote;
        this.roomTypeControllerRemote = roomTypeControllerRemote;
        this.bookingControllerRemote = bookingControllerRemote;
    }

    public void runModule() {
        Scanner sc = new Scanner(System.in);
        int input = 0;
        while (true) {
            System.out.println("==== Welcome to the Operation Manager Module ====");

            System.out.println("1: Create new room");
            System.out.println("2: View all rooms");
            System.out.println("3: Create New Room Type");
            System.out.println("4: View All Room Types"); //here can view room type details first then  delete or update also
            System.out.println("5: View Room Allocation Exception Report");
            System.out.println("6: Exit");
            input = 0;
            while (input < 1 || input > 5) {
                System.out.print(">");
                input = sc.nextInt();
                 sc.nextLine();
                if (input == 1) {
                     doCreateNewRoom(sc);
                } else if (input == 2) {
                     doViewAllRooms(sc);
                } else if (input == 3) {
                    doCreateNewRoomType(sc);
                } else if (input == 4) {
                    doViewAllRoomTypes(sc);
                }else if(input == 5){
                    doViewRoomAllocationExceptionReport();
                }
                else if (input == 6) {
                    break;
                } else {
                    System.out.println("Invalid input! Please try again!");
                }

            }
            if (input == 5) {
                break;
            }
        }
    }
//  public RoomEntity(Integer roomNumber, RoomStatus roomStatus, RoomTypeEntity roomType) {

    public void doViewAllRooms(Scanner sc){
        List<RoomEntity> roomList = roomControllerRemote.retrieveRoomList();
        if(roomList.isEmpty()){
            System.out.println("You currently have no room available! Please create a new one first.");

        }else{
            System.out.println("Select the room you'd wish to view in more detail, update or delete :");
        for (RoomEntity room : roomList) {

            System.out.println(room.getRoomId() + ". Room number : " + room.getRoomNumber() + " .");

        }
        System.out.print(">");
        Long roomId = sc.nextLong();
        sc.nextLine();
        viewSingleRoom(roomId, sc);
        }
    }
    
    public void viewSingleRoom(Long roomId, Scanner sc){
        RoomEntity room = roomControllerRemote.retrieveRoomById(roomId);
      
        int response = 0;
        System.out.println("============= Selected Room Type Information:  ===========");
        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Room Status: " + String.valueOf(room.getRoomStatus()));
        System.out.println("Room Type: " + room.getRoomType());
        System.out.println("Room Booking: " + room.getBooking());
        System.out.println("Room disabled: " + room.getIsDisabled());


        System.out.println("==============================================================");
        while (response < 1 || response > 3) {
            System.out.println("Would you like to 1. Update 2. Delete 3. Exit ?");
            response = sc.nextInt();
            sc.nextLine();
            if (response == 1) {
                doUpdateRoom(room, sc); 
            } else if (response == 2) {
                doDeleteRoom(roomId);
            } else if (response == 3) {
                break;
            } else {
                System.out.println("Invalid response! Please try again.");
            }
        }
    }
    
    public void doUpdateRoom(RoomEntity room, Scanner sc){
    System.out.println("Enter new room number: ( previous: " + room.getRoomNumber() + " ) \n");
        System.out.print(">");
        int roomNumber = sc.nextInt();
        System.out.println("Set room status :  1. Available 2. Occupied 3. Reserved ( previous: " + String.valueOf(room.getRoomStatus()) + " ) \n");
        System.out.print(">");
        
        int roomStatus = 0;
        while(roomStatus < 1 || roomStatus > 3){
            roomStatus = sc.nextInt();
            if(roomStatus > 3 || roomStatus < 1){
                System.out.println("Invalid input! Please try again.");
            }
        }
        RoomStatus newRoomStatus = RoomStatus.values()[roomStatus-1];
        
        Long bookingId = selectBookingId(sc);
        
        Long roomTypeId = selectRoomType(sc);
        
        if(bookingId == null){
        RoomEntity newRoom = roomControllerRemote.heavyUpdateRoom(room.getRoomId(), roomNumber, newRoomStatus, roomTypeId);
        }else{
        RoomEntity newRoom = roomControllerRemote.heavyUpdateRoom(room.getRoomId(), roomNumber, newRoomStatus, bookingId, roomTypeId);
        }
        System.out.println("Room has been updated!");
    }
    
    public Long selectBookingId(Scanner sc){
        List<BookingEntity> bookingList = bookingControllerRemote.retrieveBookingList();
        if(bookingList.isEmpty()){
            System.out.println("You currently have no booking available! Please create a new one first.");
            System.out.println("No booking will be assigned for now.");

        }else{
            System.out.println("Select the booking you'd wish to assign to your room:");
        for (BookingEntity booking : bookingList) {

            System.out.println("Booking Id : " + booking.getBookingId() +" .");

        }
        System.out.print(">");
        Long bookingId = sc.nextLong();
        sc.nextLine();
        return bookingId;
        }
        return null; //throw exception later come back
    }
    
    public void doDeleteRoom(Long roomId){
    
    }
    public void doCreateNewRoom(Scanner sc){
         
        System.out.println("Enter Room Number: \n");
        System.out.print(">");
        int roomNumber = sc.nextInt();
        sc.nextLine();
        Long roomTypeId = selectRoomType(sc);
        RoomTypeEntity roomType = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
        RoomEntity newRoom = new RoomEntity(roomNumber, roomType);
        roomControllerRemote.createNewRoom(newRoom);
        System.out.println("New room with room number : " + newRoom.getRoomNumber() + " has been created!");
    }
    public void doViewRoomAllocationExceptionReport(){
        System.out.println("Placeholder for something going wrong with room allocation, ie not enough rooms. come back and solve this");
    }
    
    public Long selectRoomType(Scanner sc){
        List<RoomTypeEntity> roomTypeList = roomTypeControllerRemote.retrieveRoomTypeList();
        
    
        if(roomTypeList.isEmpty()){
            System.out.println("You currently have no room types available! Please create a new one first.");
            doCreateNewRoomType(sc);
            //Assign this new roomType to this room.
            RoomTypeEntity roomType = roomTypeControllerRemote.retrieveSingleRoomType();
            return roomType.getRoomTypeId();
        }else{
        System.out.println("Select room type to assign to the room: ");

         for (RoomTypeEntity roomType : roomTypeList) {

            System.out.println(roomType.getRoomTypeId() + ". Room type name : " + roomType.getRoomName() + " .");
              System.out.print(">");
        Long roomTypeId = sc.nextLong();
        sc.nextLine();
        return roomTypeId;

        }
        
      
        }
        return null; //throw exception next time, come back
        
    }
    
    public void doViewAllRoomTypes(Scanner sc) {
        
        List<RoomTypeEntity> roomTypeList = roomTypeControllerRemote.retrieveRoomTypeList();
        if(roomTypeList.isEmpty()){
            System.out.println("You currently have no room types available! Please create a new one first.");

        }else{
            System.out.println("Select the room type you'd wish to view in more detail, update or delete :");
        for (RoomTypeEntity roomType : roomTypeList) {

            System.out.println(roomType.getRoomTypeId() + ". Room type name : " + roomType.getRoomName() + " .");

        }
        System.out.print(">");
        Long roomTypeId = sc.nextLong();
        sc.nextLine();
        viewSingleRoomType(roomTypeId, sc);
        }
    }

    public void viewSingleRoomType(Long roomTypeId, Scanner sc) {
        RoomTypeEntity roomType = roomTypeControllerRemote.retrieveRoomTypeById(roomTypeId);
        int response = 0;
        System.out.println("============= Selected Room Type Information:  ===========");
        System.out.println("Room Type Name: " + roomType.getRoomName());
        System.out.println("Room Type description: " + roomType.getDescription());
        System.out.println("Room Type amenities: " + roomType.getAmenities());
        System.out.println("Room Type bed: " + roomType.getBed());
        System.out.println("Room Type capacity: " + roomType.getCapacity());
        System.out.println("Room Type Size: " + roomType.getSize() + " metres square.");
        System.out.println("Room Type disabled: " + roomType.isIsDisabled());

        System.out.println("==============================================================");
        while (response < 1 || response > 3) {
            System.out.println("Would you like to 1. Update 2. Delete 3. Exit ?");
            response = sc.nextInt();
            sc.nextLine();
            if (response == 1) {
                doUpdateRoomType(roomType, sc); //probably just print all the artibutes, only thing same is the ID
            } else if (response == 2) {
                doDeleteRoomType(roomTypeId);
            } else if (response == 3) {
                break;
            } else {
                System.out.println("Invalid response! Please try again.");
            }
        }

    }
    
    public void doUpdateRoomType(RoomTypeEntity roomType, Scanner sc){
        System.out.println("Enter new Room Type Name: ( previous: " + roomType.getRoomName() + " ) \n");
        System.out.print(">");
        String name = sc.nextLine();
        System.out.println("Enter new description: ( previous: " + roomType.getDescription() + " ) \n");
        System.out.print(">");
        String description = sc.nextLine();
        System.out.println("Enter new size of room type (whole square meters): ( previous: " + roomType.getSize() + " ) \n");
        System.out.print(">");
        Integer size = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter new Bed provided: ( previous: " + roomType.getBed() + " ) \n");
        System.out.print(">");
        String bed = sc.nextLine();
        System.out.println("Enter new Amenities provided: ( previous: " + roomType.getAmenities() + " ) \n");
        System.out.print(">");
        String amenities = sc.nextLine();
        System.out.println("Enter new room type capacity: ( previous: " + roomType.getCapacity() + " ) \n");
        System.out.print(">");
        Integer capacity = sc.nextInt();
        
        roomTypeControllerRemote.heavyUpdateRoom(roomType.getRoomTypeId(), name, description, size, bed, amenities, capacity);
        System.out.println("Room type has been updated!");
    }
    
    public void doDeleteRoomType(Long roomTypeId){
        roomTypeControllerRemote.deleteRoomTypeById(roomTypeId);
        System.out.println("Room type has been deleted.");
    }

    public void doCreateNewRoomType(Scanner sc) {
        sc.nextLine();
        System.out.println("Enter Room Type Name: \n");
        System.out.print(">");
        String name = sc.nextLine();
        System.out.println("Enter description: \n");
        System.out.print(">");
        String description = sc.nextLine();
        System.out.println("Enter size of room type (whole square meters): \n");
        System.out.print(">");
        Integer size = sc.nextInt();
        sc.nextLine();
        System.out.println("Enter Bed provided: \n");
        System.out.print(">");
        String bed = sc.nextLine();
        System.out.println("Enter Amenities provided: \n");
        System.out.print(">");
        String amenities = sc.nextLine();
        System.out.println("Enter room type capacity: \n");
        System.out.print(">");
        Integer capacity = sc.nextInt();

        Long roomRateId = chooseRoomRate(sc);
        sc.nextLine();

        RoomTypeEntity newRoomType = new RoomTypeEntity(name, description, size, bed, amenities, capacity);
        newRoomType = roomTypeControllerRemote.createNewRoomType(newRoomType);

   
        roomTypeControllerRemote.addRoomRateById(newRoomType.getRoomTypeId(), roomRateId);
        int response = 0;
        //add another roomrate here if needed
        while (true) {
            while (response < 1 || response > 2) {
                System.out.println("Add another room rate?");
                System.out.println("1. Yes");
                System.out.println("2. No, System will create the room type and return you to the operation manager page.");
                System.out.print(">");
                response = sc.nextInt();
                sc.nextLine();
                if (response == 1) {
                    addAnotherRoomRate(newRoomType, sc);
                } else if (response == 2) {
                    break;
                }
            }
            break;
        }

    }

    public void addAnotherRoomRate(RoomTypeEntity roomType, Scanner sc) {
        Long roomRateId = chooseRoomRate(sc);
        roomTypeControllerRemote.addRoomRateById(roomType.getRoomTypeId(), roomRateId);
    }

    public Long chooseRoomRate(Scanner sc) {
        List<RoomRatesEntity> roomRatesList = roomRateControllerRemote.retrieveRoomRatesList();
        int i = 1;

        System.out.println("Choose room rate for this room type : ");

        for (RoomRatesEntity roomRateEntity : roomRatesList) { //come back check for date validity next time.
            System.out.println(i + ". " + roomRateEntity.getName() + ", Room Rate : $" + roomRateEntity.getRatePerNight() + " Per Night");
            i++;
        }
        System.out.print(">");
        return sc.nextLong();
    }

}
