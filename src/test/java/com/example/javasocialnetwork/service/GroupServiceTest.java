package com.example.javasocialnetwork.service;

import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.dto.GroupWithUsersDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupAlreadyExistException;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private GroupService groupService;

    private Group group;
    private User user;
    private GroupWithUsersDto groupDto;

    @BeforeEach
    void setUp() {
        group = new Group();
        group.setId(1L);
        group.setName("TestGroup");
        group.setUsers(new HashSet<>());

        user = new User();
        user.setId(1L);
        user.setUserName("user1");
        group.getUsers().add(user);

        groupDto = GroupWithUsersDto.toModel(group);
    }

    @Test
    void getOne_GroupInCache_ReturnsCachedDto() {
        when(cacheService.get(anyString())).thenReturn(Optional.of(groupDto));

        GroupWithUsersDto result = groupService.getOne(1L);

        assertEquals(groupDto, result);
        verify(cacheService).get("group_1");
        verify(groupRepository, never()).findWithUsersById(anyLong());
    }


    @Test
    void getOne_GroupNotFound_ThrowsException() {
        when(cacheService.get(anyString())).thenReturn(Optional.empty());
        when(groupRepository.findWithUsersById(1L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> groupService.getOne(1L));
        verify(cacheService).get("group_1");
        verify(groupRepository).findWithUsersById(1L);
    }

    @Test
    void registration_NewGroup_Success() {
        when(groupRepository.findByName("NewGroup")).thenReturn(null);
        when(groupRepository.save(any())).thenReturn(group);

        Group newGroup = new Group();
        newGroup.setName("NewGroup");

        Group result = groupService.registration(newGroup);

        assertNotNull(result);
        verify(groupRepository).findByName("NewGroup");
        verify(groupRepository).save(newGroup);
        verify(cacheService).evictByPrefix("groups_");
    }

    @Test
    void registration_ExistingGroup_ThrowsException() {
        Group existingGroup = new Group();
        existingGroup.setName("ExistingGroup");
        when(groupRepository.findByName("ExistingGroup")).thenReturn(existingGroup);

        assertThrows(GroupAlreadyExistException.class, () -> {
            groupService.registration(existingGroup);
        });
        verify(groupRepository).findByName("ExistingGroup");
        verify(groupRepository, never()).save(any());
    }

    @Test
    void updateGroup_ExistingGroup_UpdatesNameAndInvalidatesCache() {
        // Arrange
        Group updatedGroup = new Group();
        updatedGroup.setName("NewName");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(groupRepository.save(any())).thenReturn(group);

        // Act
        groupService.updateGroup(1L, updatedGroup);

        // Assert
        assertEquals("NewName", group.getName());
        verify(groupRepository).save(group);

        // Проверка инвалидации кеша
        verify(cacheService).evict("group_1");
        verify(cacheService).evict("group_by_name_NewName");
        verify(cacheService).evictByPrefix("groups_");
    }

    @Test
    void deleteGroup_SuccessfulDeletion() {
        // Arrange
        Long groupId = 1L;
        Group testGroup = new Group();
        testGroup.setId(groupId);

        User user1 = new User();
        user1.setId(101L);
        User user2 = new User();
        user2.setId(102L);

        // Настраиваем связи
        testGroup.getUsers().add(user1);
        testGroup.getUsers().add(user2);
        user1.getGroups().add(testGroup);
        user2.getGroups().add(testGroup);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        // Act
        groupService.deleteGroup(groupId);

        // Assert
        verify(groupRepository).delete(testGroup);
        // ... остальные проверки
    }

    @Test
    void deleteGroup_NonExistingGroup_ThrowsException() {
        // Arrange
        Long groupId = 2L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act & Assert
        GroupNotFoundException exception = assertThrows(GroupNotFoundException.class,
                () -> groupService.deleteGroup(groupId));

        assertEquals("Group not found", exception.getMessage());
        assertEquals(groupId, exception.getDetails().get("groupId"));

        verify(groupRepository, never()).delete(any());
        verify(userRepository, never()).save(any());
        verify(cacheService, never()).evict(anyString());
    }

}