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
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.RateType;
import util.exception.NoAvailableOnlineRoomRateException;


public interface RoomTypeControllerRemote {

    public RoomTypeEntity createNewRoomType(RoomTypeEntity roomType);

    public void addRoomRateById(Long roomTypeId, Long roomRateId);

    public List<RoomTypeEntity> retrieveRoomTypeList();

    public RoomTypeEntity retrieveRoomTypeById(long id);

    public void deleteRoomTypeById(long id);

    public RoomTypeEntity heavyUpdateRoom(long id, String name, String description, int size, String bed, String amenities, int capacity);

    public RoomTypeEntity retrieveSingleRoomType();

    public List<RoomEntity> retrieveRoomEntityByRoomType(RoomTypeEntity roomType);

    public List<RoomRatesEntity> retrieveRoomRateListById(Long roomTypeId);

    //public RoomTypeEntity findPricierAvailableRoomTypeForWalkIn(Long roomTypeId);
    
    public List<RoomTypeEntity> retrieveRoomTypeListByRates(RoomRatesEntity roomRates);

    public List<RoomTypeEntity> retrieveRoomTypesByRateType(RateType rateType);

    public RoomRatesEntity findOnlineRateForRoomType(Long roomTypeId) throws NoAvailableOnlineRoomRateException;

    public void removeRoomRate(Long roomTypeId, Long roomRateId);

    public List<RoomTypeEntity> retrieveRoomTypeByRanking();

    public void updateRoomRank(int rank, Long roomTypeId);

    public void deleteAllDisabledRoomType();
}
