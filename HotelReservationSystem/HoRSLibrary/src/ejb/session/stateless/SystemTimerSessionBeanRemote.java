/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import javax.ejb.Remote;
import javax.ejb.Timer;

/**
 *
 * @author mdk12
 */
@Remote
public interface SystemTimerSessionBeanRemote {

    public void init();

    public void roomAllocation();
    
}
