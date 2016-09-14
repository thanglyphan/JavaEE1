import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Created by thang on 13.09.2016.
 */

@Constraint(validatedBy = UserClassConstraintsValidator.class)
@Target({
        ElementType.TYPE,
        ElementType.ANNOTATION_TYPE}
)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserClassConstraints {
    String message() default "Invalid email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
