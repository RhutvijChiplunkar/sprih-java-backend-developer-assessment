package com.taskmanagement.service;

import com.taskmanagement.exception.InvalidTaskException;
import com.taskmanagement.exception.TaskNotFoundException;
import com.taskmanagement.filter.TaskFilter;
import com.taskmanagement.model.Priority;
import com.taskmanagement.model.Status;
import com.taskmanagement.model.Task;
import com.taskmanagement.repository.InMemoryTaskRepository;
import com.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TaskService Tests")
class TaskServiceTest {

    private TaskService taskService;
    private TaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
        taskService = new TaskService(repository);
    }

    @Test
    @DisplayName("Should create task successfully")
    void shouldCreateTaskSuccessfully() {
        Task task = taskService.createTask(
                "Test Task",
                Optional.of("Test Description"),
                Optional.of(LocalDateTime.now().plusDays(7)),
                Priority.HIGH
        );

        assertNotNull(task);
        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription().orElse(""));
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Should create task with minimal fields")
    void shouldCreateTaskWithMinimalFields() {
        Task task = taskService.createTask(
                "Minimal Task",
                Optional.empty(),
                Optional.empty(),
                Priority.MEDIUM
        );

        assertNotNull(task);
        assertEquals("Minimal Task", task.getTitle());
        assertFalse(task.getDescription().isPresent());
        assertFalse(task.getDueDate().isPresent());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertEquals(Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when creating task with null title")
    void shouldThrowExceptionWhenCreatingTaskWithNullTitle() {
        assertThrows(InvalidTaskException.class, () -> {
            taskService.createTask(null, Optional.empty(), Optional.empty(), Priority.LOW);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating task with empty title")
    void shouldThrowExceptionWhenCreatingTaskWithEmptyTitle() {
        assertThrows(InvalidTaskException.class, () -> {
            taskService.createTask("   ", Optional.empty(), Optional.empty(), Priority.LOW);
        });
    }

    @Test
    @DisplayName("Should throw exception when creating task with null priority")
    void shouldThrowExceptionWhenCreatingTaskWithNullPriority() {
        assertThrows(InvalidTaskException.class, () -> {
            taskService.createTask("Test Task", Optional.empty(), Optional.empty(), null);
        });
    }

    @Test
    @DisplayName("Should update task successfully")
    void shouldUpdateTaskSuccessfully() {
        Task task = taskService.createTask(
                "Original Title",
                Optional.of("Original Description"),
                Optional.empty(),
                Priority.LOW
        );

        Task updated = taskService.updateTask(
                task.getId(),
                "Updated Title",
                Optional.of("Updated Description"),
                Optional.of(LocalDateTime.now().plusDays(5)),
                Priority.HIGH,
                Status.IN_PROGRESS
        );

        assertEquals(task.getId(), updated.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription().orElse(""));
        assertEquals(Priority.HIGH, updated.getPriority());
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
        assertTrue(updated.getDueDate().isPresent());
    }

    @Test
    @DisplayName("Should update task partially")
    void shouldUpdateTaskPartially() {
        Task task = taskService.createTask(
                "Original Title",
                Optional.of("Original Description"),
                Optional.empty(),
                Priority.MEDIUM
        );

        Task updated = taskService.updateTask(
                task.getId(),
                null,
                null,
                null,
                Priority.HIGH,
                null
        );

        assertEquals("Original Title", updated.getTitle());
        assertEquals("Original Description", updated.getDescription().orElse(""));
        assertEquals(Priority.HIGH, updated.getPriority());
        assertEquals(Status.PENDING, updated.getStatus());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(
                    "non-existent-id",
                    "New Title",
                    Optional.empty(),
                    Optional.empty(),
                    Priority.HIGH,
                    null
            );
        });
    }

    @Test
    @DisplayName("Should delete task successfully")
    void shouldDeleteTaskSuccessfully() {
        Task task = taskService.createTask(
                "Task to Delete",
                Optional.empty(),
                Optional.empty(),
                Priority.MEDIUM
        );

        taskService.deleteTask(task.getId());

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTask(task.getId());
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask("non-existent-id");
        });
    }

    @Test
    @DisplayName("Should throw exception when deleting with null ID")
    void shouldThrowExceptionWhenDeletingWithNullId() {
        assertThrows(InvalidTaskException.class, () -> {
            taskService.deleteTask(null);
        });
    }

    @Test
    @DisplayName("Should get task by ID")
    void shouldGetTaskById() {
        Task created = taskService.createTask(
                "Test Task",
                Optional.empty(),
                Optional.empty(),
                Priority.MEDIUM
        );

        Task retrieved = taskService.getTask(created.getId());

        assertNotNull(retrieved);
        assertEquals(created.getId(), retrieved.getId());
        assertEquals(created.getTitle(), retrieved.getTitle());
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent task")
    void shouldThrowExceptionWhenGettingNonExistentTask() {
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.getTask("non-existent-id");
        });
    }

    @Test
    @DisplayName("Should throw exception when getting task with null ID")
    void shouldThrowExceptionWhenGettingTaskWithNullId() {
        assertThrows(InvalidTaskException.class, () -> {
            taskService.getTask(null);
        });
    }

    @Test
    @DisplayName("Should list all tasks")
    void shouldListAllTasks() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.MEDIUM);
        taskService.createTask("Task 3", Optional.empty(), Optional.empty(), Priority.HIGH);

        List<Task> tasks = taskService.listAllTasks();

        assertEquals(3, tasks.size());
    }

    @Test
    @DisplayName("Should list tasks with status filter")
    void shouldListTasksWithStatusFilter() {
        Task task1 = taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.LOW);
        Task task2 = taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.MEDIUM);
        taskService.updateTask(task2.getId(), null, null, null, null, Status.COMPLETED);

        TaskFilter filter = TaskFilter.builder().byStatus(Status.PENDING);
        List<Task> tasks = taskService.listTasks(filter);

        assertEquals(1, tasks.size());
        assertEquals(task1.getId(), tasks.get(0).getId());
    }

    @Test
    @DisplayName("Should list tasks with priority filter")
    void shouldListTasksWithPriorityFilter() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.HIGH);
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.MEDIUM);
        taskService.createTask("Task 3", Optional.empty(), Optional.empty(), Priority.HIGH);

        TaskFilter filter = TaskFilter.builder().byPriority(Priority.HIGH);
        List<Task> tasks = taskService.listTasks(filter);

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getPriority() == Priority.HIGH));
    }

    @Test
    @DisplayName("Should list tasks with due date range filter")
    void shouldListTasksWithDueDateRangeFilter() {
        LocalDateTime now = LocalDateTime.now();
        taskService.createTask("Task 1", Optional.empty(), Optional.of(now.plusDays(1)), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.of(now.plusDays(5)), Priority.MEDIUM);
        taskService.createTask("Task 3", Optional.empty(), Optional.of(now.plusDays(10)), Priority.HIGH);

        TaskFilter filter = TaskFilter.builder()
                .byDueDateRange(now, now.plusDays(7));
        List<Task> tasks = taskService.listTasks(filter);

        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Should list tasks with multiple filters")
    void shouldListTasksWithMultipleFilters() {
        Task task1 = taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.HIGH);
        taskService.updateTask(task1.getId(), null, null, null, null, Status.IN_PROGRESS);
        
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.HIGH);
        taskService.createTask("Task 3", Optional.empty(), Optional.empty(), Priority.MEDIUM);

        TaskFilter filter = TaskFilter.builder()
                .byPriority(Priority.HIGH)
                .byStatus(Status.IN_PROGRESS);
        List<Task> tasks = taskService.listTasks(filter);

        assertEquals(1, tasks.size());
        assertEquals(task1.getId(), tasks.get(0).getId());
    }

    @Test
    @DisplayName("Should list tasks with sorting by priority ascending")
    void shouldListTasksWithSortingByPriorityAscending() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.HIGH);
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.LOW);
        taskService.createTask("Task 3", Optional.empty(), Optional.empty(), Priority.MEDIUM);

        List<Task> tasks = taskService.listTasks(null, SortOption.PRIORITY_ASC);

        assertEquals(3, tasks.size());
        assertEquals(Priority.LOW, tasks.get(0).getPriority());
        assertEquals(Priority.MEDIUM, tasks.get(1).getPriority());
        assertEquals(Priority.HIGH, tasks.get(2).getPriority());
    }

    @Test
    @DisplayName("Should list tasks with sorting by priority descending")
    void shouldListTasksWithSortingByPriorityDescending() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.HIGH);
        taskService.createTask("Task 3", Optional.empty(), Optional.empty(), Priority.MEDIUM);

        List<Task> tasks = taskService.listTasks(null, SortOption.PRIORITY_DESC);

        assertEquals(3, tasks.size());
        assertEquals(Priority.HIGH, tasks.get(0).getPriority());
        assertEquals(Priority.MEDIUM, tasks.get(1).getPriority());
        assertEquals(Priority.LOW, tasks.get(2).getPriority());
    }

    @Test
    @DisplayName("Should list tasks with sorting by due date ascending")
    void shouldListTasksWithSortingByDueDateAscending() {
        LocalDateTime now = LocalDateTime.now();
        taskService.createTask("Task 1", Optional.empty(), Optional.of(now.plusDays(10)), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.of(now.plusDays(1)), Priority.MEDIUM);
        taskService.createTask("Task 3", Optional.empty(), Optional.of(now.plusDays(5)), Priority.HIGH);

        List<Task> tasks = taskService.listTasks(null, SortOption.DUE_DATE_ASC);

        assertEquals(3, tasks.size());
        assertEquals(now.plusDays(1), tasks.get(0).getDueDate().orElse(null));
        assertEquals(now.plusDays(5), tasks.get(1).getDueDate().orElse(null));
        assertEquals(now.plusDays(10), tasks.get(2).getDueDate().orElse(null));
    }

    @Test
    @DisplayName("Should list tasks with sorting by due date descending")
    void shouldListTasksWithSortingByDueDateDescending() {
        LocalDateTime now = LocalDateTime.now();
        taskService.createTask("Task 1", Optional.empty(), Optional.of(now.plusDays(1)), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.of(now.plusDays(10)), Priority.MEDIUM);
        taskService.createTask("Task 3", Optional.empty(), Optional.of(now.plusDays(5)), Priority.HIGH);

        List<Task> tasks = taskService.listTasks(null, SortOption.DUE_DATE_DESC);

        assertEquals(3, tasks.size());
        assertEquals(now.plusDays(10), tasks.get(0).getDueDate().orElse(null));
        assertEquals(now.plusDays(5), tasks.get(1).getDueDate().orElse(null));
        assertEquals(now.plusDays(1), tasks.get(2).getDueDate().orElse(null));
    }

    @Test
    @DisplayName("Should list tasks with sorting by title ascending")
    void shouldListTasksWithSortingByTitleAscending() {
        taskService.createTask("Zebra Task", Optional.empty(), Optional.empty(), Priority.LOW);
        taskService.createTask("Apple Task", Optional.empty(), Optional.empty(), Priority.MEDIUM);
        taskService.createTask("Banana Task", Optional.empty(), Optional.empty(), Priority.HIGH);

        List<Task> tasks = taskService.listTasks(null, SortOption.TITLE_ASC);

        assertEquals(3, tasks.size());
        assertEquals("Apple Task", tasks.get(0).getTitle());
        assertEquals("Banana Task", tasks.get(1).getTitle());
        assertEquals("Zebra Task", tasks.get(2).getTitle());
    }

    @Test
    @DisplayName("Should list tasks with filter and sorting combined")
    void shouldListTasksWithFilterAndSortingCombined() {
        LocalDateTime now = LocalDateTime.now();
        taskService.createTask("Task 1", Optional.empty(), Optional.of(now.plusDays(10)), Priority.HIGH);
        taskService.createTask("Task 2", Optional.empty(), Optional.of(now.plusDays(1)), Priority.HIGH);
        taskService.createTask("Task 3", Optional.empty(), Optional.of(now.plusDays(5)), Priority.MEDIUM);

        TaskFilter filter = TaskFilter.builder().byPriority(Priority.HIGH);
        List<Task> tasks = taskService.listTasks(filter, SortOption.DUE_DATE_ASC);

        assertEquals(2, tasks.size());
        assertEquals(now.plusDays(1), tasks.get(0).getDueDate().orElse(null));
        assertEquals(now.plusDays(10), tasks.get(1).getDueDate().orElse(null));
    }

    @Test
    @DisplayName("Should return empty list when no tasks match filter")
    void shouldReturnEmptyListWhenNoTasksMatchFilter() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.LOW);

        TaskFilter filter = TaskFilter.builder().byStatus(Status.COMPLETED);
        List<Task> tasks = taskService.listTasks(filter);

        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("Should handle null filter gracefully")
    void shouldHandleNullFilterGracefully() {
        taskService.createTask("Task 1", Optional.empty(), Optional.empty(), Priority.LOW);
        taskService.createTask("Task 2", Optional.empty(), Optional.empty(), Priority.MEDIUM);

        List<Task> tasks = taskService.listTasks(null);

        assertEquals(2, tasks.size());
    }

    @Test
    @DisplayName("Should throw exception when repository is null")
    void shouldThrowExceptionWhenRepositoryIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TaskService(null);
        });
    }
}

