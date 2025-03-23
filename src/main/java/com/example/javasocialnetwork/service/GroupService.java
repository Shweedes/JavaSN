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

    public Group registration(Group group) {
        if (groupRepository.findByName(group.getName()) != null) {
            throw new GroupAlreadyExistException("Group already exists")
                    .addDetail("groupName", group.getName());
        }
        cacheService.invalidateUserCache();
        return groupRepository.save(group);
    }

    public GroupWithUsersDto getOne(Long id) {
        Group group = groupRepository.findWithUsersById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found")
                        .addDetail("groupId", id));
        return GroupWithUsersDto.toModel(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found")
                        .addDetail("groupId", groupId));

        Set<User> users = new HashSet<>(group.getUsers());
        users.forEach(user -> {
            user.removeGroup(group);
            userRepository.save(user);
        });

        groupRepository.delete(group);
        cacheService.invalidateUserCache();
    }

    public void updateGroup(Long id, Group updatedGroup) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group not found")
                        .addDetail("groupId", id));

        existingGroup.setName(updatedGroup.getName());
        groupRepository.save(existingGroup);
        cacheService.invalidateUserCache();
    }
}
