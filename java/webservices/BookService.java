/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webservices;

import exceptions.WSException;
import facadebeans.BooksFacade;
import java.util.List;
import java.util.logging.Level;
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
 * Could use above to stop priviligies for non developer and non-manager 
 * roles but would be unable to throw customised exception
 */
@WebService(serviceName = "webservice")
@Stateless()
public class BookService 
{
    @Resource
    private WebServiceContext service_ctx;
    
    @EJB
    private BooksFacade book_facade;
    
    private static final Logger LOG = Logger.getLogger(BookService.class.getName());
    private Books local_book;
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "createBook")
    public String createBook(@WebParam(name = "entity") Books entity) throws WSException
    {
            if(!service_ctx.isUserInRole("administrator"))throw new WSException("401","**Not authorized to add books**"); 
            this.validateData(entity);
            if(book_facade.findByIsbn(entity.getIsbn()) != null)throw new WSException("409","**Book with ISBN already extists**"); 
            try 
            {
                book_facade.create(entity);
            } catch (Exception ex) 
            {
                throw new WSException(ex);
            }
           return "**Book is successfully created**";
    }
    
     /**
     * Web service operation
     */
    @WebMethod(operationName = "editBook")
    public String editBook(@WebParam(name = "entity") Books entity) throws WSException
    {
            if(!this.service_ctx.isUserInRole("administrator"))throw new WSException("401","**Not authorized to add books**");
            this.validateData(entity);
            Books  temp = book_facade.findByIsbn(entity.getIsbn());
            if(temp == null)throw new WSException("404","**Book with isbn not found**"); 
            try 
            {
                book_facade.edit(entity);
            } catch (Exception ex) 
            {
                
                throw new WSException(ex);
                //throw new Exception(ex);
            }
            return "**Book successfully edited**";
    }
    
     /**
     * Web service operation
     */
    @WebMethod(operationName = "removeBook")
    public String removeBook(@WebParam(name = "isbn") long isbn) throws WSException 
    {
            if(!this.service_ctx.isUserInRole("administrator"))
            {
                throw new WSException("401","**Not authorized to delete books**");
            } 
            
            if(isbn <= 0)throw new WSException("400","**Incorrect isbn number**");
            local_book = book_facade.findByIsbn(isbn);
            if(local_book == null)throw new WSException("404","**Book with isbn not found**"); 
            book_facade.remove(local_book);
            return "**Book deleted**";
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findBookByISBN")
    public Books findBookByISBN(@WebParam(name = "ISBN") long ISBN) throws WSException 
    {
        local_book = book_facade.findByIsbn(ISBN);
        if(local_book != null)return local_book;
        throw new WSException("404","**Book with isbn not found**");
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findBookByAuthor")
    public List<Books> findBookByAuthor(@WebParam(name = "author") String author) throws WSException 
    {
        List<Books> tempList = book_facade.findByAuthor(author);
        if(tempList != null)return tempList;
        throw new WSException("404","**Books with this author not found**");
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findBookByTitle")
    public Books findBookByTitle(@WebParam(name = "title") String title) throws WSException 
    {
        book_facade.findByTitle(title);
        if(local_book != null)return local_book;
        if(local_book == null)LOG.log(Level.SEVERE, title);
        throw new WSException("404","**Book with this title not found**");
    }
    
    /**
     * Web service operation
     */
    @WebMethod(operationName = "findAll")
    public List<Books> findAll() throws WSException
    {
        return book_facade.findAll();
    }
    
    /*Check if not null variables are null*/
    private void validateData(Books entity) throws WSException
    {
        if(entity == null)throw new WSException("400","**No data in request**");
        if(entity.getIsbn() <= 0)throw new WSException("400","**Incorrect isbn number**");
        if("".equals(entity.getTitle()))throw new WSException("400","**No title entered**");
        if("".equals(entity.getAuthor()))throw new WSException("400","**No Author entered**");
        if("".equals(entity.getCopyright()))throw new WSException("400","**No Copyright entered**");
        if("".equals(entity.getPublisher()))throw new WSException("400","**No publisher entered**");
        if(entity.getPrice() == 0)throw new WSException("400","**No price entered**");
        if(entity.getQuantity() <= 0)throw new WSException("400","**Quantity must be at least 1**");
    }
    
}
