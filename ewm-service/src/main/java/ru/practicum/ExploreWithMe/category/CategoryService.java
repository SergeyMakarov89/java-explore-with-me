package ru.practicum.ExploreWithMe.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ExploreWithMe.category.dto.CategoryDto;
import ru.practicum.ExploreWithMe.category.dto.NewCategoryDto;
import ru.practicum.ExploreWithMe.event.EventRepository;
import ru.practicum.ExploreWithMe.exception.ConflictException;
import ru.practicum.ExploreWithMe.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));

        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с таким ID не найдена");
        }
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Категоря связана с событием, удалите событые");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с таким ID не найдена");
        }
        if (categoryRepository.existsByName(categoryDto.getName()) && (!categoryId.equals(categoryRepository.findByName(categoryDto.getName()).getId()))) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category category = categoryRepository.getById(categoryId);
        category.setName(categoryDto.getName());

        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    public CategoryDto getCategoryById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Категория с таким ID не найдена");
        }

        return CategoryMapper.toCategoryDto(categoryRepository.getById(categoryId));
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto).toList();
    }
}
