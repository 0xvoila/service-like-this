package org.freshworks.core.Annotations;


import java.lang.annotation.*;
import java.lang.reflect.Field;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FreshLookup {
    Class<?> leftClass();
    String leftClassField();

    Class<?> rightClass();
    String rightClassField();

    String join_type();

}
