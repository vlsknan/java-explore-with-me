package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.dto.ViewStatDto;
import ru.practicum.repo.StatRepository;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StatService {
    final StatRepository statRepository;

    public void save(EndpointHit endpointHit) {
        //statRepository.save(endpointHit);
    }

    /*
    start - Дата и время начала диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
    end - Дата и время конца диапазона за который нужно выгрузить статистику (в формате "yyyy-MM-dd HH:mm:ss")
    uris - Список uri для которых нужно выгрузить статистику
    unique - Нужно ли учитывать только уникальные посещения (только с уникальным ip)
     */
    public ViewStatDto findStat(String start, String end, String[] uris, boolean unique) {

        return null;
    }
}
