package com.example.javasocialnetwork.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.javasocialnetwork.cache.CacheService;
import com.example.javasocialnetwork.dto.UserWithPostsAndGroupsDto;
import com.example.javasocialnetwork.entity.Group;
import com.example.javasocialnetwork.entity.User;
import com.example.javasocialnetwork.exception.GroupNotFoundException;
import com.example.javasocialnetwork.exception.NotFoundException;
import com.example.javasocialnetwork.exception.UserAlreadyExistException;
import com.example.javasocialnetwork.repository.GroupRepository;
import com.example.javasocialnetwork.repository.UserRepository;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER = "user_";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        LOGGER.info("Setting up test data...");
        testUser = new User();
        testUser.setId(1L);
        testUser.setUserName("testuser");
        testUser.setPassword("password123");
    }

    @Test
    void removeUserFromGroup_ShouldRemoveGroupAndEvictCache() throws Exception {
        // Arrange
        Long userId = 1L;
        Long groupId = 2L;
        User user = new User();
        user.setId(userId);
        Group group = new Group();
        group.setId(groupId);
        user.addGroup(group); // Добавляем группу для удаления

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Act
        userService.removeUserFromGroup(userId, groupId);

        // Assert
        // Проверяем, что группа удалена
        assertThat(user.getGroups()).doesNotContain(group);
        // Проверяем сохранение пользователя
        verify(userRepository).save(user);
        // Проверяем очистку кэша
        verify(cacheService).evict(USER + userId);
    }

    @Test
    void removeUserFromGroup_WhenGroupNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long groupId = 2L;
        User user = new User();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.removeUserFromGroup(userId, groupId))
                .isInstanceOf(GroupNotFoundException.class)
                .hasMessageContaining("Group with this id not exist!!!");
    }

    @Test
    void removeUserFromGroup_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        Long userId = 1L;
        Long groupId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.removeUserFromGroup(userId, groupId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User with this id not exist!!!");
    }

    @Test
    void findByPostContent_ShouldReturnCachedData() {
        String content = "test";
        List<UserWithPostsAndGroupsDto> expected = List.of(/*...*/);
        when(cacheService.get("users_by_post_content_" + content)).thenReturn(Optional.of(expected));

        List<UserWithPostsAndGroupsDto> result = userService.findByPostContent(content);

        assertThat(result).isEqualTo(expected);
        verify(userRepository, never()).findAllByPostContent(any());
    }

    @Test
    void removeUserFromGroup_ShouldCallRepositoryAndCache() throws Exception {
        // Arrange
        Long userId = 1L;
        Long groupId = 2L;
        User user = new User();
        Group group = new Group();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Act
        userService.removeUserFromGroup(userId, groupId);

        // Assert
        verify(userRepository).save(user);
        verify(cacheService).evict(USER + userId);
    }

    @Test
    void removeUserFromGroup_WhenGroupNotAssigned_ShouldDoNothing() throws Exception {
        // Arrange
        Long userId = 1L;
        Long groupId = 2L;
        User user = new User();
        Group group = new Group();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Act
        userService.removeUserFromGroup(userId, groupId);

        // Assert
        // Проверяем, что метод save всё равно вызван (даже если группа не была удалена)
        verify(userRepository).save(user);
    }

    @Test
    void registration_WithNewUser_ShouldSaveAndInvalidateCache() throws UserAlreadyExistException {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User result = userService.registration(testUser);

        assertThat(result).isEqualTo(testUser);
        verify(cacheService).invalidateUserCache();
    }

    @Test
    void registration_WithExistingUser_ShouldThrowException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registration(testUser))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("User already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void bulkCreateUsers_ShouldSaveOnlyNewUsers() {
        User existingUser = new User();
        existingUser.setUserName("existing");
        User newUser = new User();
        newUser.setUserName("newuser");

        when(userRepository.existsByUsername("existing")).thenReturn(true);
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.saveAll(List.of(newUser))).thenReturn(List.of(newUser));

        List<User> result = userService.bulkCreateUsers(List.of(existingUser, newUser));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserName()).isEqualTo("newuser");
        verify(cacheService).invalidateUserCache();
    }

    @Test
    void getOne_ShouldReturnUserFromCache() {
        UserWithPostsAndGroupsDto cachedUser = new UserWithPostsAndGroupsDto();
        cachedUser.setUsername("cachedUser");
        when(cacheService.get("user_1")).thenReturn(Optional.of(cachedUser));

        UserWithPostsAndGroupsDto result = userService.getOne(1L);

        assertThat(result.getUsername()).isEqualTo("cachedUser");
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getOne_ShouldFetchFromDbWhenCacheEmpty() {
        when(cacheService.get("user_1")).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserWithPostsAndGroupsDto result = userService.getOne(1L);

        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(cacheService).put(eq("user_1"), any(UserWithPostsAndGroupsDto.class));
    }

    @Test
    void delete_ExistingUser_ShouldEvictCache() throws NotFoundException {
        when(userRepository.existsById(1L)).thenReturn(true);

        Long deletedId = userService.delete(1L);

        assertThat(deletedId).isEqualTo(1L);
        verify(userRepository).deleteById(1L);
        verify(cacheService).evict("user_1");
    }

    @Test
    void delete_NonExistingUser_ShouldThrowException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.delete(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void addUserToGroup_ShouldUpdateCache() throws NotFoundException, GroupNotFoundException {
        Group testGroup = new Group();
        testGroup.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(groupRepository.findById(2L)).thenReturn(Optional.of(testGroup));

        userService.addUserToGroup(1L, 2L);

        verify(cacheService).evict("user_1");
        verify(cacheService).evict("group_2");
        verify(cacheService).evictByPrefix("groups_");
    }

    @Test
    void updateUser_ShouldUpdateFieldsAndEvictCache() throws NotFoundException {
        User updatedUser = new User();
        updatedUser.setUserName("newuser");
        updatedUser.setPassword("newpassword");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.updateUser(1L, updatedUser);

        assertThat(testUser.getUserName()).isEqualTo("newuser");
        assertThat(testUser.getPassword()).isEqualTo("newpassword");
        verify(cacheService).evict("user_1");
    }

    @Test
    void searchUsersByUsername_ShouldReturnFilteredResults() {
        User user1 = new User();
        user1.setUserName("testuser1");
        User user2 = new User();
        user2.setUserName("testuser2");

        when(userRepository.findByUsernameContainingIgnoreCase("test"))
                .thenReturn(List.of(user1, user2));

        List<UserWithPostsAndGroupsDto> result = userService.searchUsersByUsername("test");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser1");
        assertThat(result.get(1).getUsername()).isEqualTo("testuser2");
    }

    @Test
    void getUserGroups_ShouldReturnUserGroups() throws NotFoundException {
        Group group = new Group();
        group.setId(1L);
        testUser.setGroups(Set.of(group));

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Set<Group> result = userService.getUserGroups(1L);

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getId()).isEqualTo(1L);
    }

    @Test
    void bulkCreateUsers_WithEmptyList_ShouldReturnEmptyList() {
        List<User> result = userService.bulkCreateUsers(Collections.emptyList());
        assertThat(result).isEmpty();
    }

    @Test
    void searchUsersByUsername_WithNonExistingName_ShouldReturnEmpty() {
        when(userRepository.findByUsernameContainingIgnoreCase("unknown")).thenReturn(Collections.emptyList());

        List<UserWithPostsAndGroupsDto> result = userService.searchUsersByUsername("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void registration_WithExistingUser_ShouldIncludeDetailsInException() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.registration(testUser))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessageContaining("User already exists")
                .satisfies(ex -> {
                    Map<String, Object> details = ((UserAlreadyExistException) ex).getDetails();
                    assertThat(details).containsEntry("username", "testuser");
                });
    }

    @Test
    void getOne_ShouldMapUserToDtoCorrectly() {
        User user = new User();
        user.setUserName("test");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserWithPostsAndGroupsDto dto = userService.getOne(1L);

        assertThat(dto.getUsername()).isEqualTo("test");
    }
}