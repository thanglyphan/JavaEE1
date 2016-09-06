import javax.persistence.*;
import java.util.List;

/**
 * Created by thang on 06.09.2016.
 */

@Entity
public class User {

    @Id @GeneratedValue
    private long userId;

    private String firstname;
    private String lastname;
    private String email;

    @OneToOne(fetch = FetchType.EAGER)
    private Address address;

    @OneToMany(mappedBy = "user")
    private List<Post> post;



    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Post> getPost() {
        return post;
    }

    public void setPost(List<Post> post) {
        this.post = post;
    }

    public void addToPost(Post a){
        this.post.add(a);
    }

    public String toString(){
        return getFirstname() + " " + getLastname() + "\n" + getEmail();
    }
}
