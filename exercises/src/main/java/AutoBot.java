import javax.annotation.PostConstruct;
import javax.ejb.*;

/**
 * Created by thang on 20.09.2016.
 */

@Singleton
@Startup
public class AutoBot {

    public AutoBot(){}

    @EJB
    private UserBean userBean;

    @EJB
    private PostBean postBean;

    @PostConstruct
    private void init(){
        String firstName = "Hello";
        String lastName = "Its me";
        String email = "iwonder@gmail.com";
        Address adr = new Address();
        adr.setCountry("Norway");
        adr.setPostcode(0372);
        adr.setCity("Oslo");
        userBean.createUser(firstName, lastName, email, adr);
    }

    @Lock(LockType.WRITE)
    @Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
    private void addPosts(){
        User a = userBean.findUserByEmail("iwonder@gmail.com");
        Post post = new Post();
        post.setUser(a);
        post.setMessage("Hello");
        System.out.println(a.getEmail());
        userBean.createPostFromGivenUser(a, post);
    }
}
