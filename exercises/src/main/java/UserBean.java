import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.xml.stream.StreamFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thang on 13.09.2016.
 */
@Stateless
public class UserBean {
    @PersistenceContext(unitName = "MyDB")
    private EntityManager em;

    public UserBean(){}

    public void createUser(@NotNull String fName, @NotNull String lName, @NotNull String email, @NotNull Address adr){
        User user = new User();
        user.setFirstname(fName);
        user.setLastname(lName);
        user.setEmail(email);
        user.setAddress(adr);

        persistInATransaction(adr, user);
    }


    public boolean createPostFromGivenUser(User user, Post post){
        user.setPost(new ArrayList<>());
        user.addToPost(post);

        persistInATransaction(post);
        em.merge(user);
        User found = findUserByEmail(user.getEmail());

        if(found.getPost().size() > 0){
            return true;
        }
        return false;
    }

    public void run(){

    }


    private void persistInATransaction(Object... obj) {
        for(Object o : obj) {
            em.persist(o);
        }
    }

    public List<User> getUsers(){
        return em.createNamedQuery(User.FIND_ALL).getResultList();
    }

    public User findUserByEmail(String email){

        List<User> users = em.createNamedQuery(User.FIND_BY_EMAIL).setParameter(1, email).getResultList();

        return users.get(0);
    }
}
