import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
@NamedQueries(value = {
        @NamedQuery(name = User.FIND_ALL_USER_COUNTRIES, query = "SELECT a.address.country FROM User a"),
        @NamedQuery(name = User.FIND_ALL, query = "SELECT a FROM User a"),
        @NamedQuery(name = User.FIND_ALL_USER_IN_COUNTRY, query = "SELECT a FROM User a WHERE a.address.country LIKE ?1"),
        @NamedQuery(name = User.FIND_TOP_POSTERS, query = "SELECT a FROM User a WHERE a.post.size >= ?1 order by post.size desc "),
        @NamedQuery(name = User.FIND_BY_EMAIL, query = "SELECT a FROM User a WHERE a.email = ?1")
})
//@UserClassConstraints
public class User {
    @Id @GeneratedValue
    private long userId;

    public static final String FIND_ALL_USER_COUNTRIES = "User.find_all_user_countries";
    public static final String FIND_ALL = "User.find_all";
    public static final String FIND_ALL_USER_IN_COUNTRY = "User.find_all_user_in_country";
    public static final String FIND_TOP_POSTERS = "User.find_top_posters";
    public static final String FIND_BY_EMAIL = "User.find_by_email";

    @NotNull @Size(min = 2 , max = 100) @Pattern(regexp = "^[a-zA-Z ]*$")
    private String firstname;

    @NotNull @Size(min = 2 , max = 100) @Pattern(regexp = "^[a-zA-Z ]*$")
    private String lastname;

    @NotNull
    @Pattern(regexp = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
    private String email;


    @OneToOne(fetch = FetchType.EAGER) @NotNull
    private Address address;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL)
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
        return getFirstname() + " " + getLastname();
    }

}
