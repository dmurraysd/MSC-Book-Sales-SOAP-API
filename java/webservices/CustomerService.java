/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import exceptions.WSException;
import facadebeans.CustomerFacade;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author David_killa
 */
@DeclareRoles({"administrator", "developer","manager"})
/*@RolesAllowed({"administrator", "developer"})
 * Could above this to stop priviligies for non developer and non-manager roles 
 * but would be unable to throw customised exception, authorization at method level
 */
@WebService(serviceName = "webservice")
@Stateless()
public class CustomerService 
{

    @Resource
    private WebServiceContext service_ctx;
    
    @EJB
    private CustomerFacade customer_facade;
    
    private static final Logger LOG = Logger.getLogger(BookService.class.getName());
    private Customer local_customer;
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createCustomer")
    public String createCustomer(@WebParam(name = "entity") Customer entity) throws WSException 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to add customers");
        
        /*check for null entity*/
        if(entity == null)throw new WSException("401","**No data in request**");
        
        /*check entity entries are valid*/
        this.validateData(entity);
        
        //Check for existing customers with same name, addresses and email
        long result = customer_facade.checkCustomerDetailsExist(entity);
        if(result != -1)throw new WSException("401","**Customer already extists**"); 
        
        try 
        {
            /*set owner of this customer to developer - else if user is administrator, no need for name*/
            if(this.service_ctx.isUserInRole("developer"))entity.setOwner(this.service_ctx.getUserPrincipal().getName());
            else entity.setOwner("administrator");
            customer_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new WSException(ex);
        }
        return "**Customer created successfully";
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "editCustomer")
    public String editCustomer(@WebParam(name = "entity") Customer entity) throws WSException 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to edit customers");
        
        /*check for null entity*/
        if(entity == null)throw new WSException("401","**No data in request**");
        
        /*Check if entity data is valid*/
        this.validateData(entity);
        
        /*Check if current user created this customer - only for developer role - administrator can edit any customer*/
        if(!validateOwnerSearch(entity.getCustomerId()))
                throw new WSException("401", "**Customer to be edited not found");
        
        /*
             * Check if the edited details are the same as another customer which is in the database already
             * checkCustomerDetailsExist returns ID of duplicate customer, just in case it is the same entity be edited
             * if result is -1, there is no duplicate details.
             * if the result is ID of customer been edited, edit is allowed
             * if result has a different ID, edit can't be allowed
             */
        long result = customer_facade.checkCustomerDetailsExist(entity);
        if(result != -1 && result != entity.getCustomerId())throw new WSException("410","**Customer already exists with edited details**"); 
        try 
        {
            /*set owner of this customer to developer - else if user is administrator, no need for name - 
             * this also prevents ilegal user trying to get priviligies to a customer only available to administrator
             * by editing the owner role
             */
            if(this.service_ctx.isUserInRole("developer"))entity.setOwner(this.service_ctx.getUserPrincipal().getName());
            else entity.setOwner("administrator");
            customer_facade.edit(entity);
        }catch (Exception ex) 
        {

            throw new WSException(ex);
            //throw new Exception(ex);
        }
        return "**Customer successfully edited**";
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeCustomer")
    public String removeCustomer(@WebParam(name = "id") Integer id) throws WSException
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to remove customers");
        
        /*
         * Check if user has the authority for this particular customer
         * and if the customer exists
         */
        if(!validateOwnerSearch(id))throw new WSException("401","**Customer not found**");
        
        customer_facade.remove(local_customer);
        
        return "**Customer successfully removed";
    }
    
     /**
     * Web service operation
     */
    @WebMethod(operationName = "findCustomer")
    public Customer findCustomer(@WebParam(name = "id") Integer id) throws WSException
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to view customers");
        
        /*
         * Check if user has the authority for this particular customer
         * and if the customer exists
         */
        if(validateOwnerSearch(id))return local_customer;
        
        throw new WSException("401", "**Customer not found**");
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findAll")
    public List<Customer> findAll() throws WSException 
    {
        if(!this.service_ctx.isUserInRole("administrator"))
            return customer_facade.findAllByOwner(this.service_ctx.getUserPrincipal().getName());
        
        return customer_facade.findAll();
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCustomerCount")
    public String getCustomerCount() throws WSException 
    {
        return String.valueOf(customer_facade.count());
    }
    
    //////////////////////////////*****************Non web service methods - Not exposed to the client*******************\\\\\\\\\\\\\\\\
    
    /*Check if the current user has the privilige to access a customer in the database*/
    private boolean validateOwnerSearch(Integer id) throws WSException 
    {
        
        if(id == null)throw new WSException("401","**Incomplete customer ID entered**");
        local_customer = customer_facade.find(id);
        if(local_customer == null)return false;
        
        if(!local_customer.getOwner().equals(this.service_ctx.getUserPrincipal().getName()) 
                && !this.service_ctx.isUserInRole("administrator"))return false;
        
        return true;
    }
    
   
    /*Check if not null variables are null*/
    private void validateData(Customer entity) throws WSException
    {
        if("".equals(entity.getName()))throw new WSException("401","<->No name entered<->");
        if("".equals(entity.getAddressline1()) || "".equals(entity.getAddressline2()))throw new WSException("401","<->Incomplete Address entered<->");
        if("".equals(entity.getCity()))throw new WSException("401","<->No city entered<->");
        if("".equals(entity.getCountry()))throw new WSException("401","<->No country entered<->");
        if("".equals(entity.getEmail()))throw new WSException("401","<->No email Address entered<->");
        if("".equals(entity.getProvince()))throw new WSException("401","<->Province not entered<->");
    }

    

    
}/*end of class*/
