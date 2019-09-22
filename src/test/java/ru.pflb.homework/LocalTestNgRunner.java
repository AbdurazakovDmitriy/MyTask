package ru.pflb.homework;

import cucumber.api.testng.TestNGCucumberRunner;
import gherkin.events.PickleEvent;

public class LocalTestNgRunner extends TestNGCucumberRunner implements Cloneable {
    public LocalTestNgRunner(Class clazz) {
        super(clazz);
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }


}
