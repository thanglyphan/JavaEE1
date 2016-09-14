import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by thang on 08.09.2016.
 */
public class QueryTest {

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
    public void testGetAllCountriesOfUser(){
        //Init objects
        User user = getValidUser();
        User user1 = getValidUser();
        User user2 = getValidUser();

        user2.getAddress().setCountry("Sweden");
        user1.getAddress().setCountry("Norway");
        user.getAddress().setCountry("Norway");

        //Try persist
        assertTrue(persistInATransaction(user, user1, user2));

        //Create the query
        Query query = em.createNamedQuery("User.find_all_user_countries");

        //Get the list
        List<String> users = query.getResultList();

        Set<String> countries = new HashSet<>();

        for(String a: users){
            countries.add(a);
        }

        for(String a: countries){
            System.out.println(a);
        }
        //We have now two countries in our list, three users have one adress each, two users lives in Norway, one in Sweden.
        assertTrue(!countries.isEmpty());

        //Now we find all users in Norway.
        query = em.createNamedQuery("User.find_all_user_in_country");
        query.setParameter(1, "Norway");
        //Two users live in Norway, one user live in Sweden.
        assertEquals(2, query.getResultList().size());
        query.setParameter(1, "Sweden");
        assertEquals(1, query.getResultList().size());

        //Lets find all the users in the database, should be three.
        query = em.createNamedQuery("User.find_all");
        assertEquals(3, query.getResultList().size());
    }
    @Test
    public void testFindAllPost(){
        User user1 = getValidUser();
        User user2 = getValidUser();
        User user3 = getValidUser();

        user1.getAddress().setCountry("Norway");
        user2.getAddress().setCountry("Norway");
        user3.getAddress().setCountry("Vietnam");
        user1.setPost(new ArrayList<>()); user2.setPost(new ArrayList<>()); user3.setPost(new ArrayList<>());

        Post post1 = new Post(); Post post2 = new Post(); Post post3 = new Post(); Post post4 = new Post();

        post1.setUser(user1);
        post1.setMessage("First");
        post2.setUser(user2);
        post2.setMessage("Second");
        post3.setUser(user3);
        post3.setMessage("Third");
        post4.setUser(user1);
        post4.setMessage("Fourth");

        user1.setFirstname("Per");
        user1.setLastname("Bjarne");
        user1.setEmail("p.b@gmail.com");

        user1.addToPost(post1);
        user1.addToPost(post4);
        user2.addToPost(post2);
        user3.addToPost(post3);

        assertTrue(persistInATransaction(user1, user2, user3, post1, post2, post3, post4));

        Query query = em.createNamedQuery("Post.find_all");

        List<Post> posts = query.getResultList();

        //Finally test all three posts are in the list of query.
        assertEquals(4, posts.size());

        //Now, we find all post in Norway.
        Query countryQuery = em.createNamedQuery("Post.find_all_in_country");
        countryQuery.setParameter(1, "Norway");

        List<Post> countries = countryQuery.getResultList();
        //Now, there are three posts from Norway.
        assertEquals(3, countries.size());

        //Now, we test post in Vietnam, there are only one.
        countryQuery.setParameter(1, "Vietnam");
        countries = countryQuery.getResultList();
        assertEquals(1, countries.size());

        //Now, we test top posters, who is it. We set parameter like 2 "post", Per is only one. If we set to 1, all users posted one post comming.
        Query qa = em.createNamedQuery("User.find_top_posters");
        qa.setParameter(1, 2);

        List<User> users = qa.getResultList();

        for(User a: users){
            System.out.println(a);
        }
        //We check if the first person in the list is top poster called "Per"
        assertEquals("Per", users.get(0).getFirstname());





    }
}
