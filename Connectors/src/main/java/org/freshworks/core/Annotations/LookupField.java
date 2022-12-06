package org.freshworks.core.Annotations;


import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface LookupField {
    Class<?> leftClass();
    String leftClassField();

    Class<?> rightClass();
    String rightClassField();

    String join_type();

}
