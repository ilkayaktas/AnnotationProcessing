package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by iaktas on 25.09.2017 at 17:00.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface BuilderProperty {
}