package ru.practicum.utility;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.enums.EventSorting;

@UtilityClass
public class PageUtility {
    public static PageRequest pagination(int from, int size) {
        int page = from < size ? 0 : from / size;
        return PageRequest.of(page, size);
    }

    public static PageRequest pagination(int from, int size, EventSorting sort) {
        int page = from < size ? 0 : from / size;
        String sorting;
        switch (sort) {
            case EVENT_DATE:
                sorting = "eventDate";
                break;
            case VIEWS:
                sorting = "view";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + sort);
        }
        return PageRequest.of(page, size, Sort.by(sorting).descending());
    }
}
