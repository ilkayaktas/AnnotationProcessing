import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by iaktas on 26.09.2017 at 11:26.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Factory {
    /**
     * The name of the factory
     */
    Class type();

    /**
     * The identifier for determining which item should be instantiated
     */
    String id();
}
