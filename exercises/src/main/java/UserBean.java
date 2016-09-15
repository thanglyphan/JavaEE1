import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.xml.stream.StreamFilter;
import java.util.List;

/**
 * Created by thang on 13.09.2016.
 */
@Stateless
public class UserBean{
    @PersistenceContext(unitName = "MyDB")
    private EntityManager em;


    public UserBean(){}

    public boolean createUser(String fName, String lName, String email, Address adr){

        User user = new User();
        user.setFirstname(fName);
        user.setLastname(lName);
        user.setEmail(email);
        user.setAddress(adr);

        return persistInATransaction(adr, user);
    }

    private boolean persistInATransaction(Object... obj) {
        try {
            for(Object o : obj) {
                em.persist(o);
            }
        } catch (Exception e) {
            System.out.println("FAILED TRANSACTION: " + e.toString());
            return false;
        }
        return true;
    }
    public List<User> getUsers(){
        List<User> results = em.createQuery("SELECT u FROM User u", User.class).getResultList();

        return em.createNamedQuery(User.FIND_ALL).getResultList();
    }
    public User findUserByEmail(String email){

        List<User> users = em.createNamedQuery(User.FIND_BY_EMAIL).setParameter(1, email).getResultList();

        return users.get(0);
    }
}
