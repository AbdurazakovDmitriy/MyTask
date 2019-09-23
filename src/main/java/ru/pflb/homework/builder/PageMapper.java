package ru.pflb.homework.builder;

import org.apache.commons.io.FileUtils;
import ru.pflb.homework.annotations.Page;
import ru.pflb.homework.annotations.Pages;
import ru.pflb.homework.utils.CustomReflection;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PageMapper {
    private static final  Map<String, Class<?>> classMap;
    private static final Map<Class<?>, Object> objectClass;

    static {
        classMap = new HashMap<>();
        objectClass = new HashMap<>();
        populatePage();
    }


    @SuppressWarnings("unchecked")
    public static synchronized Object getPage(String className) {
        if(className==null)
            return null;
        else if(!classMap.keySet().contains(className))
            return null;
        Class clazz = classMap.get(className);
        Object instance;
        if((instance = objectClass.get(clazz))!=null) {
            return instance;
        } else {
            Object page = CustomReflection.createNewInstanceOr(clazz,null);
            objectClass.put(clazz,page);
            return page;
        }
    }

    private static synchronized void populatePage() {
        String separator = System.getProperty("file.separator");
        String pagesPath = System.getProperty("user.dir")+separator+"src" + separator+"main";
        Collection<File> files = FileUtils.listFiles(new File(pagesPath), new String[] {"java"}, true);
        String sep = File.separator;
        Set<Class> pageClasses = files.stream()
            .map(o -> o.getPath().substring(o.getPath().indexOf("ru"+sep+"pflb")).replaceAll(sep+sep,"\\.").replaceAll(".java","")).filter(o -> {
                try {
                    return Class.forName(o).isAnnotationPresent(Page.class)||Class.forName(o).isAnnotationPresent(Pages.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }).map(o -> {
                try {
                    return Class.forName(o);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toSet());
        for (Class pageClass : pageClasses) {
            if (pageClass.isAnnotationPresent(Pages.class)) {
                for (Page page : ((Pages) pageClass.getAnnotation(Pages.class)).value()) {
                    classMap.put(page.value(), pageClass);
                }
            }
        }
        pageClasses.stream().filter(o -> o.isAnnotationPresent(Page.class)).forEach(o -> classMap.put(((Page) o.getAnnotation(Page.class)).value(), o));
    }
}
