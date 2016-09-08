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

    @Test
    public void testGetAllCountriesOfUser(){
        //Init objects
        User user = new User();
        User user1 = new User();
        User user2 = new User();
        Address adr2 = new Address();
        adr2.setCountry("Sweden");
        Address adr = new Address();
        adr.setCountry("Norway");
        user2.setAddress(adr2);
        user1.setAddress(adr);
        user.setAddress(adr);

        //Try persist
        assertTrue(persistInATransaction(user, user1, user2, adr, adr2));

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
    }
    @Test
    public void testFindAllPost(){
        User user1 = new User(); User user2 = new User(); User user3 = new User();
        Address adr1 = new Address();
        Address adr2 = new Address();
        adr1.setCountry("Norway");
        adr2.setCountry("Vietnam");
        user1.setAddress(adr1);
        user2.setAddress(adr1);
        user3.setAddress(adr2);
        user1.setPost(new ArrayList<>()); user2.setPost(new ArrayList<>()); user3.setPost(new ArrayList<>());

        Post post1 = new Post(); Post post2 = new Post(); Post post3 = new Post();

        post1.setUser(user1);
        post1.setMessage("First");
        post2.setUser(user2);
        post2.setMessage("Second");
        post3.setUser(user3);
        post3.setMessage("Third");

        user1.addToPost(post1);
        user2.addToPost(post2);
        user3.addToPost(post3);

        assertTrue(persistInATransaction(user1, user2, user3, post1, post2, post3, adr1, adr2));

        Query query = em.createNamedQuery("Post.find_all");

        List<Post> posts = query.getResultList();

        //Finally test all three posts are in the list of query.
        assertEquals(3, posts.size());

        //Now, we find all post in Norway.
        Query countryQuery = em.createNamedQuery("Post.find_all_in_country");
        countryQuery.setParameter(1, "Norway");

        List<Post> countries = countryQuery.getResultList();
        //Now, there are two posts from Norway.
        assertEquals(2, countries.size());

        //Now, we test post in Vietnam, there are only one.
        countryQuery.setParameter(1, "Vietnam");
        countries = countryQuery.getResultList();
        assertEquals(1, countries.size());



    }
}
