import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertTrue;

/**
 * Created by thang on 13.09.2016.
 */

@RunWith(Arquillian.class)
public class UserBeanTest {
    private EntityManagerFactory factory;
    private EntityManager em;

    @Deployment
    public static JavaArchive createTestArchive()
            throws UnsupportedEncodingException {
        return ShrinkWrap.create(JavaArchive.class).addClass(UserBean.class);
    }

    @EJB
    private UserBean userBean;


    @Test
    public void testUserIsCreated(){

        Address adr = new Address();
        adr.setCity("Oslo");
        adr.setCountry("Norway");
        adr.setPostcode(0372);

        userBean.createUser("Thang", "Phan", "Lyern52@gmail.com", adr);

        assertTrue(userBean.createUser("Thang", "Phan", "Lyern52@gmail.com", adr));
    }


}
