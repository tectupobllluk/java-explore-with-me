package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;
import ru.practicum.enums.CommentSorting;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Registered user with id - {} create comment - {} on event with id - {}",
                userId, newCommentDto, eventId);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable Long userId,
                                    @PathVariable Long commentId) {
        log.info("Registered user with id - {} deleting comment with id - {}", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    @PatchMapping("/{commentId}")
    public CommentDto editComment(@PathVariable Long userId,
                                  @PathVariable Long commentId,
                                  @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Registered user with id - {} edit comment with id - {} by {}", userId, commentId, newCommentDto);
        return commentService.editComment(userId, commentId, newCommentDto);
    }

    @GetMapping
    public List<CommentDto> getUserComments(@PathVariable Long userId,
                                            @RequestParam(required = false) Long eventId,
                                            @RequestParam(defaultValue = "NEW") CommentSorting commentSorting,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                LocalDateTime rangeEnd,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Registered user with id - {}, get his own comments by: eventId - {}, sorting - {}, " +
                "rangeStart - {}, rangeEnd - {}, from - {}, size - {}", userId, eventId, commentSorting,
                rangeStart, rangeEnd, from, size);
        return commentService.getUserComments(userId, eventId, commentSorting, rangeStart, rangeEnd, from, size);
    }

    @GetMapping("/{id}")
    public CommentDto getUserComment(@PathVariable Long id) {
        log.info("Registered user get comment with id - {}", id);
        return commentService.getAuthorizedComment(id);
    }
}
