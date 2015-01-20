/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import exceptions.WSException;
import facadebeans.GroupsFacade;
import java.util.List;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;

/**
 *
 * @author David_killa
 */
@DeclareRoles({"manager"})
@RolesAllowed({"manager"})
@WebService(serviceName = "webservice")
@Stateless()
public class GroupService 
{

    @EJB
    private GroupsFacade group_facade;

    private Groups local_group;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createGroupUser")
    public String createGroupUser(@WebParam(name = "name") Groups entity) throws WSException 
    {
        if(entity == null)throw new WSException("401","**Invalid request data**");
        
        if("".equals(entity.getUsername()))throw new WSException("401","**No user name entered**");
        if("".equals(entity.getGroupname()))throw new WSException("401","**No group entered**");
        
        if(entity.getGroupname() != "administrator" || entity.getGroupname() != "developer" 
                || entity.getGroupname() != "manager")throw new WSException("401","**Group doesn't extist**");
        
        if(group_facade.find(entity.getUsername()) != null)throw new WSException("500","**Username already extists**"); 
        try 
        {
            group_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new WSException(ex);
        }
        return "**Group User successfully created**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "editGroupUser")
    public String editGroupUser(@WebParam(name = "name") Groups entity) throws WSException 
    {
        if(entity == null)throw new WSException("401","**Invalid request data**");
        
        if("".equals(entity.getUsername()))throw new WSException("401","**No user name entered**");
        if("".equals(entity.getGroupname()))throw new WSException("401","**No group entered**");
        
        if(!entity.getGroupname().equals("administrator") || entity.getGroupname().equals("developer") 
                || entity.getGroupname().equals("manager"))throw new WSException("401","**Group doesn't extist**");

        local_group = group_facade.find(entity.getUsername());
        
        if(local_group == null)throw new WSException("401","**User not found**"); 
        try 
        {
            group_facade.edit(entity);
        } catch (Exception ex) 
        {

            throw new WSException(ex);
            //throw new Exception(ex);
        }
        return "**Group user successfully edited**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeGroupUser")
    public String removeGroupUser(@WebParam(name = "username") String username) throws WSException 
    {
        if("".equals(username))throw new WSException("401","**No user name entered**");
        
        local_group = group_facade.find(username);
        
        if(local_group == null)throw new WSException("401","**User not found**"); 
        
        group_facade.remove(local_group);
        return "**User successfully removed**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "findGroupUser")
    public Groups findGroupUser(@WebParam(name = "username") String username) throws WSException 
    {
        if("".equals(username))throw new WSException("401","**No user name entered**");
        
        local_group = group_facade.find(username);
        
        if(local_group == null)throw new WSException("401","**User not found**"); 

        return local_group;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllGroupUsers")
    public List<Groups> getAllGroupUsers() 
    {
        return group_facade.findAll();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getGroupUserCount")
    public String getGroupUserCount() 
    {
        return String.valueOf(group_facade.count());
    }
    
    
    
}
