package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
@RequestMapping("/comments")
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{id}")
    public CommentDto getComment(@PathVariable Long id) {
        log.info("Get comment with id - {}", id);
        return commentService.getComment(id);
    }

    @GetMapping("/all/{eventId}")
    public List<CommentDto> getCommentList(@PathVariable Long eventId,
                                           @RequestParam(defaultValue = "NEW") CommentSorting sorting,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeStart,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                               LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                           @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get comments by: eventId - {}, sorting - {}, rangeStart - {}, rangeEnd - {}, from - {}, " +
                "size - {}", eventId, sorting, rangeStart, rangeEnd, from, size);
        return commentService.getCommentList(eventId, sorting, rangeStart, rangeEnd, from, size);
    }
}
