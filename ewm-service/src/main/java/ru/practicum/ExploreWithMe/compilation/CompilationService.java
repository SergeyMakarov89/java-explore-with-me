package ru.practicum.ExploreWithMe.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.ExploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.ExploreWithMe.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ExploreWithMe.event.Event;
import ru.practicum.ExploreWithMe.event.EventRepository;
import ru.practicum.ExploreWithMe.exception.NotFoundException;

import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.by;

@Service
@RequiredArgsConstructor
public class CompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = eventRepository.findByIdIn(newCompilationDto.getEvents());

        return CompilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events)));
    }

    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Такой подборки не существует");
        }

        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow();

        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }

        if (updateCompilationRequest.getTitle() != null && !(updateCompilationRequest.getTitle().isEmpty())) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }

        if (updateCompilationRequest.getEvents() != null && !(updateCompilationRequest.getEvents().isEmpty())) {
            compilation.setEvents(eventRepository.findByIdIn(updateCompilationRequest.getEvents().stream().toList()));
        }

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public void deleteCompilation(Long compilationId) {
        compilationRepository.deleteById(compilationId);
    }

    public CompilationDto getCompilationById(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new NotFoundException("Такой подборки не существует");
        }

        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow();

        return CompilationMapper.toCompilationDto(compilation);
    }

    public List<CompilationDto> publicSearchCompilation(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, by("id").ascending());

        return compilationRepository.publicSearchCompilation(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .toList();
    }
}
