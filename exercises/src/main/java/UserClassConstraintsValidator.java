import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Created by thang on 13.09.2016.
 */
public class UserClassConstraintsValidator implements ConstraintValidator<UserClassConstraints, User> {
   @Override
   public void initialize(UserClassConstraints constraint) {
   }
   @Override
   public boolean isValid(User value, ConstraintValidatorContext context) {
      if(value.getEmail().contains(" ")){
         return false;
      }
      return !value.getEmail().contains(" ");
   }
}
