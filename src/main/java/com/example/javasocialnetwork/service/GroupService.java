package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.entity.GroupEntity;
import com.example.javasocialnetwork.entity.UserEntity;
import com.example.javasocialnetwork.exception.GroupAlreadyExistException;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.model.GroupWithUsers;
import com.example.javasocialnetwork.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public GroupEntity registration(GroupEntity group) throws GroupAlreadyExistException {
        if (groupRepository.findByName(group.getName()) != null) {
            throw new GroupAlreadyExistException("Group with this name already exists!!!");
        }
        return groupRepository.save(group);
    }

    public GroupWithUsers getOne(Long id) throws GroupNotFoundException {
        GroupEntity group = groupRepository.findWithUsersById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this id not exist!"));
        return GroupWithUsers.toModel(group);
    }

    public void deleteGroup(Long groupId) throws GroupNotFoundException {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        for (UserEntity user : group.getUsers()) {
            user.removeGroup(group);
        }
        groupRepository.delete(group);
    }

    public void updateGroup(Long id, GroupEntity updatedGroup) throws GroupNotFoundException {
        GroupEntity existingGroup = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Group with this ID does not exist!!!"));
        existingGroup.setName(updatedGroup.getName());

        groupRepository.save(existingGroup);
    }
}
