import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
public class Comment extends Post{

    @NotNull @ManyToOne //One comment HAS to belong to one post. Many comments can belong to one post.
    private Post thePost;

    public Comment(){}


    public void setThePost(Post thePost) {
        this.thePost = thePost;
    }

    public Post getThePost() {
        return thePost;
    }
}
