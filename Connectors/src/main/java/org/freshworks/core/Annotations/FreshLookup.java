package org.freshworks.core.Annotations;


import java.lang.annotation.*;

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
