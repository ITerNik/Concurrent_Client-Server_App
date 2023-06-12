package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для считывания экземпляров коллекции и других составных объектов.
 * Помечает методы-сеттеры класса, которые будут использоваться для заполнения полей нового экземпляра.
 * В дополнение содержит локализированные названия параметров {@link this#field()} для запроса,
 * список возможных вариантов {@link this#variants()}, а также порядок установки полей {@link this#order()}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Builder {
    String field() default "";
    String[] variants() default {};
    int order();
}

