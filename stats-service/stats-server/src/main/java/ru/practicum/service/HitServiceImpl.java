package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.exception.WrongDataException;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    public HitDto createHit(HitDto hitDto) {
        hitRepository.save(HitMapper.toEndpointHit(hitDto));
        return hitDto;
    }

    @Override
    public List<StatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<EndpointHit> endpointHits;
        List<StatsDto> stats;

        if (start == null || end == null) {
            throw new WrongDataException("Start date and end date are required for the request");
        }

        if (start.isAfter(end)) {
            throw new WrongDataException("Range end is before Range start");
        }

        if (uris == null || uris.isEmpty()) {
            endpointHits = hitRepository.findAllHitsBetweenDates(start, end);
        } else {
            endpointHits = hitRepository.findByTimestampAndUris(start, end, uris);
        }

        stats = HitMapper.toResponse(endpointHits, unique);

        return stats;
    }
}
