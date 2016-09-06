import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.ArrayList;

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
        User user = new User();
        assertTrue(persistInATransaction(user));
    }

    @Test
    public void testUserWithAddress(){
        //Create user and adress.
        User user = new User();
        Address address = new Address();
        address.setCity("Oslo");
        address.setCountry("Norway");
        address.setPostcode(0372);

        //Persisting user with no address.
        assertTrue(persistInATransaction(user));
        assertNull(user.getAddress());

        //Clear the cache
        em.clear();

        //Now we add address to user, persist address first.
        assertTrue(persistInATransaction(address));
        user.setAddress(address);
        em.getTransaction().begin();
        em.merge(user);

        //Find the user with the user id generated.
        User user1 = em.find(User.class, user.getUserId());

        assertEquals(address.getCity(), user1.getAddress().getCity());
    }

    @Test
    public void testAddingPostWithUpvotesAndDownvotes(){
        //Make user.
        User user = new User();
        user.setFirstname("Thang");
        user.setLastname("Phan");
        user.setEmail("Lyern52@gmail.com");

        //Make address.
        Address address = new Address();
        address.setCity("Oslo");
        address.setCountry("Norway");
        address.setPostcode(0372);
        user.setAddress(address);

        //Make post.
        Post post = new Post();
        post.setUser(user);
        String message = "Hello World";
        post.setMessage(message);

        user.setPost(new ArrayList<>());
        user.addToPost(post);

        //Check if this is good.
        assertTrue(persistInATransaction(user, address, post));
        assertEquals(post.getMessage(), user.getPost().get(0).getMessage());

        //Adding upvotes and downvotes on post created by specific user.
        User user2 = new User();
        user2.setFirstname("Thang2");
        user2.setLastname("Phan2");
        user2.setEmail("2Lyern52@gmail.com");

        assertTrue(persistInATransaction(user2));

        //Find the post i want to modify and clear cache afterwards.
        Post found = em.find(Post.class, post.getPostId());
        assertEquals(user.getEmail(), found.getPostId());
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

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setMessage("Fuck off");
        assertTrue(persistInATransaction(comment));
        em.clear();
        em.getTransaction().begin();
        post.addComment(comment);
        em.merge(post);

        assertEquals("This should fail", post.getComments().get(0).getMessage());

    }

}