/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.RoomEntity;
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.RoomStatus;

/**
 *
 * @author mdk12
 */
@Remote
public interface RoomControllerRemote {

    public List<RoomEntity> retrieveRoomListByTypeId(Long roomTypeId);

    public void createNewRoom(RoomEntity room);

    public List<RoomEntity> retrieveRoomList();

    public RoomEntity retrieveRoomById(Long id);

    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long bookingId, long roomTypeId);

    public RoomEntity heavyUpdateRoom(Long id, int roomNumber, RoomStatus newRoomStatus, long roomTypeId);

    public void deleteRoomById(Long id);
    
    public boolean checkAvailabilityOfRoomByRoomTypeId(Long RoomTypeId);
        
    public RoomEntity allocateRoom(Long roomTypeId);

    public RoomEntity walkInAllocateRoom(Long roomTypeId);

    public void changeRoomStatus(Long roomEntityId, RoomStatus status);
    
}
