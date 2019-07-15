package ru.pflb.homework.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Аннотация для определения имени страницы.
 */
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Pages.class)
public @interface Page {

    String value();
}
