import javax.persistence.*;
import javax.xml.stream.StreamFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
public class Post {
    @Id
    private String postId;
    @ManyToOne
    private User user;

    private String message;
    private int upvotes;
    private int downvotes;
    private String date;
    private String author;

    @OneToMany
    private List<User> upvotedBy;
    @OneToMany
    private List<User> downvotedBy;


    @OneToMany(mappedBy = "post")
    private List<Comment> comments;


    public Post(){
        this.date = "123";
        //comments = new ArrayList<>();
        upvotedBy = new ArrayList<>();
        downvotedBy = new ArrayList<>();
    }
    public void addComment(Comment a){
        this.comments.add(a);
    }
    public List<Comment> getComments(){
        return comments;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.author = user.getFirstname() + " " + user.getLastname();
        this.postId = user.getEmail();
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
}
