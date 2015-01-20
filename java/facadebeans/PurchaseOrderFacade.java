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
import entities.PurchaseOrder;

/**
 *
 * @author David_killa
 */
@Stateless
public class PurchaseOrderFacade extends AbstractFacade<PurchaseOrder> 
{
    @PersistenceContext(unitName = "webservicePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PurchaseOrderFacade() {
        super(PurchaseOrder.class);
    }
    
     @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void create(PurchaseOrder entity) 
    {
        super.create(entity); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void edit(PurchaseOrder entity) 
    {
        PurchaseOrder  temp = this.find(entity.getOrderNum());
        entity.setOrderNum(temp.getOrderNum());
        super.edit(entity); //To change body of generated methods, choose Tools | Templates.
    }
    public List<PurchaseOrder> findAllByOwner(String owner) 
    {
        List<PurchaseOrder> temp_order = null;
        try 
        {
            temp_order = (List<PurchaseOrder>)em.createNamedQuery("PurchaseOrder.findAllByOwner").setParameter("owner", owner).getResultList();
        } catch (Exception e) 
        {
            /*No need to deal with exception, not a rollback exception. 
             *The exception throws if the entity was not found
             */
        }
        return temp_order;
    }
}
