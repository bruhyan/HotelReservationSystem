/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.RoomEntity;
import Entity.RoomTypeEntity;
import java.util.Date;
import java.util.List;
import util.enumeration.RoomStatus;


public interface RoomControllerLocal {

    public List<RoomEntity> retrieveRoomListByType(RoomTypeEntity roomType);
    
    public List<RoomEntity> retrieveRoomListByTypeId(Long roomTypeId);

    public boolean checkAvailabilityOfRoomByRoomTypeId(Long RoomTypeId, Date checkInDate);

    public RoomEntity allocateRoom(Long roomTypeId);

    public void createNewRoom(RoomEntity room);

    public List<RoomEntity> retrieveRoomList();

    public RoomEntity retrieveRoomById(Long id);

    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long bookingId, long roomTypeId, boolean isDisabled);

    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long roomTypeId, boolean isDisabled);

    public void deleteRoomById(Long id);

    public RoomEntity walkInAllocateRoom(Long roomTypeId);

    public void changeRoomStatus(Long roomEntityId, RoomStatus status);
    
    public void deleteAllDisabledRooms();


    public boolean checkAvailabilityOfRoomTypeWhenAllocating(Long roomTypeId);
    
}
