package ru.pflb.homework.stepdefinitions;

import cucumber.api.java.bg.И;

public class MyStepdefs {
    @И("выполнена авторизация")
    public void auth() {
        System.out.println("Авторизация");
    }
}
