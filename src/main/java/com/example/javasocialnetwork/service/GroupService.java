package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.dto.GroupWithUsersDto;
import com.example.javasocialnetwork.entity.Groups;
import com.example.javasocialnetwork.entity.Users;
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

    @Autowired
    public GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public Groups registration(Groups group) throws GroupAlreadyExistException {
        if (groupRepository.findByName(group.getName()) != null) {
            throw new GroupAlreadyExistException("Group with this name already exists!!!");
        }
        return groupRepository.save(group);
    }

    public GroupWithUsersDto getOne(Long id) throws GroupNotFoundException {
        Groups group = groupRepository.findWithUsersById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!"));
        return GroupWithUsersDto.toModel(group);
    }

    @Transactional
    public void deleteGroup(Long groupId) throws GroupNotFoundException {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Set<Users> users = new HashSet<>(group.getUsers());

        for (Users user : users) {
            user.removeGroup(group);
            userRepository.save(user);
        }

        groupRepository.delete(group);
    }

    public void updateGroup(Long id, Groups updatedGroup) throws GroupNotFoundException {
        Groups existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this ID does not exist!!!"));
        existingGroup.setName(updatedGroup.getName());

        groupRepository.save(existingGroup);
    }
}
