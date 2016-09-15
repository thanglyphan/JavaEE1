import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

/**
 * Created by thang on 06.09.2016.
 */
@Entity
public class Address {
    @Id @GeneratedValue
    private Long id;

    @NotNull @Size(min = 2, max = 100)
    private String city;

    @NotNull @Size(min = 2, max = 100)
    private String country;

    @NotNull
    private int postcode;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getPostcode() {
        return postcode;
    }

    public void setPostcode(int postcode) {
        this.postcode = postcode;
    }


}
