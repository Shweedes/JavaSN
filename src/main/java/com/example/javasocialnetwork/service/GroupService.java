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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private static final Logger logger = LoggerFactory.getLogger(GroupService.class);
    private static final String GROUP_NOT_FOUND = "Group not found";
    private static final String GROUP_ID = "groupId";
    private static final String GROUP = "group_";
    private static final String GROUPS = "groups_";
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

    public List<GroupWithUsersDto> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(GroupWithUsersDto::toModel)
                .collect(Collectors.toList());
    }

    public GroupWithUsersDto getOne(Long id) {
        String cacheKey = GROUP + id;

        return (GroupWithUsersDto) cacheService.get(cacheKey)
                .orElseGet(() -> {
                    logger.info("[DB] Fetching group from database by id: {}", id);

                    Group group = groupRepository.findWithUsersById(id)
                            .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND)
                                    .addDetail(GROUP_ID, id));

                    GroupWithUsersDto dto = GroupWithUsersDto.toModel(group);
                    cacheService.put(cacheKey, dto);
                    return dto;
                });
    }

    public Group registration(Group group) {
        if (groupRepository.findByName(group.getName()) != null) {
            throw new GroupAlreadyExistException("Group already exists")
                    .addDetail("groupName", group.getName());
        }

        Group savedGroup = groupRepository.save(group);

        // Инвалидация общего кеша групп
        cacheService.evictByPrefix(GROUPS);
        logger.info("[CACHE] Invalidated groups cache after registration");

        return savedGroup;
    }

    @Transactional
    public void deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND)
                        .addDetail(GROUP_ID, groupId));

        Set<User> users = new HashSet<>(group.getUsers());
        users.forEach(user -> {
            user.removeGroup(group);
            userRepository.save(user);
            // Инвалидация кеша пользователей
            cacheService.evict("user_" + user.getId());
        });

        groupRepository.delete(group);

        // Инвалидация кеша группы и общего кеша
        cacheService.evict(GROUP + groupId);
        cacheService.evictByPrefix(GROUPS);
        cacheService.invalidateUserCache();
        logger.info("[CACHE] Invalidated group {} and user caches", groupId);
    }

    public void updateGroup(Long id, Group updatedGroup) {
        Group existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException(GROUP_NOT_FOUND)
                        .addDetail(GROUP_ID, id));

        existingGroup.setName(updatedGroup.getName());
        groupRepository.save(existingGroup);

        // Инвалидация кеша группы и поиска по имени
        cacheService.evict(GROUP + id);
        cacheService.evict("group_by_name_" + existingGroup.getName());
        cacheService.evictByPrefix(GROUPS);
        logger.info("[CACHE] Invalidated group {} cache after update", id);
    }
}
