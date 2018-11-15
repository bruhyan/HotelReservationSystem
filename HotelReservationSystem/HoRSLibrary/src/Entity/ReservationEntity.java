/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlTransient;
import util.enumeration.ReservationType;

/**
 *
 * @author mdk12
 */
@Entity
public class ReservationEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    @Column(nullable = false)
    private Date dateOfReservation;
    @Column(nullable = false)
    private Date checkInDateTime;
    @Column(nullable = false)
    private Date checkOutDateTime;
    @Column(nullable = false)
    private Boolean showedUp;
    @OneToMany(mappedBy = "reservation")
    private List<BookingEntity> bookingList;
    @ManyToOne
    private CustomerEntity customer;
    @ManyToOne
    private PartnerEntity partner;
    @OneToOne
    private TransactionEntity transaction;
    private Integer numOfGuests;
    @Column(nullable = false)
    private ReservationType reservationType;

    public ReservationEntity() {
        this.bookingList = new ArrayList<>();
    }

    //use this if customer reservation
    public ReservationEntity(Date dateOfReservation, Date checkInDateTime, Date checkOutDateTime, Boolean showedUp, CustomerEntity customer, ReservationType reservationType) {
        this();
        this.dateOfReservation = dateOfReservation;
        this.checkInDateTime = checkInDateTime;
        this.checkOutDateTime = checkOutDateTime;
        this.showedUp = showedUp;
        this.customer = customer;
        this.reservationType = reservationType;
        //this.transaction = transaction;
        //this.numOfGuests = numOfGuests;
    }

    //use this if partner reservation
    public ReservationEntity(Date dateOfReservation, Date checkInDateTime, Date checkOutDateTime, Boolean showedUp, PartnerEntity partner, ReservationType reservationType) {
        this.dateOfReservation = dateOfReservation;
        this.checkInDateTime = checkInDateTime;
        this.checkOutDateTime = checkOutDateTime;
        this.showedUp = showedUp;
        this.partner = partner;
        this.reservationType = reservationType;
    }

    public void addBooking(BookingEntity booking) {
        this.bookingList.add(booking);
    }


    public Date getDateOfReservation() {
        return dateOfReservation;
    }

    public void setDateOfReservation(Date dateOfReservation) {
        this.dateOfReservation = dateOfReservation;
    }

    //@XmlTransient
    public Date getCheckInDateTime() {
        return checkInDateTime;
    }

    public void setCheckInDateTime(Date checkInDateTime) {
        this.checkInDateTime = checkInDateTime;
    }


    public Date getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public void setCheckOutDateTime(Date checkOutDateTime) {
        this.checkOutDateTime = checkOutDateTime;
    }


    public Boolean getShowedUp() {
        return showedUp;
    }

    public void setShowedUp(Boolean showedUp) {
        this.showedUp = showedUp;
    }

    @XmlTransient
    public List<BookingEntity> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<BookingEntity> bookingList) {
        this.bookingList = bookingList;
    }

    @XmlTransient
    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    @XmlTransient
    public TransactionEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionEntity transaction) {
        this.transaction = transaction;
    }

    public Integer getNumOfGuests() {
        return numOfGuests;
    }

    public void setNumOfGuests(Integer numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof ReservationEntity)) {
            return false;
        }
        ReservationEntity other = (ReservationEntity) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entity.ReservationEntity[ id=" + reservationId + " ]";
    }


    public ReservationType getReservationType() {
        return reservationType;
    }

    public void setReservationType(ReservationType reservationType) {
        this.reservationType = reservationType;
    }

    public PartnerEntity getPartner() {
        return partner;
    }

    public void setPartner(PartnerEntity partner) {
        this.partner = partner;
    }

}
