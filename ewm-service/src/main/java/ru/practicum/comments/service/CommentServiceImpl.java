package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.checks.EntityCheck;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.enums.CommentSorting;
import ru.practicum.enums.CommentStatus;
import ru.practicum.enums.State;
import ru.practicum.events.model.Event;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.mappers.CommentMapper;
import ru.practicum.users.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final EntityCheck entityCheck;

    @Override
    public CommentDto getComment(Long id) {
        Comment comment = entityCheck.getCommentOrNotFound(id);
        if (!comment.getStatus().equals(CommentStatus.PUBLISHED)) {
            throw new ConflictException("Unregistered user cannot get comment with MODERATION status");
        }
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentList(Long eventId, CommentSorting sorting, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Integer from, Integer size) {
        entityCheck.checkEvent(eventId);
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConflictException("Start time can't be after End time");
        }
        Pageable page = null;
        switch (sorting) {
            case NEW:
                page = PageRequest.of(from / size, size, Sort.Direction.ASC, "published");
                break;
            case OLD:
                page = PageRequest.of(from / size, size, Sort.Direction.DESC, "published");
                break;
        }

        Specification<Event> spec = Specification.where(null);

        if (rangeStart != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("published"), rangeStart));
        }

        if (rangeEnd != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("published"), rangeEnd));
        }
        spec = spec.and(((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("event").get("id"), eventId)));
        spec = spec.and(((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), CommentStatus.PUBLISHED)));

        return commentRepository.findAll(spec, page).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCommentByAdmin(Long id) {
        entityCheck.checkComments(id);
        commentRepository.deleteById(id);
    }

    @Override
    public CommentDto moderateComment(Long id, Boolean approve) {
        Comment comment = entityCheck.getCommentOrNotFound(id);
        if (approve) {
            comment.setStatus(CommentStatus.PUBLISHED);
            comment.setPublished(LocalDateTime.now());
        } else {
            comment.setStatus(CommentStatus.REJECTED);
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = entityCheck.getUserOrNotFound(userId);
        Event event = entityCheck.getEventOrNotFound(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Cannot add comment to not PUBLISHED event");
        }
        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, user, event)));
    }

    @Override
    public void deleteCommentByUser(Long userId, Long commentId) {
        entityCheck.checkUser(userId);
        Comment comment = entityCheck.getCommentOrNotFound(commentId);
        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new ConflictException("Only comment author and moderator can delete this comment");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto editComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        User user = entityCheck.getUserOrNotFound(userId);
        Comment comment = entityCheck.getCommentOrNotFound(commentId);
        if (Objects.equals(user.getId(), comment.getAuthor().getId())) {
            if (comment.getStatus() == CommentStatus.PUBLISHED) {
                if (ChronoUnit.HOURS.between(comment.getPublished(), LocalDateTime.now()) < 24) {
                    comment.setText(newCommentDto.getText());
                } else {
                    throw new ConflictException("User cannot edit comment after 24 hours of publication");
                }
            } else {
                if (comment.getStatus() == CommentStatus.REJECTED) {
                    comment.setStatus(CommentStatus.MODERATION);
                }
                comment.setText(newCommentDto.getText());
            }
        } else {
            throw new ConflictException("User cannot edit someone else comment");
        }
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getUserComments(Long userId, Long eventId, CommentSorting commentSorting,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                            Integer from, Integer size) {
        User user = entityCheck.getUserOrNotFound(userId);
        Event event;
        if (eventId != null) {
            event = entityCheck.getEventOrNotFound(eventId);
        } else {
            event = null;
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConflictException("Start time can't be after End time");
        }
        Pageable page = null;
        switch (commentSorting) {
            case NEW:
                page = PageRequest.of(from / size, size, Sort.Direction.ASC, "created");
                break;
            case OLD:
                page = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
                break;
        }
        Specification<Event> spec = Specification.where(null);
        spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("author"), user)));

        if (rangeStart != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("created"), rangeStart));
        }

        if (rangeEnd != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("created"), rangeEnd));
        }

        if (event != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("event"), event)));
        }
        return commentRepository.findAll(spec, page).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getAuthorizedComment(Long id) {
        return CommentMapper.toCommentDto(entityCheck.getCommentOrNotFound(id));
    }

    @Override
    public List<CommentDto> getAdminCommentList(Long eventId, CommentSorting sorting, LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd, Integer from, Integer size) {
        Event event;
        if (eventId != null) {
            event = entityCheck.getEventOrNotFound(eventId);
        } else {
            event = null;
        }

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ConflictException("Start time can't be after End time");
        }
        Pageable page = null;
        switch (sorting) {
            case NEW:
                page = PageRequest.of(from / size, size, Sort.Direction.ASC, "created");
                break;
            case OLD:
                page = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
                break;
        }
        Specification<Event> spec = Specification.where(null);
        if (rangeStart != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("created"), rangeStart));
        }

        if (rangeEnd != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("created"), rangeEnd));
        }

        if (event != null) {
            spec = spec.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("event"), event)));
        }
        return commentRepository.findAll(spec, page).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
