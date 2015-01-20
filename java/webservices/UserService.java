/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import exceptions.WSException;
import facadebeans.UsersFacade;
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
public class UserService 
{
    
    @EJB
    private UsersFacade user_facade;

    private Users local_user;
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createUser")
    public String createUser(@WebParam(name = "entity") Users entity) throws WSException 
    {
        //check for null entity
        if(entity == null)throw new WSException("401","\n<->Invalid request data<->");
        
        //validate entity variables
        if("".equals(entity.getUsername()))throw new WSException("401","**No user name entered**");
        if("".equals(entity.getPassword()))throw new WSException("401","**No password entered**");
        
        if(user_facade.find(entity.getUsername()) != null)throw new WSException("500","**Username already extists**"); 
        try 
        {
            user_facade.create(entity);
        } catch (Exception ex) 
        {
            throw new WSException(ex);
        }
       return "**User successfully created**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "editUser")
    public String editUser(@WebParam(name = "entity") Users entity) throws WSException  
    {
        //check for null entity
        if(entity == null)throw new WSException("401","**Invalid request data**");
        
        //validate entity variables
        if("".equals(entity.getUsername()))throw new WSException("401","**No user name entered**");
        if("".equals(entity.getPassword()))throw new WSException("401","**No password entered**");
        
        local_user = user_facade.find(entity.getUsername());
        if(local_user  == null)throw new WSException("401","**User not found**");
        try 
        {
            user_facade.edit(entity);
        } catch (Exception ex) 
        {
            throw new WSException(ex);
        }
        return "**User edited successfully**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "removeUser")
    public String removeUser(@WebParam(name = "username") String username) throws WSException 
    {
        //validate username
        if("".equals(username))throw new WSException("401","\n<->No user name entered<->");
        
        local_user = user_facade.find(username);
        
        if(local_user == null)throw new WSException("401","**User not found**");
        
        user_facade.remove(local_user);
        
        return "**User successfully removed**";
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "findUser")
    public Users findUser(@WebParam(name = "username") String username) throws WSException 
    {
        if("".equals(username))throw new WSException("401","**No user name entered**");
        
        local_user = user_facade.find(username);
        
        if(local_user == null)throw new WSException("401","**User not found**");
        
        return local_user;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getAllUsers")
    public List<Users> getAllUsers() 
    {
        return user_facade.findAll();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getUserCount")
    public String getUserCount() 
    {
        return String.valueOf(user_facade.count());
    }
    
    
    
    
    
}
