package org.freshworks.core.Annotations;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface FreshHierarchy {

    Class<?> parentClass() default Void.class;
}

