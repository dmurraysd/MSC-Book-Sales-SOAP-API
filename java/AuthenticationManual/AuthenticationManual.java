/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AuthenticationManual;

import com.sun.jersey.core.util.Base64;
import exceptions.WSException;
import java.util.ArrayList;
import java.util.Map;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;
import webservices.Users;

/**
 *
 * @author David_killa
 */
public class AuthenticationManual 
{
    private boolean loggedin = false;
    
    /*Check basic authentication*/
    private boolean checkAuthentication(WebServiceContext service_ctx, Users user )  throws WebServiceException, WSException
    {
            MessageContext mctx = service_ctx.getMessageContext();
            Map http_headers = (Map) mctx.get(MessageContext.HTTP_REQUEST_HEADERS);
            ArrayList list = (ArrayList) http_headers.get("Authorization");
            
            if (list == null || list.isEmpty()) throw new WSException("401","Authentication failed - Username & password not present in header");
            String userpass = (String) list.get(0);
            userpass = userpass.substring(5);
            String credentials = Base64.base64Decode(userpass);
            String username = null;
            String password = null;
            int p = credentials.indexOf(":");
            if (p > -1) 
            {
                username = credentials.substring(0, p);
                password = credentials.substring(p+1);
            }  
            else throw new WSException("401","Authentication failed - Error decoding Username & password");
            
            if (username.equals("")) throw new WSException("401","Authentication failed - Username is null");
            else if(password.equals("")) throw new WSException("401","Authentication failed - Password is null");
            else
            {
                 if(user == null)throw new WSException("401","User object null");
                 else
                 {
                     if(!user.getUsername().equals(username))throw new WSException("401","Authentication failed - username is incorrect");
                     if(!user.getPassword().equals(password))throw new WSException("401","Authentication failed - Password is incorrect");
                     this.loggedin = true;
                     return true;
                 }
            }
    }
}
