import org.hibernate.annotations.Synchronize;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by thang on 15.09.2016.
 */
@Stateless
public class PostBean {

    @PersistenceContext(unitName = "MyDB")
    private EntityManager em;

    public PostBean(){}

    public List<Post> getAllPosts(){
        List<Post> posts = em.createNamedQuery(Post.FIND_ALL).getResultList();
        return posts;
    }
    public void mergeCommentToPost(Post post, Comment... comment){
        for(Comment o : comment) {
            em.persist(o);
            post.setComments(o);
        }
        em.merge(post);
    }
    public List<Post> findAllCommentsInOnePost(Post a){
        return em.createNamedQuery(Post.FIND_ALL_COMMENTS_IN_ONE_POST).setParameter(1, a.getPostId()).getResultList();

    }
}
