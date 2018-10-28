/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.CustomerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Bryan
 */
@Stateless
public class CustomerController implements CustomerControllerRemote, CustomerControllerLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    public CustomerEntity createCustomerEntity(CustomerEntity cus) {
        em.persist(cus);
        em.flush();
        return cus;
    }
    
    public CustomerEntity retrieveCustomerEntityById(long customerId) {
        return em.find(CustomerEntity.class, customerId);  
    }
    
    public CustomerEntity retrieveCustomerEntityByContactNumber(String contactNum) {
        Query query = em.createQuery("SELECT c FROM CustomerEntity c WHERE c.contactNumber = :contactNum");
        query.setParameter("contactNum", contactNum);
        return (CustomerEntity)query.getSingleResult();
        
    }
}
