package ru.practicum.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Mapper {
    public ViewStats toViewStats(EndpointHit endpointHit, int hits) {
        return ViewStats.builder()
                .app(endpointHit.getApp())
                .hits(endpointHit.getId())
                .uri(endpointHit.getUri())
                .hits(hits)
                .build();
    }
}
