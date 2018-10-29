/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import Entity.CustomerEntity;
import javax.ejb.Remote;

/**
 *
 * @author Bryan
 */
@Remote
public interface CustomerControllerRemote {

    public CustomerEntity createCustomerEntity(CustomerEntity cus);

    public CustomerEntity retrieveCustomerEntityById(long customerId);

    public CustomerEntity retrieveCustomerEntityByContactNumber(String contactNum);
    
}