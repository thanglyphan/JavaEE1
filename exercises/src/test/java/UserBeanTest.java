import javafx.geometry.Pos;
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
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by thang on 13.09.2016.
 */

@RunWith(Arquillian.class)
public class UserBeanTest {

    @Deployment
    public static JavaArchive createTestArchive()
            throws UnsupportedEncodingException {

        return ShrinkWrap.create(JavaArchive.class).addDefaultPackage().addAsResource("META-INF/persistence.xml");

    }
    @EJB
    private UserBean userBean;

    @EJB
    private PostBean postBean;


    @Test
    public void testUserIsCreated(){
        Address adr = new Address();
        adr.setCity("Oslo");
        adr.setCountry("Norway");
        adr.setPostcode(0372);

        Address adr2 = new Address();
        adr2.setCity("Oslo");
        adr2.setCountry("Norway");
        adr2.setPostcode(0372);

        userBean.createUser("Thang", "Phan", "Lyern52@gmail.com", adr);
        userBean.createUser("Thango", "Phano", "Lyerno@gmail.com", adr2);

        assertEquals(2, userBean.getUsers().size());
    }

    @Test
    public void testFindUserByEmail(){
        assertEquals(2, userBean.getUsers().size());

        assertEquals("Lyerno@gmail.com", userBean.findUserByEmail("Lyerno@gmail.com").getEmail());
        assertEquals("Lyern52@gmail.com", userBean.findUserByEmail("Lyern52@gmail.com").getEmail());
    }

    @Test
    public void testcreatePostFromGivenUser(){
        User found = userBean.findUserByEmail("Lyern52@gmail.com");
        User found2 = userBean.findUserByEmail("Lyerno@gmail.com");

        Post post = new Post();
        post.setUser(found);
        post.setMessage("You have one received message");

        assertTrue(userBean.createPostFromGivenUser(found, post));

        assertEquals(post.getMessage(), found.getPost().get(0).getMessage());
        assertEquals(found.getEmail(), post.getUser().getEmail());

        Comment comment = new Comment();
        comment.setUser(found2);
        comment.setThePost(post);
        comment.setMessage("This is a comment");

        assertTrue(userBean.createPostFromGivenUser(found2, comment));
        postBean.mergeCommentToPost(post, comment);

        //assertEquals("lol", userBean.findUserByEmail("Lyerno@gmail.com").getPost().get(0).getMessage());
        List<Post> posts = postBean.getAllPosts();

        //Check here if the comments and posts are good.
        assertEquals(comment.getMessage(), posts.get(0).getComments().get(0).getMessage());
        assertEquals(found2.getFirstname(), posts.get(0).getComments().get(0).getUser().getFirstname());
    }





}
