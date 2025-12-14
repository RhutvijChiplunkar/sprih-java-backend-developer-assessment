package com.taskmanagement.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Model Tests")
class TaskTest {

    @Test
    @DisplayName("Should create task with all fields")
    void shouldCreateTaskWithAllFields() {
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        Task task = new Task.Builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(dueDate)
                .priority(Priority.HIGH)
                .status(Status.PENDING)
                .build();

        assertNotNull(task.getId());
        assertEquals("Test Task", task.getTitle());
        assertEquals("Test Description", task.getDescription().orElse(""));
        assertEquals(dueDate, task.getDueDate().orElse(null));
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(Status.PENDING, task.getStatus());
    }

    @Test
    @DisplayName("Should create task with minimal required fields")
    void shouldCreateTaskWithMinimalFields() {
        Task task = new Task.Builder()
                .title("Minimal Task")
                .priority(Priority.MEDIUM)
                .build();

        assertNotNull(task.getId());
        assertEquals("Minimal Task", task.getTitle());
        assertFalse(task.getDescription().isPresent());
        assertFalse(task.getDueDate().isPresent());
        assertEquals(Priority.MEDIUM, task.getPriority());
        assertEquals(Status.PENDING, task.getStatus()); // Default status
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task.Builder()
                    .title(null)
                    .priority(Priority.LOW)
                    .build();
        });
    }

    @Test
    @DisplayName("Should throw exception when title is empty")
    void shouldThrowExceptionWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task.Builder()
                    .title("   ")
                    .priority(Priority.LOW)
                    .build();
        });
    }

    @Test
    @DisplayName("Should throw exception when priority is null")
    void shouldThrowExceptionWhenPriorityIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task.Builder()
                    .title("Test Task")
                    .priority(null)
                    .build();
        });
    }

    @Test
    @DisplayName("Should trim title and description")
    void shouldTrimTitleAndDescription() {
        Task task = new Task.Builder()
                .title("  Trimmed Title  ")
                .description("  Trimmed Description  ")
                .priority(Priority.MEDIUM)
                .build();

        assertEquals("Trimmed Title", task.getTitle());
        assertEquals("Trimmed Description", task.getDescription().orElse(""));
    }

    @Test
    @DisplayName("Should handle empty description as Optional.empty")
    void shouldHandleEmptyDescription() {
        Task task = new Task.Builder()
                .title("Test Task")
                .description("")
                .priority(Priority.MEDIUM)
                .build();

        assertFalse(task.getDescription().isPresent());
    }

    @Test
    @DisplayName("Should update task with new values")
    void shouldUpdateTaskWithNewValues() {
        Task original = new Task.Builder()
                .title("Original Title")
                .description("Original Description")
                .priority(Priority.LOW)
                .status(Status.PENDING)
                .build();

        Task updates = new Task.Builder()
                .title("Updated Title")
                .description("Updated Description")
                .priority(Priority.HIGH)
                .status(Status.IN_PROGRESS)
                .build();

        Task updated = original.updateWith(updates);

        assertEquals(original.getId(), updated.getId());
        assertEquals("Updated Title", updated.getTitle());
        assertEquals("Updated Description", updated.getDescription().orElse(""));
        assertEquals(Priority.HIGH, updated.getPriority());
        assertEquals(Status.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @DisplayName("Should maintain original values when update fields are null")
    void shouldMaintainOriginalValuesWhenUpdateFieldsAreNull() {
        Task original = new Task.Builder()
                .title("Original Title")
                .description("Original Description")
                .priority(Priority.MEDIUM)
                .status(Status.PENDING)
                .build();

        Task updates = new Task.Builder()
                .titleForUpdate("")
                .priorityForUpdate(null)
                .statusForUpdate(null)
                .buildForUpdate();

        Task updated = original.updateWith(updates);

        assertEquals("Original Title", updated.getTitle());
        assertEquals("Original Description", updated.getDescription().orElse(""));
        assertEquals(Priority.MEDIUM, updated.getPriority());
        assertEquals(Status.PENDING, updated.getStatus());
    }

    @Test
    @DisplayName("Should generate unique IDs")
    void shouldGenerateUniqueIds() {
        Task task1 = new Task.Builder()
                .title("Task 1")
                .priority(Priority.LOW)
                .build();

        Task task2 = new Task.Builder()
                .title("Task 2")
                .priority(Priority.LOW)
                .build();

        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    @DisplayName("Should be equal based on ID")
    void shouldBeEqualBasedOnId() {
        String id = "test-id";
        Task task1 = new Task.Builder()
                .id(id)
                .title("Task 1")
                .priority(Priority.LOW)
                .build();

        Task task2 = new Task.Builder()
                .id(id)
                .title("Task 2")
                .priority(Priority.HIGH)
                .build();

        assertEquals(task1, task2);
        assertEquals(task1.hashCode(), task2.hashCode());
    }
}

