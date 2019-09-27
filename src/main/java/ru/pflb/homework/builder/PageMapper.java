package ru.pflb.homework.builder;

import org.apache.commons.io.FileUtils;
import ru.pflb.homework.annotations.Page;
import ru.pflb.homework.annotations.Pages;
import ru.pflb.homework.page.AbstractPage;
import ru.pflb.homework.utils.CustomReflection;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PageMapper {
    private static class Key{
        private Key(Class<? extends AbstractPage> clazz, String sessionId) {
            this.clazz = clazz;
            this.sessionId = sessionId;
        }

        Class<? extends AbstractPage> clazz;
        String sessionId;
        @Override
        public boolean equals(Object o){
            if (o==null || o.getClass()!=this.getClass())
                return false;
            Key key= (Key) o;
            return clazz.equals(key.clazz) && sessionId.equals(key.sessionId);
        }
    }
    private static volatile Map<String, Class<? extends AbstractPage>> classMap;
    private static volatile Map<Key, Object> objectClass;
    private static volatile Map<String, Object> threadlyActivePage;
    static {
        classMap = new ConcurrentHashMap<>();
        objectClass = new ConcurrentHashMap<>();
        threadlyActivePage = new ConcurrentHashMap<>();
        populatePage();
    }

    public static synchronized Object getActivePage() {
        return threadlyActivePage.get(ProcessingThread.getSessionId());
    }

    @SuppressWarnings("unchecked")
    public static synchronized Object getPage(String className) {
        if(className==null)
            return null;
        else if(!classMap.keySet().contains(className))
            return null;
        Class clazz = classMap.get(className);
        Object instance;
        Key key = new Key(clazz, ProcessingThread.getSessionId());
        if((instance = objectClass.get(key))!=null) {
            threadlyActivePage.put(ProcessingThread.getSessionId(), instance);
            return instance;
        } else {
            Object page = CustomReflection.createNewInstanceOr(clazz,null);
            objectClass.put(key,page);
            threadlyActivePage.put(ProcessingThread.getSessionId(),page);
            return page;
        }
    }

    private static synchronized void populatePage() {
        String separator = System.getProperty("file.separator");
        String pagesPath = System.getProperty("user.dir")+separator+"src" + separator+"main";
        Collection<File> files = FileUtils.listFiles(new File(pagesPath), new String[] {"java"}, true);
        String sep = File.separator;
        Set<Class> pageClasses = files.stream()
            .map(o -> o.getPath().substring(o.getPath().indexOf("ru"+sep+"pflb")).replaceAll(System.getProperty("os.name").toLowerCase().contains("mac")?sep:sep+sep,"\\.").replaceAll(".java","")).filter(o -> {
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

    public static void drop(String sessionId) {
        classMap.values().forEach(key -> objectClass.remove(new Key(key,sessionId)));
    }
}
