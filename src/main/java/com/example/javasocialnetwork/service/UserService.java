package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public UserService(UserRepository userRepository, GroupRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public List<UserWithPostsAndGroupsDto> searchUsersByUsername(String username) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(username);
        return users.stream().map(UserWithPostsAndGroupsDto::toModel).toList();
    }

    public User registration(User user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUserName()) != null) {
            throw new UserAlreadyExistException("User with this name already exists!!!");
        }
        return userRepository.save(user);
    }

    public UserWithPostsAndGroupsDto getOne(Long id) throws UserNotFoundException {
        User user = userRepository.findWithPostsAndGroupsById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this id not exist!!!"));
        return UserWithPostsAndGroupsDto.toModel(user);
    }

    public Long delete(Long id) throws UserNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with this id does not exist!!!");
        }
        userRepository.deleteById(id);
        return id;
    }

    public User getUserById(Long id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User with this id not exist!!!");
        }
    }

    public void addUserToGroup(Long userId, Long groupId) throws UserNotFoundException,
            GroupNotFoundException {
        User user = getUserById(userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));

        user.addGroup(group);
        userRepository.save(user);
    }

    public void removeUserFromGroup(Long userId, Long groupId) throws UserNotFoundException,
            GroupNotFoundException {
        User user = getUserById(userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));
        user.removeGroup(group);
        userRepository.save(user);
    }

    public Set<Group> getUserGroups(Long userId) throws UserNotFoundException {
        return getUserById(userId).getGroups();
    }

    public void updateUser(Long id, User updatedUser) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with this ID not exist!!!"));

        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setPassword(updatedUser.getPassword());

        userRepository.save(existingUser);
    }
}

