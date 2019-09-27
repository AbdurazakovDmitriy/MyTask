package ru.pflb.homework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация для определения имени элементов. При использовании над методом необходимо, чтобы аннотируемый метод не имел
 * паматретров и возвращал значение
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Element {
    String value();
}
