package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.service.CommentService;
import ru.practicum.enums.CommentSorting;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long id) {
        log.info("Administrator deleted comment with id - {}", id);
        commentService.deleteCommentByAdmin(id);
    }

    @PatchMapping("/{id}")
    public CommentDto moderateComment(@PathVariable Long id,
                                      @RequestParam Boolean approve) {
        log.info("Administrator moderate comment with id - {} and solution - {}", id, approve);
        return commentService.moderateComment(id, approve);
    }

    @GetMapping("/{id}")
    public CommentDto getAdminComment(@PathVariable Long id) {
        log.info("Administrator get comment with id - {}", id);
        return commentService.getAuthorizedComment(id);
    }

    @GetMapping("/all/{eventId}")
    public List<CommentDto> getAdminCommentList(@PathVariable Long eventId,
                                           @RequestParam(defaultValue = "NEW") CommentSorting sorting,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                           LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get comments by: eventId - {}, sorting - {}, rangeStart - {}, rangeEnd - {}, from - {}, " +
                "size - {}", eventId, sorting, rangeStart, rangeEnd, from, size);
        return commentService.getAdminCommentList(eventId, sorting, rangeStart, rangeEnd, from, size);
    }
}
