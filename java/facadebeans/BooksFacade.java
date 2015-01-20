/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package facadebeans;

import exceptions.WSException;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;
import webservices.Books;

/**
 *
 * @author David_killa
 */
@Stateless
public class BooksFacade extends AbstractFacade<Books> {
    @PersistenceContext(unitName = "webservicePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BooksFacade() {
        super(Books.class);
    }
     /**
     * Persists a books entity to the database
     * @TransactionAttribute is used to catch exceptions in a CMT application
     * @param entity
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(Books entity) 
    {
        super.create(entity);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void edit(Books entity) 
    {
        Books  temp = this.findByIsbn(entity.getIsbn());
        entity.setBookid(temp.getBookid());
        super.edit(entity); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     *
     * @param ISBN
     * @return null or the book entity with the chosen ISBN
     */
    public Books findByIsbn(long ISBN) 
    {
        Books temp_book = null;
        try 
        {
            temp_book = (Books)em.createNamedQuery("Books.findByIsbn").setParameter("isbn", ISBN).getSingleResult();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }
    
    
    public List<Books> findByAuthor(String author) 
    {
        List<Books> temp_book = null;
        try 
        {
            temp_book = (List<Books>)em.createNamedQuery("Books.findByAuthor").setParameter("author", author).getResultList();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }

    public Books findByTitle(String title) 
    {
        Books temp_book = null;
        try 
        {
            temp_book = (Books)em.createNamedQuery("Books.findByTitle").setParameter("title", title).getSingleResult();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_book;
    }
    
}
