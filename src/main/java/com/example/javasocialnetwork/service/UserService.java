package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.NotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CacheService cacheService;

    @Autowired
    public UserService(UserRepository userRepository,
                       GroupRepository groupRepository,
                       CacheService cacheService) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.cacheService = cacheService;
    }

    public List<UserWithPostsAndGroupsDto> findByPostContent(String content) {
        String cacheKey = "users_by_post_content_" + content;
        return (List<UserWithPostsAndGroupsDto>) cacheService.get(cacheKey)
                .orElseGet(() -> {
                    logger.info("[DB] Fetching users_by_post_content from database");
                    List<UserWithPostsAndGroupsDto> result = userRepository.findAllByPostContent(content)
                            .stream()
                            .map(UserWithPostsAndGroupsDto::toModel)
                            .toList();
                    cacheService.put(cacheKey, result);
                    return result;
                });
    }

    public List<UserWithPostsAndGroupsDto> searchUsersByUsername(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream().map(UserWithPostsAndGroupsDto::toModel).toList();
    }

    public User registration(User user) throws UserAlreadyExistException {
        if (userRepository.existsByUsername(user.getUserName())) {
            throw new UserAlreadyExistException("User already exists")
                    .addDetail("username", user.getUserName());
        }
        User savedUser = userRepository.save(user);
        cacheService.invalidateUserCache();
        return savedUser;
    }

    public UserWithPostsAndGroupsDto getOne(Long id) {
        return userRepository.findById(id)
                .map(UserWithPostsAndGroupsDto::toModel)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id)
                        .addDetail("userId", id));
    }

    public Long delete(Long id) throws NotFoundException {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found")
                    .addDetail("userId", id);
        }
        userRepository.deleteById(id);
        cacheService.invalidateUserCache();
        return id;
    }

    public User getUserById(Long id) throws NotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException("User with this id not exist!!!");
        }
    }

    public void addUserToGroup(Long userId, Long groupId) throws NotFoundException,
            GroupNotFoundException {
        User user = getUserById(userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));

        user.addGroup(group);
        userRepository.save(user);
        cacheService.invalidateUserCache();
    }

    public void removeUserFromGroup(Long userId, Long groupId) throws NotFoundException,
            GroupNotFoundException {
        User user = getUserById(userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));
        user.removeGroup(group);
        userRepository.save(user);
        cacheService.invalidateUserCache();
    }

    public Set<Group> getUserGroups(Long userId) throws NotFoundException {
        return getUserById(userId).getGroups();
    }

    public void updateUser(Long id, User updatedUser) throws NotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with this ID not exist!!!"));

        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setPassword(updatedUser.getPassword());

        userRepository.save(existingUser);
        cacheService.invalidateUserCache();
    }
}

