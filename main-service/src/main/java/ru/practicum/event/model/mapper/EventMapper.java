package ru.practicum.event.model.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;
import ru.practicum.category.model.dto.CategoryDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.dto.EventFullOutDto;
import ru.practicum.event.model.dto.EventShortOutDto;
import ru.practicum.event.model.dto.NewEventInDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.dto.UserShortDto;

@UtilityClass
public class EventMapper {

    public static Event toEvent(NewEventInDto eventDto, Category category, User initiator) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .initiator(initiator)
                .category(category)
                .description(eventDto.getDescription())
                .eventDate(eventDto.getEventDate())
                .locationLatitude(eventDto.getLocation().getLat())
                .locationLongitude(eventDto.getLocation().getLon())
                .paid(eventDto.isPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.isRequestModeration())
                .title(eventDto.getTitle())
                .build();
    }

    public static EventFullOutDto toEventFullDto(Event event, CategoryDto category, UserShortDto initiator,
                                                 int confirmedRequests, int view) {
        return EventFullOutDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .location(getLocation(event.getLocationLatitude(), event.getLocationLongitude()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(view)
                .build();
    }

    public static EventShortOutDto toEventShortDto(Event event, CategoryDto category, UserShortDto initiator,
                                                   int confirmedRequests, int view) {
        return EventShortOutDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(category)
                .confirmedRequests(confirmedRequests)
                .eventDate(event.getEventDate())
                .initiator(initiator)
                .paid(event.isPaid())
                .title(event.getTitle())
                .views(view)
                .build();
    }

    private Location getLocation(float lat, float lon) {
        return Location.builder()
                .lat(lat)
                .lon(lon)
                .build();
    }
}
