package ru.pflb.homework.exceptions;

public class MyUncheckedException extends MyException {
    public MyUncheckedException(String message){
        super(message);
    }
    public MyUncheckedException(String message, Throwable t){
        super(message);
        this.setStackTrace(t.getStackTrace());
    }
}
