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

    public void mergeCommentToPost(Post post, Comment comment){
        post.setComments(comment);
        em.merge(post);
    }
}
