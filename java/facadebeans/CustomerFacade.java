/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facadebeans;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import webservices.Customer;

/**
 *
 * @author David_killa
 */
@Stateless
public class CustomerFacade extends AbstractFacade<Customer> 
{
    @PersistenceContext(unitName = "webservicePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() 
    {
        return em;
    }

    public CustomerFacade() 
    {
        super(Customer.class);
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(Customer entity) 
    {
        super.create(entity); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void edit(Customer entity) 
    {
        Customer  temp = this.find(entity.getCustomerId());
        entity.setCustomerId(temp.getCustomerId());
        super.edit(entity);  //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Customer> findAllByOwner(String owner) 
    {
        List<Customer> temp_book = null;
        try 
        {
            temp_book = (List<Customer>)em.createNamedQuery("Customer.findAllByOwner").setParameter("owner", owner).getResultList();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }

    public long checkCustomerDetailsExist(Customer customer) 
    {
        Customer temp_customer = null;
        try 
        {
                temp_customer = (Customer)em.createNamedQuery("Customer.findExistingCustomer")
                .setParameter("name", customer.getName())
                .setParameter("addressline1", customer.getAddressline1())
                .setParameter("addressline2", customer.getAddressline2())
                .setParameter("city", customer.getCity())
                .setParameter("province", customer.getProvince())
                .setParameter("country", customer.getCountry())
                .setParameter("email", customer.getEmail()).getSingleResult();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        if(temp_customer != null)return temp_customer.getCustomerId();
        return -1;
    }
}
