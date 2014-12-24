/**
 * DbPersian (DataBase PERSistence ANnotations)
 */

package net.dbpersian.annotations.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DbColumn
{
    String sqlName();
    String indexName() default "";
    boolean primaryKey() default false;
    boolean unique() default false;
    boolean notNull() default false;
    String fkTable() default "";
    String fkColumn() default "";
    boolean serializable() default false;
}
