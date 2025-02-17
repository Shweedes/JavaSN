package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.entity.GroupEntity;
import com.example.javasocialnetwork.entity.UserEntity;
import com.example.javasocialnetwork.exception.GroupAlreadyExistException;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

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

    public GroupEntity getOne(Long id) throws GroupNotFoundException {
        Optional<GroupEntity> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isPresent()) {
            return optionalGroup.get();
        } else {
            // Обработка случая, когда пользователь не найден, например, выброс исключения
            throw new GroupNotFoundException("User with this id does not exist!!!");
        }
    }

    public void deleteGroup(Long groupId) throws GroupNotFoundException {
        GroupEntity group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));
        // Удаляем группу из всех пользователей, которые в ней состоят
        for (UserEntity user : group.getUsers()) {
            user.removeGroup(group);  // Убираем эту группу из всех пользователей
        }
        // Удаляем группу из базы данных
        groupRepository.delete(group);
    }
}
