package com.taskmanagement.repository;

import com.taskmanagement.model.Priority;
import com.taskmanagement.model.Status;
import com.taskmanagement.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InMemoryTaskRepository Tests")
class InMemoryTaskRepositoryTest {

    private InMemoryTaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTaskRepository();
    }

    @Test
    @DisplayName("Should save and retrieve task")
    void shouldSaveAndRetrieveTask() {
        Task task = new Task.Builder()
                .title("Test Task")
                .priority(Priority.MEDIUM)
                .build();

        Task saved = repository.save(task);
        Task retrieved = repository.findById(task.getId());

        assertNotNull(retrieved);
        assertEquals(task.getId(), retrieved.getId());
        assertEquals(task.getTitle(), retrieved.getTitle());
    }

    @Test
    @DisplayName("Should update existing task")
    void shouldUpdateExistingTask() {
        Task task = new Task.Builder()
                .title("Original Title")
                .priority(Priority.LOW)
                .build();

        repository.save(task);

        Task updated = new Task.Builder()
                .id(task.getId())
                .title("Updated Title")
                .priority(Priority.HIGH)
                .build();

        Task saved = repository.save(updated);
        Task retrieved = repository.findById(task.getId());

        assertEquals("Updated Title", retrieved.getTitle());
        assertEquals(Priority.HIGH, retrieved.getPriority());
    }

    @Test
    @DisplayName("Should return null for non-existent task")
    void shouldReturnNullForNonExistentTask() {
        Task task = repository.findById("non-existent-id");
        assertNull(task);
    }

    @Test
    @DisplayName("Should return all tasks")
    void shouldReturnAllTasks() {
        Task task1 = new Task.Builder()
                .title("Task 1")
                .priority(Priority.LOW)
                .build();

        Task task2 = new Task.Builder()
                .title("Task 2")
                .priority(Priority.MEDIUM)
                .build();

        repository.save(task1);
        repository.save(task2);

        List<Task> allTasks = repository.findAll();

        assertEquals(2, allTasks.size());
        assertTrue(allTasks.contains(task1));
        assertTrue(allTasks.contains(task2));
    }

    @Test
    @DisplayName("Should filter tasks by status")
    void shouldFilterTasksByStatus() {
        Task task1 = new Task.Builder()
                .title("Task 1")
                .priority(Priority.LOW)
                .status(Status.PENDING)
                .build();

        Task task2 = new Task.Builder()
                .title("Task 2")
                .priority(Priority.MEDIUM)
                .status(Status.COMPLETED)
                .build();

        Task task3 = new Task.Builder()
                .title("Task 3")
                .priority(Priority.HIGH)
                .status(Status.PENDING)
                .build();

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        List<Task> pendingTasks = repository.findAll(task -> task.getStatus() == Status.PENDING);

        assertEquals(2, pendingTasks.size());
        assertTrue(pendingTasks.stream().allMatch(t -> t.getStatus() == Status.PENDING));
    }

    @Test
    @DisplayName("Should filter tasks by priority")
    void shouldFilterTasksByPriority() {
        Task task1 = new Task.Builder()
                .title("Task 1")
                .priority(Priority.HIGH)
                .build();

        Task task2 = new Task.Builder()
                .title("Task 2")
                .priority(Priority.MEDIUM)
                .build();

        Task task3 = new Task.Builder()
                .title("Task 3")
                .priority(Priority.HIGH)
                .build();

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        List<Task> highPriorityTasks = repository.findAll(task -> task.getPriority() == Priority.HIGH);

        assertEquals(2, highPriorityTasks.size());
        assertTrue(highPriorityTasks.stream().allMatch(t -> t.getPriority() == Priority.HIGH));
    }

    @Test
    @DisplayName("Should delete task by ID")
    void shouldDeleteTaskById() {
        Task task = new Task.Builder()
                .title("Task to Delete")
                .priority(Priority.MEDIUM)
                .build();

        repository.save(task);
        assertTrue(repository.existsById(task.getId()));

        boolean deleted = repository.deleteById(task.getId());

        assertTrue(deleted);
        assertFalse(repository.existsById(task.getId()));
        assertNull(repository.findById(task.getId()));
    }

    @Test
    @DisplayName("Should return false when deleting non-existent task")
    void shouldReturnFalseWhenDeletingNonExistentTask() {
        boolean deleted = repository.deleteById("non-existent-id");
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should check if task exists")
    void shouldCheckIfTaskExists() {
        Task task = new Task.Builder()
                .title("Test Task")
                .priority(Priority.MEDIUM)
                .build();

        assertFalse(repository.existsById(task.getId()));

        repository.save(task);

        assertTrue(repository.existsById(task.getId()));
    }

    @Test
    @DisplayName("Should handle null ID gracefully")
    void shouldHandleNullIdGracefully() {
        assertNull(repository.findById(null));
        assertFalse(repository.existsById(null));
        assertFalse(repository.deleteById(null));
    }

    @Test
    @DisplayName("Should throw exception when saving null task")
    void shouldThrowExceptionWhenSavingNullTask() {
        assertThrows(IllegalArgumentException.class, () -> {
            repository.save(null);
        });
    }

    @Test
    @DisplayName("Should return empty list when no tasks exist")
    void shouldReturnEmptyListWhenNoTasksExist() {
        List<Task> tasks = repository.findAll();
        assertTrue(tasks.isEmpty());
    }

    @Test
    @DisplayName("Should filter tasks by due date range")
    void shouldFilterTasksByDueDateRange() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task.Builder()
                .title("Task 1")
                .priority(Priority.LOW)
                .dueDate(now.plusDays(1))
                .build();

        Task task2 = new Task.Builder()
                .title("Task 2")
                .priority(Priority.MEDIUM)
                .dueDate(now.plusDays(5))
                .build();

        Task task3 = new Task.Builder()
                .title("Task 3")
                .priority(Priority.HIGH)
                .dueDate(now.plusDays(10))
                .build();

        repository.save(task1);
        repository.save(task2);
        repository.save(task3);

        LocalDateTime start = now;
        LocalDateTime end = now.plusDays(7);

        List<Task> filtered = repository.findAll(task -> {
            if (!task.getDueDate().isPresent()) {
                return false;
            }
            LocalDateTime dueDate = task.getDueDate().get();
            return !dueDate.isBefore(start) && !dueDate.isAfter(end);
        });

        assertEquals(2, filtered.size());
    }
}

