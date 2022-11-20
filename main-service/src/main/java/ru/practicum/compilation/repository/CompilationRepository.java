package ru.practicum.compilation.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.dto.EventShortOutDto;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Integer> {
    @Override
    List<Compilation> findAllByPinned(boolean pinned, PageRequest page);
}
