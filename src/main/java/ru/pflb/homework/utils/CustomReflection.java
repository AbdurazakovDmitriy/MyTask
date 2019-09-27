package ru.pflb.homework.utils;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Кастамный класс для работы с рефлексией
 */
public class CustomReflection {


    public static Set<Method> getMethods(Class clazz) {
        Set<Method> methods = new HashSet<>();
        if(clazz.getSuperclass()!=Object.class) {
            methods.addAll(getMethods(clazz.getSuperclass()));
        }
        methods.addAll(Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toSet()));
        methods.forEach(o->o.setAccessible(true));
        return methods;
    }

    public static Set<Constructor> getConstructors(Class clazz) {
        Set<Constructor> constructors = Arrays.stream(clazz.getDeclaredConstructors()).collect(Collectors.toSet());
        constructors.forEach(o->o.setAccessible(true));
        return constructors;
    }

    private static Optional<Method> getOptionalMethod(Class clazz, String methodName, Class... argTypes) {
        BiPredicate<Class[], Class[]> argMatcher = (a1, a2) -> {
            if (a1 == a2) {
                return true;
            }
            if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                return false;
            }
            for (int i = 0; i < a1.length; i++) {
                if (!a1[i].equals(a2[i])) {
                    return false;
                }
            }
            return true;
        };
        return getMethods(clazz).stream().filter(o -> o.getName().equals(methodName) && argMatcher.test(argTypes, o.getParameterTypes()))
                .findFirst();
    }
    public static Set<Field> getFields(Class clazz) {
        Set<Field> result = new HashSet<>();
        if (clazz.getSuperclass() != Object.class) {
            result.addAll(getFields(clazz.getSuperclass()));
        }
        result.addAll(Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList()));
        result.forEach(o -> o.setAccessible(true));
        return result;
    }

    public static Method getMethod(Class clazz, String methodName, Class... argTypes) throws NoSuchMethodException {
        return getOptionalMethod(clazz, methodName, argTypes).orElseThrow(NoSuchMethodException::new);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeOr(Object object, String methodName, T defaultResult, Object... args) {
        try {
            Class[] argTypes = new Class[args.length];
            Arrays.parallelSetAll(argTypes, o -> args[o].getClass());
            return args.length == 0 ? (T) getMethod(object.getClass(), methodName).invoke(object) :
                    (T) getMethod(object.getClass(), methodName, argTypes).invoke(object, args);
        } catch (Exception e) {
            return defaultResult;
        }
    }

    private static Optional<Constructor> getOptionalConstructor(Class clazz, Class... argTypes) {
        BiPredicate<Class[], Class[]> argMatcher = (a1, a2) -> {
            if (a1 == a2) {
                return true;
            }
            if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                return false;
            }
            for (int i = 0; i < a1.length; i++) {
                if (!a1[i].equals(a2[i]) && !a2[i].isAssignableFrom(a1[i])) {
                    return false;
                }
            }
            return true;
        };
        return getConstructors(clazz).stream().filter(o -> argMatcher.test(argTypes, o.getParameterTypes())).findFirst();
    }

    @SuppressWarnings( {"unchecked", "OptionalGetWithoutIsPresent"})
    public static <T> T createNewInstanceOr(Class<T> clazz, T defaultValue, Object... args) {
        try {
            if (args.length == 0) {
                return (T) getOptionalConstructor(clazz).get().newInstance();
            }
            Class[] types = new Class[args.length];
            Arrays.stream(args).map(Object::getClass).collect(Collectors.toList()).toArray(types);
            return (T) getOptionalConstructor(clazz, types).get().newInstance(args);
        } catch (Exception e) {
            if (defaultValue != null) {
                return defaultValue;
            }
            throw new RuntimeException(e);
        }
    }

    public static Class getClazz(Class clazz, String className) {
        Set<Class> classes = Arrays.stream(clazz.getDeclaredClasses()).collect(Collectors.toSet());
        return classes.stream().filter(o->o.getSimpleName().equals(className)).findFirst().get();
    }
    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        return getOptionalField(clazz, fieldName).orElseThrow(NoSuchFieldException::new);
    }
    private static Optional<Field> getOptionalField(Class clazz, String fieldName) {
        BiPredicate<Class[], Class[]> argMatcher = (a1, a2) -> {
            if (a1 == a2) {
                return true;
            }
            if ((a1 == null || a2 == null) || (a1.length != a2.length)) {
                return false;
            }
            for (int i = 0; i < a1.length; i++) {
                if (!a1[i].equals(a2[i])) {
                    return false;
                }
            }
            return true;
        };
        return getFields(clazz).stream().filter(o -> o.getName().equals(fieldName)).findFirst();
    }


    @Contract("null, _, _, -> param3")
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValueOr(Object object, String fieldName, T defaultResult) {
        try {
            return (T) getField(object.getClass(), fieldName).get(object);
        } catch (Exception e) {
            return defaultResult;
        }
    }
}
