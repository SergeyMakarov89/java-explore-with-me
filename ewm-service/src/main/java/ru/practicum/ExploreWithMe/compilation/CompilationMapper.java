package ru.practicum.ExploreWithMe.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.ExploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.ExploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.ExploreWithMe.event.Event;
import ru.practicum.ExploreWithMe.event.EventMapper;
import ru.practicum.ExploreWithMe.event.dto.EventShortDto;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public CompilationDto toCompilationDto(Compilation compilation) {
        Set<EventShortDto> events = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(events);

        return compilationDto;
    }

    public Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned());
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setEvents(events);

        return compilation;
    }
}
