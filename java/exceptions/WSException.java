/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exceptions;

import faults.MySoapFault;
import javax.validation.ConstraintViolationException;
import javax.xml.ws.WebFault;

/**
 *
 * @author David_killa
 */
@WebFault(name="MySoapFault",targetNamespace="http://webservices/")
public class WSException extends Exception
{
    private static final long serialVersionUID = -6647544772732631047L; 
    private MySoapFault fault; 
    
    public WSException() 
    { 
        // TODO Auto-generated constructor stub 
    }
    protected WSException(MySoapFault fault) 
    { 
        super(fault.getFaultString()); 
        this.fault = fault;
    } 
    

    public WSException(String message, MySoapFault faultInfo)
    { 
        super(message); 
        this.fault = faultInfo; 
    }

    public WSException(String message, MySoapFault faultInfo, Throwable cause)
    { 
        super(message,cause); 
        this.fault = faultInfo; 
    } 

    public MySoapFault getFaultInfo()
    { 
        return fault; 
    } 
    
    public WSException(String message) 
    { 
        super(message); 
    }
    
    public WSException(String code, String message) 
    { 
        super(message);
        this.fault = new MySoapFault(); 
        this.fault.setFaultString(message); 
        this.fault.setFaultCode(code); 
        System.out.println(code);
    } 

    public WSException(Throwable cause) 
    { 
        super(cause);
    } 

    public WSException(Exception ex) throws WSException 
    {
        //get newline character
        String eol = System.getProperty("line.separator") + "<->"; 
        String constraintviolation_str = "\nBook data incorrect   \nProbable Causes: \n\tinserted null values\n\tincorrect data value sizes\n\tPlease look at data rules in api documentation"; 
        //casting Throwable object to SQL Exception
        Throwable t = unrollException(ex);
        if(t instanceof ConstraintViolationException)throw new WSException("406","Incorrect data sent");
        //if(ex.getCause() instanceof PersistenceException)throw new CustomEx(400,"innit");
        /**
         * Integrity constraint violation
         * Use to catch specific primary key violation
         * if(t instanceof org.apache.derby.client.am.SqlException)
         * if(t.getSQLState().equals("23505")) // Integrity constraint violation
         */
        
         throw new WSException(ex,"Internal");
    }
     
    public WSException(Exception ex, String internal) 
    { 
        super(ex);
    } 
    
    private Throwable unrollException(Exception exception)
    {
        Throwable temp_throw = exception;
        while(temp_throw.getCause() != null)
        {
          temp_throw = temp_throw.getCause();
        }
        return temp_throw;
    }
}
