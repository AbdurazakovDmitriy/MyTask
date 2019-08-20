package ru.pflb.homework.exceptions;

public class MyAssertionException extends MyException {
    @Override
    public void resolve(){
        throw new AssertionError();
    }
}
