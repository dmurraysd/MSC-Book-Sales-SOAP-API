/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package faults;

/**
 *
 * @author David_killa
 */
public class MySoapFault
{
     private String faultCode;
    private String faultString;
    
    public String getFaultCode() 
    { 
        return faultCode; 
    } 
    
    public void setFaultCode(String faultCode) 
    { 
        this.faultCode = faultCode; 
    } 
    public String getFaultString() 
    { 
        return faultString; 
    } 
    public void setFaultString(String faultString) 
    { 
        this.faultString = faultString; 
    }
}
