import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.xml.stream.StreamFilter;

/**
 * Created by thang on 13.09.2016.
 */
@Stateless
public class UserBean{
    @PersistenceContext(unitName = "MyDB")
    private EntityManagerFactory entityManagerFactory;
    private EntityManager em = entityManagerFactory.createEntityManager();


    public UserBean(){}

    public boolean createUser(String fName, String lName, String email, Address adr){
        User user = new User();
        user.setFirstname(fName);
        user.setLastname(lName);
        user.setAddress(adr);
        user.setEmail(email);
        System.out.println(em);
        return persistInATransaction(user, adr);
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
    public boolean isRegistered(@NotNull Long userId){
        User user = em.find(User.class, userId);
        return user != null;
    }
}
