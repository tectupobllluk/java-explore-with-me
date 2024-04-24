package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.checks.EntityCheck;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.dto.UpdateCompilationRequest;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.mappers.CompilationMapper;
import ru.practicum.mappers.EventMapper;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EntityCheck entityCheck;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (pinned.equals(false)) {
            return compilationRepository.findAll(page).toList().stream()
                    .map(CompilationMapper::toCompilationDto)
                    .collect(Collectors.toList());
        }
        return compilationRepository.findByPinned(pinned, page).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation not found with id: " + compId));
        Set<EventShortDto> events = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toSet());
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(events);
        return compilationDto;
    }

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto compilation) {
        Compilation compilationToSave = CompilationMapper.toCompilation(compilation);
        if (compilation.getEvents() == null || compilation.getEvents().isEmpty()) {
            compilationToSave.setEvents(Collections.emptySet());
        } else {
            compilationToSave.setEvents(eventRepository.findByIdIn(compilation.getEvents()));
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToSave));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        entityCheck.checkCompilation(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationDto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id - %s not found", compId)));

        if (compilationDto.getEvents() == null || compilationDto.getEvents().isEmpty()) {
            compilation.setEvents(Collections.emptySet());
        } else {
            compilation.setEvents(eventRepository.findByIdIn(compilationDto.getEvents()));
        }
        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        compilation.setPinned(compilationDto.getPinned());

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }
}
