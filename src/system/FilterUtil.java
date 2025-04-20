package system;

import java.util.List;
import java.util.function.Predicate;

public class FilterUtil {
    // 7 - Generics, allowing for an easy way to filter any kind of object based on any condition
    public static <T> List<T> filter(List<T> items, Predicate<T> predicate) {
        return items.stream()
                .filter(predicate)
                .toList();
    }
}