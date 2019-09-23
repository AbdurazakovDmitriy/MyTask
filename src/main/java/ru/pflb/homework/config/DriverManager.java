package ru.pflb.homework.config;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.service.DriverService;
import ru.pflb.homework.builder.StaxStreamProcessor;
import ru.pflb.homework.utils.CustomLogger;
import ru.pflb.homework.utils.CustomReflection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public final class DriverManager {
    private static volatile Map<String, WebDriver> local;

    static {
        local = new ConcurrentHashMap<>();
    }

    public static synchronized WebDriver createWD(String driverType) {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) { //todo забуфферизировать файл
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                    String driverTypeName = reader.getAttributeValue(null, "type");
                    String shortDriverType = driverTypeName.substring(0, driverTypeName.indexOf("Driver"));
                    String driverOptionsName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + shortDriverType + "Options";
                    Class<?> driverOptionsClass = Class.forName(driverOptionsName);
                    Object driverOptions = CustomReflection.createNewInstanceOr(driverOptionsClass, null);
                    CustomLogger.debug(String.format("Created driverOptions '%s'", driverOptionsName));//ChromeOptions или FireFoxOptions
                    String driverServiceName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + reader.getAttributeValue(null, "serviceType");
                    Class<?> driverServiceClass = Class.forName(driverServiceName);
                    Class driverBuilderClass = CustomReflection.getClazz(driverServiceClass, "Builder");
                    Object driverBuilder = CustomReflection.createNewInstanceOr(driverBuilderClass, null);
                    CustomReflection.invokeOr(driverBuilder, "usingDriverExecutable", null, new File(reader.getAttributeValue(null, "filePath")));
                    Object driverService = ((DriverService.Builder) driverBuilder).build();
                    CustomLogger.debug(String.format("Created driverService '%s'", driverServiceName));
                    CustomReflection.invokeOr(driverOptions, "setPageLoadStrategy", null, PageLoadStrategy.NORMAL);
                    CustomReflection.invokeOr(driverOptions, "addArguments", null, "--start-maximized");
                    String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type")
                            .toLowerCase()
                            .replaceAll("driver", ""), reader.getAttributeValue(null, "type"));
                    Class<?> clazz = Class.forName(className);
                    WebDriver driver = (WebDriver) CustomReflection.createNewInstanceOr(clazz, null, driverService, driverOptions);
                    return driver;
                }
            }
        } catch (XMLStreamException | IOException | ClassNotFoundException e) {
            CustomLogger.fail("Не удалось создать драйвер", e);
        }

        return null;
    }

    public static synchronized WebDriver getWD(String sessionId) {
        return local.get(sessionId);
    }

    public static synchronized void setWD(String sessionId, WebDriver driver) {
        local.put(sessionId, driver);
    }

}
