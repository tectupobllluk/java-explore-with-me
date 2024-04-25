package ru.practicum.checks;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.categories.repository.CategoryRepository;
import ru.practicum.compilations.repository.CompilationRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.users.model.User;
import ru.practicum.users.repository.UserRepository;

@Component
@AllArgsConstructor
public class EntityCheck {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CommentRepository commentRepository;

    public void checkUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User not found with id: %s", id));
        }
    }

    public User getUserOrNotFound(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User not found with id: %s", id)));
    }

    public void checkCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category not found with id: %s", id));
        }
    }

    public void checkEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Event not found with id: %s", id));
        }
    }

    public Event getEventOrNotFound(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event not found with id: %s", id)));
    }

    public void checkCompilation(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Compilation not found with id: %s", id));
        }
    }

    public void checkComments(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new NotFoundException(String.format("Comment not found with id: %s", id));
        }
    }

    public Comment getCommentOrNotFound(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment not found with id: %s", id)));
    }

}
