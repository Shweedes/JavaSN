package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.entity.GroupEntity;
import com.example.javasocialnetwork.entity.UserEntity;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.exception.UserNotFoundException;
import com.example.javasocialnetwork.model.User;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
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

    public UserEntity registration(UserEntity user) throws UserAlreadyExistException {
        if (userRepository.findByUsername(user.getUserName()) != null) {
            throw new UserAlreadyExistException("User with this name already exists!!!");
        }
        return  userRepository.save(user);
    }

    public User getOne(Long id) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return User.toModel(optionalUser.get());
        } else {
            throw new UserNotFoundException("User with this id not exist!!!");
        }
    }

    public Long delete(Long id) {
        userRepository.deleteById(id);
        return id;
    }

    public UserEntity getUserById(Long id) throws UserNotFoundException {
        Optional<UserEntity> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new UserNotFoundException("User with this id not exist!!!");
        }
    }

    public void addUserToGroup(Long userId, Long groupId) throws UserNotFoundException,
            GroupNotFoundException {
        UserEntity user = getUserById(userId);
        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));

        user.addGroup(group);
        userRepository.save(user);
    }

    public void removeUserFromGroup(Long userId, Long groupId) throws UserNotFoundException,
            GroupNotFoundException {
        UserEntity user = getUserById(userId);
        GroupEntity group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!!!"));

        user.removeGroup(group);
        userRepository.save(user);
    }

    public Set<GroupEntity> getUserGroups(Long userId) throws UserNotFoundException {
        return getUserById(userId).getGroups();
    }

    public void updateUser(Long id, UserEntity updatedUser) throws UserNotFoundException {
        UserEntity existingUser = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User with this ID not exist!!!"));

        existingUser.setUserName(updatedUser.getUserName());
        existingUser.setPassword(updatedUser.getPassword());

        userRepository.save(existingUser);
    }
}

