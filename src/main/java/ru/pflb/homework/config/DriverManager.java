package ru.pflb.homework.config;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.service.DriverService;
import ru.pflb.homework.builder.StaxStreamProcessor;
import ru.pflb.homework.utils.CustomLogger;
import ru.pflb.homework.utils.CustomReflection;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public final class DriverManager {
    private static ThreadLocal<Map<String, WebDriver>> drivers;

    static {
        drivers = new ThreadLocal<>();
    }

    //todo sessionId

    public static synchronized WebDriver get(String driverType) {
        if (driverType != null && !driverType.equals("") && drivers.get() != null && drivers.get().containsKey(driverType)) {
            return drivers.get().get(driverType);
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        System.setProperty(reader.getAttributeValue(null, "key"), reader.getAttributeValue(null, "filePath"));
                        String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type")
                            .toLowerCase()
                            .replaceAll("driver", ""), reader.getAttributeValue(null, "type"));
                        Class<?> clazz = Class.forName(className);
                        WebDriver driver = (WebDriver) clazz.getConstructor().newInstance();
                        Map<String, WebDriver> driverMap = new HashMap<>();
                        driverMap.put(driverType, driver);
                        drivers.set(driverMap);
                    }
                }
            } catch (XMLStreamException | IOException | InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return drivers.get().get(driverType);
        }
    }


    public static synchronized WebDriver getDW(String driverType) throws IOException, XMLStreamException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (driverType != null && !driverType.equals("") && drivers.get() != null && drivers.get().containsKey(driverType)) {
            return drivers.get().get(driverType);
        } else {
            try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) { //todo забуфферизировать файл
                XMLStreamReader reader = processor.getReader();
                while (reader.hasNext()) {
                    int event = reader.next();
                    if (event == XMLEvent.START_ELEMENT && reader.getLocalName().equals("Driver") && reader.getAttributeValue(null, "type").equals(driverType)) {
                        String driverTypeName = reader.getAttributeValue(null, "type");
                        String shortDriverType = driverTypeName.substring(0, driverTypeName.indexOf("Driver"));
                        String driverOptionsName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + shortDriverType + "Options";
                        Class<?> driverOptionsClass = Class.forName(driverOptionsName);
                        Object driverOptions = CustomReflection.createNewInstanceOr(driverOptionsClass,null);
                        CustomLogger.debug(String.format("Created driverOptions '%s'", driverOptionsName));//ChromeOptions или FireFoxOptions
                        String driverServiceName = "org.openqa.selenium." + shortDriverType.toLowerCase() + "." + reader.getAttributeValue(null, "serviceType");
                        Class<?> driverServiceClass = Class.forName(driverServiceName);
                        Class driverBuilderClass = CustomReflection.getClazz(driverServiceClass,"Builder");
                        Object driverBuilder = CustomReflection.createNewInstanceOr(driverBuilderClass,null);
                        CustomReflection.invokeOr(driverBuilder,"usingDriverExecutable", null, new File(reader.getAttributeValue(null, "filePath")));
                        Object driverService = ((DriverService.Builder) driverBuilder).build();
                        CustomLogger.debug(String.format("Created driverService '%s'",driverServiceName));
                        CustomReflection.invokeOr(driverOptions,"setPageLoadStrategy", null,PageLoadStrategy.NORMAL);
                        CustomReflection.invokeOr(driverOptions,"addArguments", null,"--start-maximized");
                        String className = String.format("org.openqa.selenium.%s.%s", reader.getAttributeValue(null, "type")
                            .toLowerCase()
                            .replaceAll("driver", ""), reader.getAttributeValue(null, "type"));
                        Class<?> clazz = Class.forName(className);
                        WebDriver driver = (WebDriver)CustomReflection.createNewInstanceOr(clazz,null, driverService, driverOptions);
                        Map<String, WebDriver> driverMap = new HashMap<>();
                        driverMap.put(driverType, driver);
                        drivers.set(driverMap);
                    }
                }
            }
        }
        return drivers.get().get(driverType);
    }

}
