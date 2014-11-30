/**
 * DbPersian (DataBase PERSistence ANnotations)
 */

package net.dbpersian.annotations.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DbTable
{
    String sqlName();
    int version() default 1;
    String onEntityLoad() default "";
}
