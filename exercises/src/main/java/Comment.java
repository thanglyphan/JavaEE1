import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
public class Comment extends Post{
    @Id
    private String id;
    @ManyToOne
    private Post post;

}
