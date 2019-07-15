package ru.pflb.homework.elementModels;

public class BasicElement {
    private String name;
    private String type;
    private String chromePath;
    private String firefoxPath;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setChromePath(String chromePath) {
        this.chromePath = chromePath;
    }

    public void setFirefoxPath(String firefoxPath) {
        this.firefoxPath = firefoxPath;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getChromePath() {
        return chromePath;
    }

    public String getFirefoxPath() {
        return firefoxPath;
    }
}
