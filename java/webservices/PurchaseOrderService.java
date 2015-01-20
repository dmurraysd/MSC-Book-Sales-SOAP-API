/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import entities.PurchaseOrder;
import exceptions.WSException;
import facadebeans.BooksFacade;
import facadebeans.CustomerFacade;
import facadebeans.PurchaseOrderFacade;
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
public class PurchaseOrderService 
{
    
    @Resource
    private WebServiceContext service_ctx;
    
    @EJB
    private PurchaseOrderFacade order_facade;
    
    @EJB 
    private BooksFacade books_facade;
    
    @EJB
    private CustomerFacade customer_facade;
    
    private static final Logger LOG = Logger.getLogger(BookService.class.getName());
    
    private Customer local_customer;
    private PurchaseOrder local_order;
    private Books local_book;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createOrder")
    public String createOrder(@WebParam(name = "name") PurchaseOrder entity) throws WSException 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to create orders");
        
        //Check if entity data is valid
        this.checkEntityData(entity);
        
        //check if book exists 
        local_book = books_facade.findByIsbn(entity.getIsbn());
        if(local_book == null)throw new WSException("401","Book not found");
        
        
        //check if customer exists 
        local_customer = customer_facade.find(entity.getCustomer_id());
        if(local_customer == null)throw new WSException("401","Customer not found");
        
        //check if it is a valid request
        if(entity.getQuantity() <=0)throw new WSException("401","Quantity requested set at 0");
        
        //check if the book is available
        if(local_book.getQuantity() <= 0)throw new WSException("401","Store Quantity at 0");
        
        //check if enough copies are available to satisfy oder
        if(entity.getQuantity() > local_book.getQuantity())throw new WSException("401","Excess quantity requested");
        
        //set the owner of the order
        if(!this.service_ctx.isUserInRole("administrator"))entity.setOwner(this.service_ctx.getUserPrincipal().getName());
        else entity.setOwner("administrator");
        
        try 
        {
            //set the id for easy search if required, for order processors
            entity.setBookid(local_book.getBookid());
            
            order_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new WSException(ex);
        }
        local_book.setQuantity(local_book.getQuantity() - entity.getQuantity());
        return "**Order successfully created**";
    }
    
     /**
     * Web service operation
     */
    @WebMethod(operationName = "editOrder")
    public String editOrder(@WebParam(name = "order") PurchaseOrder entity) throws WSException 
    {
       /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to create orders");
        
        //Check if entity data is valid
        this.checkEntityData(entity);
        
        //Validate entity order number
        if(entity.getOrderNum() == null)throw new WSException("401","Incomplete order number entered");
        local_order = order_facade.find(entity.getOrderNum());
        if(local_order == null)throw new WSException("401","Order not found");
        
        //check if order has been processed and probably sent
        if(local_order.isProcessed() == 1)throw new WSException("401","Order already processed");
        
        //check if book exists 
        local_book = books_facade.findByIsbn(entity.getIsbn());
        if(local_book == null)throw new WSException("401","Book not found");
        
        //check if customer exists
        local_customer = customer_facade.find(entity.getCustomer_id());
        if(local_customer == null)throw new WSException("401","Customer not found");
        
        //check if it is a valid request
        if(entity.getQuantity() <=0)throw new WSException("401","Quantity requested set at 0");
        
        //check if the book is available
        if(local_book.getQuantity() <= 0)throw new WSException("401","Store Quantity at 0");
        
        //check if enough copies are available to satisfy oder
        if(entity.getQuantity() > local_book.getQuantity())throw new WSException("401","Excess quantity requested");
        
        //set the owner of the order
        if(!service_ctx.isUserInRole("administrator"))entity.setOwner(service_ctx.getUserPrincipal().getName());
        else entity.setOwner("administrator");
        try 
        {
            order_facade.edit(entity);
        } catch (Exception ex) 
        {

            throw new WSException(ex);
        }
        return "Order was edited successfully";
        
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeOrder")
    public String removeOrder(@WebParam(name = "id") Integer id) throws WSException 
    {
        /*Check for authorisation - could have just checked for a non-manager role, but a new role may be added at a later stage*/
        if(!this.service_ctx.isUserInRole("administrator") && !this.service_ctx.isUserInRole("developer"))
            throw new WSException("401","Not authorized to create orders");
        
        //Validate id
        if(id == null)throw new WSException("401","Incomplete order number entered");
        local_order = order_facade.find(id);
        if(local_order == null)throw new WSException("401","**Order not found**");
        
        //Validate correct owner or administrator
        if(!local_order.getOwner().equals(service_ctx.getUserPrincipal().getName()) && !service_ctx.isUserInRole("administrator"))
            throw new WSException("401","**Order not found**");
        
        //Order processed, too late to remove order - but administrator can still delete - order kept normally for record details
        if(local_order.isProcessed() == 1 && !service_ctx.isUserInRole("administrator"))throw new WSException("401","**nOrder processed, unable to delete**");
        
        order_facade.remove(local_order);
        
        return "**Order removed**";
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findOrder")
    public PurchaseOrder findOrder(@WebParam(name = "id") Integer id) throws WSException 
    {
        if(id == null)throw new WSException("401","**Incomplete order number entered**"); 
        local_order = order_facade.find(id);
        if(local_order == null)
            throw new WSException("401","**Order not found**");
        
        if(!local_order.getOwner().equals(service_ctx.getUserPrincipal().getName()) && !service_ctx.isUserInRole("administrator"))
            throw new WSException("401","**Order not found**");
    
         return local_order;
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findAll")
    public List<PurchaseOrder> findAll() 
    {
        if(!service_ctx.isUserInRole("administrator"))
            return order_facade.findAllByOwner(service_ctx.getUserPrincipal().getName());
        
        return order_facade.findAll();
    }
    
    ////////////////************
    
    private void checkEntityData(PurchaseOrder entity) throws WSException
    {
        if(entity == null)throw new WSException("401","<->No data in request<->");
        if(entity.getIsbn() <= 0)throw new WSException("401","<->Incomplete isbn entered<->");
        if(entity.getCustomer_id() == null)throw new WSException("401","<->Incomplete customer id entered<->");
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getOrderCount")
    public String getOrderCount() 
    {
        return String.valueOf(order_facade.count());
    }
    
}
