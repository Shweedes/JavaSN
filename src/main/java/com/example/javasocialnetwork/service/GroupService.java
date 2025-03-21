package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.dto.GroupWithUsersDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupAlreadyExistException;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final CacheService cacheService;

    @Autowired
    public GroupService(GroupRepository groupRepository,
                        UserRepository userRepository,
                        CacheService cacheService) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.cacheService = cacheService;
    }

    public Group registration(Group group) throws GroupAlreadyExistException {
        if (groupRepository.findByName(group.getName()) != null) {
            throw new GroupAlreadyExistException("Group with this name already exists!!!");
        }

        cacheService.invalidateUserCache();
        return groupRepository.save(group);
    }

    public GroupWithUsersDto getOne(Long id) throws GroupNotFoundException {
        Group group = groupRepository.findWithUsersById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!"));
        return GroupWithUsersDto.toModel(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) throws GroupNotFoundException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Set<User> users = new HashSet<>(group.getUsers());

        for (User user : users) {
            user.removeGroup(group);
            userRepository.save(user);
        }

        groupRepository.delete(group);
        cacheService.invalidateUserCache();
    }

    public void updateGroup(Long id, Group updatedGroup) throws GroupNotFoundException {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this ID does not exist!!!"));
        existingGroup.setName(updatedGroup.getName());

        groupRepository.save(existingGroup);

        cacheService.invalidateUserCache();
    }
}
