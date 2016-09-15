import com.sun.org.apache.xalan.internal.xsltc.dom.AdaptiveResultTreeImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by thang on 06.09.2016.
 */
public class UserTest {

    private EntityManagerFactory factory;
    private EntityManager em;

    @Before
    public void init() {
        factory = Persistence.createEntityManagerFactory("DB");
        em = factory.createEntityManager();
    }

    @After
    public void tearDown() {
        em.close();
        factory.close();
    }


    private boolean persistInATransaction(Object... obj) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for(Object o : obj) {
                em.persist(o);
            }
            tx.commit();
        } catch (Exception e) {
            System.out.println("FAILED TRANSACTION: " + e.toString());
            tx.rollback();
            return false;
        }

        return true;
    }


    @Test
    public void testEmptyUser(){
        User user = getValidUser();
        assertTrue(persistInATransaction(user));
    }

    private User getValidUser(){
        User user = new User();
        user.setFirstname("Thang");
        user.setLastname("Phan");
        user.setEmail("lyern52@gmail.com");
        Address adr = new Address();
        adr.setCountry("Norway");
        adr.setCity("Oslo");
        adr.setPostcode(1722);
        em.persist(adr);
        user.setAddress(adr);
        return user;
    }

    @Test
    public void testUserWithAddress(){
        //Create user and adress.
        User user = getValidUser();

        user.getAddress().setCity("Oslo");
        user.getAddress().setCountry("Norway");
        user.getAddress().setPostcode(0372);

        //Persisting user with address.
        assertTrue(persistInATransaction(user));

        //Clear the cache
        em.clear();

        //Now we add address to user, persist address first.
        em.getTransaction().begin();
        em.merge(user);

        //Find the user with the user id generated.
        User user1 = em.find(User.class, user.getUserId());

        assertEquals(user.getAddress().getCity(), user1.getAddress().getCity());
    }

    @Test
    public void testAddingPostWithUpvotesAndDownvotes(){
        //Make user.
        User user = getValidUser();

        //Make address.
        user.getAddress().setCity("Oslo");
        user.getAddress().setCountry("Norway");
        user.getAddress().setPostcode(0372);

        //Make post.
        Post post = new Post();
        post.setUser(user);
        String message = "Hello World";
        post.setMessage(message);

        user.setPost(new ArrayList<>());
        user.addToPost(post);

        //Check if this is good.
        assertTrue(persistInATransaction(user, post));
        assertEquals(post.getMessage(), user.getPost().get(0).getMessage());

        //Adding upvotes and downvotes on post created by specific user.
        User user2 = getValidUser();
        user2.setFirstname("Thangyo");
        user2.setLastname("Phanyo");
        user2.setEmail("2Lyern52@gmail.com");

        assertTrue(persistInATransaction(user2));

        //Find the post i want to modify and clear cache afterwards.
        Post found = em.find(Post.class, post.getPostId());
        assertEquals(post.getPostId(), found.getPostId());
        em.clear();

        //Start to add upvotes/downvotes, and check if the upvote/downvote is added by THAT user.
        em.getTransaction().begin();
        post.setUpvotes(1, user2);
        post.setDownvotes(1, user);
        em.merge(post);

        //Now its time to check if the users who upvoted/downvotes is correct.
        assertEquals(1, post.getUpvotes());
        assertEquals(1, post.getDownvotes());
        assertTrue(post.getUpvotedBy(user2));
        assertTrue(post.getDownvotedBy(user));

        //Lets print out from post who is upvoted and downvoted.
        post.printDownvoted();
        post.printUpvoted();
        //As you can see, this is correctly implemented :)
    }

    @Test
    public void testPostCanHaveComments(){
        //Creating a post and comment
        Post post = new Post();
        post.setMessage("Testing the message");

        //New user wrote this comment.
        Comment comment = new Comment();
        comment.setMessage("Lol");
        comment.setThePost(post);

        User newUser = getValidUser();
        newUser.setFirstname("Test");
        newUser.setLastname("Tester");
        newUser.setEmail("Test@test.no");
        newUser.setPost(new ArrayList<>());
        newUser.addToPost(comment);
        comment.setUser(newUser);
        //Set comment into "post"
        post.setComments(comment);

        User u = getValidUser();
        u.setPost(new ArrayList<>());
        u.addToPost(post);

        post.setUser(u);
        //And persist, then check who created post, who created first comment.
        assertTrue(persistInATransaction(u, newUser, post, comment)); //Works even when I dont persist post and comment.
        assertEquals(u.getFirstname(), post.getUser().getFirstname());
        assertEquals(newUser.getFirstname(), comment.getUser().getFirstname());
        //Now, one user have one registered post and comment in his name.

        //Creating new comment and add that new comment to the old comment.
        Comment newComment = new Comment();
        newComment.setMessage("The message1");
        newComment.setUser(u);
        Comment newComment2 = new Comment();
        newComment2.setMessage("The message2");
        newComment2.setUser(u);
        comment.setComments(newComment);
        comment.setComments(newComment2);


        //User upvotes one comment
        User user = getValidUser();
        user.setFirstname("Thango");
        user.setLastname("Mango");
        user.setEmail("Thang-phan@outlook.com");

        user.setPost(new ArrayList<>());
        user.addToPost(newComment);
        user.addToPost(newComment2);

        assertTrue(persistInATransaction(user));
        comment.setUpvotes(1, user);

        //Check who if "user" upvoted
        assertTrue(comment.getUpvotedBy(user));
        //Find out who upvoted
        assertEquals("Thango", comment.getWhoUpvoted().get(0).getFirstname());

        assertEquals("Lol", comment.getMessage());
        assertEquals(1, comment.getUpvotes());
        assertEquals(0, comment.getComments().get(0).getUpvotes());
        assertEquals("The message1", comment.getComments().get(0).getMessage());

        //How many comments are inside "comment"
        assertEquals(2, comment.getComments().size());
        //How many comments are inside "post"
        assertEquals(1, post.getComments().size());
        System.out.println(comment.getDate());

    }


}