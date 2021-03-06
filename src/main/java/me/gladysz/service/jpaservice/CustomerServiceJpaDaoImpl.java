package me.gladysz.service.jpaservice;

import me.gladysz.model.Customer;
import me.gladysz.model.User;
import me.gladysz.service.CustomerService;
import me.gladysz.service.security.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Profile("jpadao")
public class CustomerServiceJpaDaoImpl extends AbstractJpaDaoService implements CustomerService {

    private final EncryptionService encryptionService;

    @Autowired
    public CustomerServiceJpaDaoImpl(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    @Override
    public List<Customer> listAll() {
        EntityManager em = emf.createEntityManager();

        return em.createQuery("from Customer", Customer.class).getResultList();
    }

    @Override
    public Customer getById(Long id) {
        EntityManager em = emf.createEntityManager();

        return em.find(Customer.class, id);
    }

    @Override
    public Customer saveOrUpdate(Customer domainObject) {
        EntityManager em = emf.createEntityManager();

        em.getTransaction().begin();
        if (domainObject.getUser() != null && domainObject.getUser().getPassword() != null) {
            domainObject.getUser().setEncryptedPassword(
                    encryptionService.encryptString(domainObject.getUser().getPassword()));
        }

        Customer savedCustomer = em.merge(domainObject);
        em.getTransaction().commit();
        return savedCustomer;
    }

    @Override
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();

        Customer customer = em.find(Customer.class, id);

        em.getTransaction().begin();
        User user = customer.getUser();
        if (user != null) {
            user.setCustomer(null);
            customer.setUser(null);
        }
        em.remove(customer);
        em.getTransaction().commit();
    }

}
