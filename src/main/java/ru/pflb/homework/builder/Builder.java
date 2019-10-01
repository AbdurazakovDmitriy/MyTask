package ru.pflb.homework.builder;

import com.sun.source.util.JavacTask;
import org.apache.commons.io.FileUtils;
import ru.pflb.homework.utils.CustomClassLoader;
import ru.pflb.homework.elementModels.ElementPattern;

import javax.tools.JavaCompiler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Builder {
    //driver types
    public static final String CHROME = "Chrome";
    public static final String FIREFOX = "Firefox";

    //page attributes
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String CHROME_PATH = "ChromePath";
    private static final String FIREFOX_PATH = "FirefoxPath";



    private static synchronized List<ElementPattern> parsePage(String pageName) {
        List<ElementPattern> elementList = new ArrayList<>();
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {       // while not end of XML
                int event = reader.next();   // read next event

                if (event == XMLEvent.START_ELEMENT && pageName.equals(reader.getAttributeValue(null, "name"))) {
                    int unclosedTags = 1;
                    while (reader.hasNext() && unclosedTags != 0) {
                        int elementEvent = reader.next();
                        //подсчет разницы между количеством открывающих и закрывающих тегов
                        if (reader.getEventType() == XMLEvent.START_ELEMENT) {
                            unclosedTags += 1;
                        } else if (reader.getEventType() == XMLEvent.END_ELEMENT) {
                            unclosedTags -= 1;
                        }
                        if (elementEvent == XMLEvent.START_ELEMENT) {
                            elementList.add(ElementPattern.of(reader));
                        }
                    }
                }
            }
        } catch (XMLStreamException | IOException e) {
            e.printStackTrace();
        }
        return elementList;
    }


    public static synchronized Class buildPage(String driverType, String pageName) {
        File file = new File(String.format("./src/main/java/ru/pflb/homework/page/%s",driverType.replaceAll("Driver","")));
        file.mkdir();
        String basicPage = null;
        try {
            basicPage = FileUtils.readFileToString(new File("./src/main/resources/PagePattern.pattern"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        basicPage = Objects.requireNonNull(basicPage)
                .replaceFirst("@Page\\(\"(.*)\"\\)", "@Page\\(\"" + pageName + "\"\\)")
                .replaceAll("driverType", driverType)
                .replaceAll("TYPEPATH",driverType.replaceAll("Driver",""));
        basicPage = basicPage.replaceAll("PageName", pageName);
        int indexOfSelectableElement = basicPage.indexOf('{');
        StringBuilder stringBuilder = new StringBuilder(basicPage);
        List<ElementPattern> elementList = parsePage(pageName);
        String element = "\n\r@Element(\"elementNameHolder\")\n\rpublic typeHolder elementNameHolder(){\n\r return elementFactory(\"elementNameHolder\",typeHolder.class, By.xpath(\"pathHolder\"));\n\r}\n\r";
        for (ElementPattern elementPattern : elementList) {
            stringBuilder.insert(indexOfSelectableElement + 1, element.replaceAll("elementNameHolder", elementPattern.getAttribute("name"))
                    .replaceAll("pathHolder", elementPattern.getAttribute(String.format("%sPath", driverType.replaceAll("Driver","").toLowerCase())))
                    .replaceAll("typeHolder", elementPattern.getAttribute("type"))
                    .replaceAll("driverType",driverType));
        }
        String pageClassname = String.format("./src/main/java/ru/pflb/homework/page/%s/%s.java",driverType.replaceAll("Driver",""), pageName);
        File pageFile = new File(pageClassname);
        try {
            FileUtils.write(pageFile, stringBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = System.getProperties();
        String sep = properties.getProperty("file.separator");
        String jrePath = properties.getProperty("java.home");
        String classFileDirectory = String.format("target%sclasses", sep, sep);

        String format=System.getProperty("os.name").toLowerCase().contains("windows")
                ? "\"%s\""
                : "%s";
        String javac = jrePath + sep + "bin" + sep + "javac";
        javac=String.format(format,javac);
        String classPath=System.getProperty("java.class.path").replaceAll("\\s","\\\\ ");
        String command = String.format("%s -d %s -sourcepath src -classpath \"%s\" %s", javac, classFileDirectory, classPath, pageFile.getPath());
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Objects.requireNonNull(process).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CustomClassLoader customClassLoader = new CustomClassLoader();
        try {
            return customClassLoader.findClass(String.format("%s%sru%spflb%shomework%spage%s%s%s%s", classFileDirectory, sep, sep, sep, sep,sep,driverType.replaceAll("Driver",""), sep, pageName));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Удаляет все созданные страницы
     *
     * @throws IOException
     * @throws XMLStreamException
     */
    public static void destroyPages() throws IOException, XMLStreamException {
        try (StaxStreamProcessor processor = new StaxStreamProcessor(Files.newInputStream(Paths.get("./src/main/resources/PageXmlSources.xml")))) {
            XMLStreamReader reader = processor.getReader();
            while (reader.hasNext()) {
                int event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    String tagName = reader.getLocalName();
                    if (tagName.equals("Page")) {
                        String pageName = reader.getAttributeValue(null, "name");
                        File javaFile = new File(String.format("./src/main/java/ru/pflb/homework/page//%s.java", pageName));
                        javaFile.deleteOnExit();
                    }
                }
            }
        }
    }
}
