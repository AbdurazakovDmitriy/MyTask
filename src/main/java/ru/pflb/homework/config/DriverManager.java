package ru.pflb.homework.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.pflb.homework.builder.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class DriverManager {
    private static WebDriver webDriver;

    private DriverManager() {
    }

    public static synchronized WebDriver get(String driverType) {
        if (driverType != null && !driverType.equals("") && webDriver != null && webDriver.getClass().getSimpleName().equals(driverType)) {
            return webDriver;
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        System.setProperty(reader.getAttributeValue(null, "key"), reader.getAttributeValue(null, "filePath"));
//                        Class<?> clazz = Class.forName(reader.getAttributeValue(null, "type"));
                        String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type").toLowerCase().replaceAll("driver",""),reader.getAttributeValue(null, "type"));
                        Class<?> clazz = Class.forName(className);
                        webDriver = (WebDriver) clazz.getConstructor().newInstance();

                    }
                }
            } catch (XMLStreamException | IOException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return webDriver;
        }
    }

    public static synchronized WebDriver get() {
        return webDriver;
    }


}
