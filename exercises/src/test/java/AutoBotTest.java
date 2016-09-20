import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ejb.EJB;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by thang on 20.09.2016.
 */

@RunWith(Arquillian.class)
public class AutoBotTest {
    @Deployment
    public static JavaArchive createTestArchive()
            throws UnsupportedEncodingException {

        return ShrinkWrap.create(JavaArchive.class).addDefaultPackage().addAsResource("META-INF/persistence.xml");

    }

    @EJB
    private PostBean postBean;

    @EJB
    private UserBean userBean;

    @Test
    public void testPostFromBot() throws InterruptedException {
        Thread.sleep(6000);

        List<Post> post = postBean.getAllPosts();

        assertEquals(4, post.size());

        User user = userBean.findUserByEmail("iwonder@gmail.com");

        assertTrue(post.stream().anyMatch(p -> p.getUser().getEmail().equals(user.getEmail())));
    }
}
