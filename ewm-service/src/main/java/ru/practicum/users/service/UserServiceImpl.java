package ru.practicum.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.checks.EntityCheck;
import ru.practicum.mappers.UserMapper;
import ru.practicum.users.dto.UserDto;
import ru.practicum.users.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityCheck entityCheck;

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (ids != null) {
            return userRepository.findByIdInOrderByIdAsc(ids, page).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(page).getContent().stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto user) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(user)));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        entityCheck.checkUser(userId);
        userRepository.deleteById(userId);
    }
}
