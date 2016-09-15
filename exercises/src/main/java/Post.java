import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
@NamedQueries(value = {
        @NamedQuery(name = Post.FIND_ALL, query = "SELECT a FROM Post a"),
        @NamedQuery(name = Post.FIND_ALL_IN_COUNTRY, query = "SELECT a FROM Post a WHERE a.user.address.country LIKE ?1")
})
public class Post {
    @Id @GeneratedValue
    private Long postId;

    @ManyToOne @NotNull
    private User user;

    public static final String FIND_ALL = "Post.find_all";
    public static final String FIND_ALL_IN_COUNTRY = "Post.find_all_in_country";

    @NotNull @Size(min = 1, max = 300)
    private String message;

    private int upvotes;

    private int downvotes;

    @Past
    @NotNull
    @Temporal(TemporalType.DATE)
    private Date date;

    @NotNull
    private String author;

    @OneToMany
    private List<User> upvotedBy;
    @OneToMany
    private List<User> downvotedBy;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "thePost", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Post(){
        this.date = new Date();
        LocalDateTime localDate = LocalDateTime.now();
        date = date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());
        comments = new ArrayList<>();
        upvotedBy = new ArrayList<>();
        downvotedBy = new ArrayList<>();
    }
    public Date getDate(){
        return date;
    }

    public Long getPostId() {
        return postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.author = user.getFirstname() + " " + user.getLastname();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes, User a) {
        this.upvotes += upvotes;
        this.upvotedBy.add(a);
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes, User a) {
        this.downvotes += downvotes;
        this.downvotedBy.add(a);
    }
    public boolean getUpvotedBy(User a){
        for(User b: upvotedBy){
            if(b.equals(a)){
                return true;
            }
        }
        return false;
    }


    public boolean getDownvotedBy(User a){
        for(User b: downvotedBy){
            if(b.equals(a)){
                return true;
            }
        }
        return false;
    }
    public List<User> getWhoUpvoted(){
        return upvotedBy;
    }
    public List<User> getWhoDownvoted(){
        return downvotedBy;
    }

    public void printUpvoted(){
        System.out.println("UPVOTED BY:");
        for(User a: upvotedBy){
            System.out.println(a);
        }
    }

    public void printDownvoted(){
        System.out.println("DOWNVOTED BY:");
        for(User a: downvotedBy){
            System.out.println(a);
        }
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(Comment a){
        getComments().add(a);
    }
}
