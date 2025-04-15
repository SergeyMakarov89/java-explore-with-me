package ru.practicum.ExploreWithMe.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ExploreWithMe.compilation.CompilationService;
import ru.practicum.ExploreWithMe.compilation.dto.CompilationDto;
import ru.practicum.ExploreWithMe.compilation.dto.NewCompilationDto;
import ru.practicum.ExploreWithMe.compilation.dto.UpdateCompilationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto createCompilation(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
