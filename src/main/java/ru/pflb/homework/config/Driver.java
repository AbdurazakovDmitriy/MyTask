package ru.pflb.homework.config;


public enum Driver {
    Chrome,
    Firefox;

    public static Driver of(String browser) {
        switch (browser.toUpperCase()){
            case "CHROME": return Chrome;
            case "FIREFOX": return Firefox;
        }
        return null;
    }
}
