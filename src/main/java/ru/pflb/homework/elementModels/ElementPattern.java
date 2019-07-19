package ru.pflb.homework.elementModels;
//TODO from y.skvortsov: безумное количество одинакового кода. Используй ломбок, либо рефлексию, либо мапу, либо свою модель на основе капабилок.
//Но 45 строк единственная логика которых - работа с примитивным POJO это жесть
//Аналогично в билдере не надо получать каждый аттрибут, как будто это бог, не надо к нему поименно обращаться
//Просто через форич загоняешь все через reflection либо через мапу/модель сюда

@Deprecated
public class ElementPattern {
    private String name;
    private String type;
    private String chromePath;

    private String firefoxPath;

    private String isDeprecated;

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

    public void setDeprecated(String attributeValue) {
        isDeprecated=attributeValue;
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

    public boolean isDeprecated() {
        return "true".equals(isDeprecated);
    }
}
