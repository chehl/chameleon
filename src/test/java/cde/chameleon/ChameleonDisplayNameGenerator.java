package cde.chameleon;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.List;

public class ChameleonDisplayNameGenerator implements DisplayNameGenerator {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return testClass.getCanonicalName();
    }

    @Override
    public String generateDisplayNameForNestedClass(List<Class<?>> enclosingInstanceTypes, Class<?> nestedClass) {
        return nestedClass.getSimpleName();
    }

    private String capitalizeFirst(String text) {
        return !text.isEmpty() ? Character.toUpperCase(text.charAt(0)) + text.substring(1) : text;
    }

    private String highlightPrefix(String prefix, String text) {
        return text.startsWith(prefix) ? capitalizeFirst(prefix) + ":" + text.substring(prefix.length()) : text;
    }

    private String removeCamelCase(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c: text.toCharArray()) {
            sb.append(Character.isUpperCase(c) ? " " + Character.toLowerCase(c) : c);
        }
        return sb.toString();
    }

    @Override
    public String generateDisplayNameForMethod(List<Class<?>> enclosingInstanceTypes, Class<?> testClass, Method testMethod) {
        String name = removeCamelCase(testMethod.getName());
        String[] parts = name.split("_");
        if (parts.length == 3) {
            String given = highlightPrefix("given", parts[0]);
            String when = highlightPrefix("when", parts[1]);
            String then = highlightPrefix("then", parts[2]);
            return given + " | " + when + " | " + then;
        } else {
            return name;
        }
    }
}
