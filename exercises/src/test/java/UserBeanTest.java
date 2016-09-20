import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.core.api.threading.ExecutorService;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ejb.EJB;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

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

        assertEquals(3, userBean.getUsers().size());
    }

    @Test
    public void testFindUserByEmail(){
        assertEquals(3, userBean.getUsers().size());

        assertEquals("Lyerno@gmail.com", userBean.findUserByEmail("Lyerno@gmail.com").getEmail());
        assertEquals("Lyern52@gmail.com", userBean.findUserByEmail("Lyern52@gmail.com").getEmail());
    }
    @Test
    public void testPostFromBot() throws InterruptedException {
        Thread.sleep(20000);

        List<Post> post = postBean.getAllPosts();

        //assertTrue(post.size() > 0);
        assertTrue(post.stream().anyMatch(n -> n.getUser().getFirstname().equals("iwonder@gmail.com")));
    }
    @Test
    public void testcreatePostFromGivenUser(){
        //Finding users
        User thang = userBean.findUserByEmail("Lyern52@gmail.com");
        User hassan = userBean.findUserByEmail("Lyerno@gmail.com");

        //Create one new post. Post created by Thang
        Post post = new Post();
        post.setUser(thang);
        post.setMessage("You have one received message");

        //Add the post to given user. Thang owns the post now.
        assertTrue(userBean.createPostFromGivenUser(thang, post));

        //Check if Thang have posted something.
        assertEquals(post.getMessage(), thang.getPost().get(0).getMessage());
        assertEquals(thang.getEmail(), post.getUser().getEmail());

        //Now, lets add a comment to Thangs post.
        Comment comment = new Comment();
        comment.setUser(hassan);
        comment.setThePost(post);
        comment.setMessage("This is a comment");

        Comment secondComment = new Comment();
        secondComment.setUser(hassan);
        secondComment.setThePost(post);
        secondComment.setMessage("This is another comment");

        Comment thirdComment = new Comment();
        thirdComment.setUser(hassan);
        thirdComment.setThePost(post);
        thirdComment.setMessage("This is the third comment");


        //Now, add comment to Thangs post by another user Hassan
        postBean.mergeCommentToPost(post, comment, secondComment, thirdComment);


        List<Post> posts = postBean.getAllPosts();

        //Check here if the comments and posts are good.
        assertEquals(5, posts.size());
        assertEquals(comment.getMessage(), posts.get(0).getComments().get(0).getMessage());
        assertEquals(secondComment.getMessage(), posts.get(0).getComments().get(1).getMessage());
        assertEquals(thirdComment.getMessage(), posts.get(0).getComments().get(2).getMessage());
        assertEquals(hassan.getFirstname(), posts.get(0).getComments().get(0).getUser().getFirstname());
    }

    @Test
    public void testfindAllCommentsInOnePost(){
        Post found = postBean.getAllPosts().get(0);
        //All the comments, what post does that belongs to? I check here.
        //assertEquals("[This is a comment, This is another comment, This is the third comment]", postBean.findAllCommentsInOnePost(found).toString());
        //assertEquals(3, postBean.findAllCommentsInOnePost(found).size());
    }

    //@Test
    public void testTwoUsersWriteToSamePostSameTime(){
        //Getting everything I need first.
        assertEquals(2, userBean.getUsers().size());
        assertNotNull(getRandomUser());
        Post post = postBean.getAllPosts().get(0);

        //Check if post contains 3 comments, then delete, then check if post has 0 comments.
        assertEquals(3, post.getComments().size());
        assertTrue(postBean.deleteCommentsOnPost(post));
        assertEquals(0, post.getComments().size());

        //Now, lets make user "a" and "b" write some comments to the same post.
        final int nThreads = 4;
        final int loops = 100;


        Runnable runnable = () -> {
            for(int i = 0; i < loops; i++){
                Comment comment = new Comment();
                comment.setThePost(post);
                comment.setMessage("1");
                comment.setUser(getRandomUser());
                postBean.mergeCommentToPost(post, comment);
            }
        };

        for(int i = 0; i < nThreads; i++){
            Thread t = new Thread(runnable);
            t.run();
        }

        //3 bcuz 3 comments are added before.
        int expected = 3 + nThreads * loops;
        assertEquals(expected, postBean.getAllPosts().get(0).getComments().size());

    }

    private User getRandomUser(){
        User a = userBean.findUserByEmail("Lyern52@gmail.com");
        User b = userBean.findUserByEmail("Lyerno@gmail.com");

        ArrayList<User> list = new ArrayList<>();
        list.add(a);
        list.add(b);

        return list.get((int) Math.random() * 2);
    }







}
