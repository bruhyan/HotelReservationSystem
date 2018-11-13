/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import Entity.EmployeeEntity;
import Entity.GuestRelationOfficer;
import Entity.OperationManager;
import Entity.RoomEntity;
import Entity.RoomRatesEntity;
import Entity.RoomTypeEntity;
import Entity.SalesManager;
import Entity.SystemAdministrator;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.RateType;

/**
 *
 * @author mdk12
 */
@Singleton
@LocalBean
@Startup

public class InitialDataSessionBean {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        EmployeeEntity e = em.find(EmployeeEntity.class, 1l);
        RoomRatesEntity r1 = em.find(RoomRatesEntity.class, 1l);
        RoomTypeEntity r2 = em.find(RoomTypeEntity.class, 1l);
        RoomEntity r3 = em.find(RoomEntity.class, 1l);

        if (e == null) {
            initialiseEmployeeData();
        }

        if (r1 == null) {
            initialiseRoomRates();
        }

        if (r2 == null) {
            initialiseRoomType();
            setRoomTypesToRoomRates();
        }

        if (r3 == null) {
            initialiseRooms();
        }

    }

    public void initialiseEmployeeData() {
        //String name, String contactNumber, String email, String password, String address
        SystemAdministrator e1 = new SystemAdministrator("System Admin", "1", "90001000", "1", "NUS Computing");
        OperationManager e2 = new OperationManager("Operation Manager", "2", "91234567", "1", "Merlion Hotel");
        SalesManager e3 = new SalesManager("Sales Manager", "3", "99999999", "1", "Merlion Hotel");
        GuestRelationOfficer e4 = new GuestRelationOfficer("Guest Officer", "4", "62353535", "1", "Merlion Hotel");

        em.persist(e1);
        em.persist(e2);
        em.persist(e3);
        em.persist(e4);
        em.flush();
    }

    public void initialiseRoomRates() {
        //String name, BigDecimal ratePerNight, Date validityStart, Date validityEnd, RateType rateType

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

        //valid period
        Date date1 = new Date(2018, 11, 10);
        Date date2 = new Date(2018, 12, 25);

        //expired
        Date date3 = new Date(2018, 02, 10);
        Date date4 = new Date(2018, 04, 12);

        //haven't start
        Date date5 = new Date(2019, 01, 01);
        Date date6 = new Date(2019, 02, 02);

        List<RoomRatesEntity> roomRates = new ArrayList<>();

        //valid published, remember possible to have null.
        roomRates.add(new RoomRatesEntity("Published Valid Test Rate 1", BigDecimal.valueOf(50.00), date1, date2, RateType.PUBLISHED));
        roomRates.add(new RoomRatesEntity("Published Valid Test Rate 2", BigDecimal.valueOf(150.00), date1, date2, RateType.PUBLISHED));
        roomRates.add(new RoomRatesEntity("Published Valid Test Rate 3", BigDecimal.valueOf(500.00), date1, date2, RateType.PUBLISHED));
        roomRates.add(new RoomRatesEntity("Published Valid Test Rate 4", BigDecimal.valueOf(5000.00), date1, date2, RateType.PUBLISHED));

        //invalid published
        roomRates.add(new RoomRatesEntity("Published Invalid Test Rate 5", BigDecimal.valueOf(150.00), date3, date4, RateType.PUBLISHED));
        roomRates.add(new RoomRatesEntity("Published Invalid Test Rate 6", BigDecimal.valueOf(5000.00), date5, date6, RateType.PUBLISHED));
        //6

        //normal
        //valid normal, remember possible to have null.
        roomRates.add(new RoomRatesEntity("Normal Valid Test Rate 1", BigDecimal.valueOf(50.00), date1, date2, RateType.NORMAL));
        roomRates.add(new RoomRatesEntity("Normal Valid Test Rate 2", BigDecimal.valueOf(150.00), date1, date2, RateType.NORMAL));
        roomRates.add(new RoomRatesEntity("Normal Valid Test Rate 3", BigDecimal.valueOf(500.00), date1, date2, RateType.NORMAL));
        roomRates.add(new RoomRatesEntity("Normal Valid Test Rate 4", BigDecimal.valueOf(5000.00), date1, date2, RateType.NORMAL));

        //invalid normal
        roomRates.add(new RoomRatesEntity("Normal Invalid Test Rate 5", BigDecimal.valueOf(150.00), date3, date4, RateType.NORMAL));
        roomRates.add(new RoomRatesEntity("Normal Invalid Test Rate 6", BigDecimal.valueOf(5000.00), date5, date6, RateType.NORMAL));
        //12

        //peak
        //valid peak, remember possible to have null.
        roomRates.add(new RoomRatesEntity("Peak Valid Test Rate 1", BigDecimal.valueOf(50.00), date1, date2, RateType.PEAK));
        roomRates.add(new RoomRatesEntity("Peak Valid Test Rate 2", BigDecimal.valueOf(150.00), date1, date2, RateType.PEAK));
        roomRates.add(new RoomRatesEntity("Peak Valid Test Rate 3", BigDecimal.valueOf(500.00), date1, date2, RateType.PEAK));
        roomRates.add(new RoomRatesEntity("Peak Valid Test Rate 4", BigDecimal.valueOf(5000.00), date1, date2, RateType.PEAK));

        //invalid peak
        roomRates.add(new RoomRatesEntity("Peak Invalid Test Rate 5", BigDecimal.valueOf(150.00), date3, date4, RateType.PEAK));
        roomRates.add(new RoomRatesEntity("Peak Invalid Test Rate 6", BigDecimal.valueOf(5000.00), date5, date6, RateType.PEAK));
        //18

        //promo
        //valid Promo, remember possible to have null.
        roomRates.add(new RoomRatesEntity("Promo Valid Test Rate 1", BigDecimal.valueOf(50.00), date1, date2, RateType.PROMOTIONAL));
        roomRates.add(new RoomRatesEntity("Promo Valid Test Rate 2", BigDecimal.valueOf(150.00), date1, date2, RateType.PROMOTIONAL));
        roomRates.add(new RoomRatesEntity("Promo Valid Test Rate 3", BigDecimal.valueOf(500.00), date1, date2, RateType.PROMOTIONAL));
        roomRates.add(new RoomRatesEntity("Promo Valid Test Rate 4", BigDecimal.valueOf(5000.00), date1, date2, RateType.PROMOTIONAL));

        //invalid Promo
        roomRates.add(new RoomRatesEntity("Promo Invalid Test Rate 5", BigDecimal.valueOf(150.00), date3, date4, RateType.PROMOTIONAL));
        roomRates.add(new RoomRatesEntity("Promo Invalid Test Rate 6", BigDecimal.valueOf(5000.00), date5, date6, RateType.PROMOTIONAL));
        //24

        for (RoomRatesEntity roomRate : roomRates) {
            em.persist(roomRate);
            em.flush(); //for the right ordering
        }

    }

    public void initialiseRoomType() {
        //String roomName, String description, Integer size, String bed, String amenities, Integer capacity
        List<RoomTypeEntity> roomTypes = new ArrayList<>();

        //published and normal
        roomTypes.add(new RoomTypeEntity("Published And Normal $50", "Should apply either", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Published And Normal $150", "Should apply either", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Published And Normal $500", "Should apply either", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Published And Normal $5000", "Should apply either", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Published And Normal $500", "Should apply either", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Published And Normal $5000", "Should apply either", 2, "3 double size", "Free air", 5));

        //Normal And Promo
        roomTypes.add(new RoomTypeEntity("Normal And Promo $50", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal And Promo $150", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal And Promo $500", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal And Promo $5000", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal And Promo $500", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal And Promo $5000", "Should apply promo", 2, "3 double size", "Free air", 5));
        //12
        //Normal And Peak
        roomTypes.add(new RoomTypeEntity("Normal and peak $50", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal and peak $150", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal and peak $500", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal and peak $5000", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal and peak $500", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal and peak $5000", "Should apply peak", 2, "3 double size", "Free air", 5));
        //18
        //Promo And Peak
        roomTypes.add(new RoomTypeEntity("Promo and peak $50", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Promo and peak $150", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Promo and peak $500", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Promo and peak $5000", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Promo and peak $500", "Should apply peak", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Promo and peak $5000", "Should apply peak", 2, "3 double size", "Free air", 5));
        //24
        //Normal Promo And Peak
        roomTypes.add(new RoomTypeEntity("Normal, promo and peak $50", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal, promo and peak $150", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal, promo and peak $500", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Normal, promo and peak $5000", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal, promo and peak $500", "Should apply promo", 2, "3 double size", "Free air", 5));
        roomTypes.add(new RoomTypeEntity("Invalid Normal, promo and peak $5000", "Should apply promo", 2, "3 double size", "Free air", 5));
        //30

        for (RoomTypeEntity roomType : roomTypes) {
            em.persist(roomType);
            em.flush();//for the right ordering
        }
    }

    public void setRoomTypesToRoomRates() {

        //Settle the first rule, Published And Normal, both valid and invalid
        for (int i = 1; i <= 6; i++) {
            RoomTypeEntity roomType = em.find(RoomTypeEntity.class, Long.valueOf(i));
            RoomRatesEntity roomRatePublished = em.find(RoomRatesEntity.class, Long.valueOf(i));
            roomType.getRoomRateList().add(roomRatePublished);
            roomRatePublished.getRoomTypeList().add(roomType);

            RoomRatesEntity roomRateNormal = em.find(RoomRatesEntity.class, Long.valueOf(i + 6));
            roomType.getRoomRateList().add(roomRateNormal);
            roomRateNormal.getRoomTypeList().add(roomType);

        }

        //Normal and promo
        for (int i = 7; i <= 12; i++) {
            RoomTypeEntity roomType = em.find(RoomTypeEntity.class, Long.valueOf(i));
            RoomRatesEntity roomRateNormal = em.find(RoomRatesEntity.class, Long.valueOf(i));
            roomType.getRoomRateList().add(roomRateNormal);
            roomRateNormal.getRoomTypeList().add(roomType);

            RoomRatesEntity roomRatePromo = em.find(RoomRatesEntity.class, Long.valueOf(i + 12));
            roomType.getRoomRateList().add(roomRatePromo);
            roomRatePromo.getRoomTypeList().add(roomType);
        }

        //Normal and Peak
        for (int i = 13; i <= 18; i++) {
            RoomTypeEntity roomType = em.find(RoomTypeEntity.class, Long.valueOf(i));

            RoomRatesEntity roomRatePeak = em.find(RoomRatesEntity.class, Long.valueOf(i));
            roomType.getRoomRateList().add(roomRatePeak);
            roomRatePeak.getRoomTypeList().add(roomType);

            RoomRatesEntity roomRateNormal = em.find(RoomRatesEntity.class, Long.valueOf(i - 6));
            roomType.getRoomRateList().add(roomRateNormal);
            roomRateNormal.getRoomTypeList().add(roomType);
        }

        //Promo and Peak
        for (int i = 19; i <= 24; i++) {
            RoomTypeEntity roomType = em.find(RoomTypeEntity.class, Long.valueOf(i));

            RoomRatesEntity roomRatePromo = em.find(RoomRatesEntity.class, Long.valueOf(i));
            roomType.getRoomRateList().add(roomRatePromo);
            roomRatePromo.getRoomTypeList().add(roomType);
            
            RoomRatesEntity roomRatePeak = em.find(RoomRatesEntity.class, Long.valueOf(i - 6));
            roomType.getRoomRateList().add(roomRatePeak);
            roomRatePeak.getRoomTypeList().add(roomType);
        }

        //Normal, Promo, Peak
        for (int i = 25; i <= 30; i++) {
            RoomTypeEntity roomType = em.find(RoomTypeEntity.class, Long.valueOf(i));

            RoomRatesEntity roomRatePeak = em.find(RoomRatesEntity.class, Long.valueOf(i - 12));
            roomType.getRoomRateList().add(roomRatePeak);
            roomRatePeak.getRoomTypeList().add(roomType);

            RoomRatesEntity roomRateNormal = em.find(RoomRatesEntity.class, Long.valueOf(i - 18));
            roomType.getRoomRateList().add(roomRateNormal);
            roomRateNormal.getRoomTypeList().add(roomType);

            RoomRatesEntity roomRatePromo = em.find(RoomRatesEntity.class, Long.valueOf(i - 6));
            roomType.getRoomRateList().add(roomRatePromo);
            roomRatePromo.getRoomTypeList().add(roomType);

        }

    }

    public void initialiseRooms() {
        //Integer roomNumber, RoomTypeEntity roomType
        //every room got one room type
        Query query = em.createQuery("SELECT r FROM RoomTypeEntity r");

        List<RoomTypeEntity> roomTypes = query.getResultList();

        int index = 1;
        for (RoomTypeEntity roomType : roomTypes) {
            if (index == 31) {
                break;
            }
            RoomEntity room = new RoomEntity(index, roomType);
            roomType.getRoomList().add(room);
            em.persist(room);
            em.flush();
            index++;
        }

    }

}