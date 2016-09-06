import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
public class Comment extends Post{

    @ManyToOne
    private Post thePost;

    public Comment(){}
}
