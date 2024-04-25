package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.enums.CommentSorting;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto getComment(Long id);

    List<CommentDto> getCommentList(Long eventId, CommentSorting sorting, LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd, Integer from, Integer size);

    void deleteCommentByAdmin(Long id);

    CommentDto moderateComment(Long id, Boolean approve);

    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    void deleteCommentByUser(Long userId, Long commentId);

    CommentDto editComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    List<CommentDto> getUserComments(Long userId, Long eventId, CommentSorting commentSorting,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                     Integer size);

    CommentDto getAuthorizedComment(Long id);

    List<CommentDto> getAdminCommentList(Long eventId, CommentSorting sorting, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Integer from, Integer size);
}
