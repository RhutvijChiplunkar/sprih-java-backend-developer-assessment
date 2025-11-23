package com.taskmanagement.service;

import com.taskmanagement.exception.InvalidTaskException;
import com.taskmanagement.exception.TaskNotFoundException;
import com.taskmanagement.filter.TaskFilter;
import com.taskmanagement.model.Priority;
import com.taskmanagement.model.Status;
import com.taskmanagement.model.Task;
import com.taskmanagement.repository.TaskRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }

    public Task createTask(String title, Optional<String> description, 
                          Optional<LocalDateTime> dueDate, Priority priority) {
        validateTitle(title);
        validatePriority(priority);

        Task task = new Task.Builder()
                .title(title)
                .description(description.orElse(null))
                .dueDate(dueDate.orElse(null))
                .priority(priority)
                .status(Status.PENDING)
                .build();

        return repository.save(task);
    }

    public Task updateTask(String id, String title, Optional<String> description,
                          Optional<LocalDateTime> dueDate, Priority priority, Status status) {
        Task existingTask = getTask(id);

        Task.Builder builder = new Task.Builder(existingTask);
        
        if (title != null && !title.trim().isEmpty()) {
            builder.title(title);
        }
        if (description != null) {
            builder.description(description.orElse(null));
        }
        if (dueDate != null) {
            builder.dueDate(dueDate.orElse(null));
        }
        if (priority != null) {
            builder.priority(priority);
        }
        if (status != null) {
            builder.status(status);
        }

        Task updatedTask = builder.build();
        return repository.save(updatedTask);
    }

    public void deleteTask(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTaskException("Task ID cannot be null or empty");
        }
        
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new TaskNotFoundException("Task with ID '" + id + "' not found");
        }
    }

    public Task getTask(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidTaskException("Task ID cannot be null or empty");
        }

        Task task = repository.findById(id);
        if (task == null) {
            throw new TaskNotFoundException("Task with ID '" + id + "' not found");
        }
        return task;
    }

    public List<Task> listTasks(TaskFilter filter) {
        if (filter == null) {
            return repository.findAll();
        }
        return repository.findAll(filter.build());
    }

    public List<Task> listTasks(TaskFilter filter, SortOption sortOption) {
        List<Task> tasks = listTasks(filter);
        
        if (sortOption == null) {
            return tasks;
        }

        Comparator<Task> comparator = getComparator(sortOption);
        return tasks.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Task> listAllTasks() {
        return repository.findAll();
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidTaskException("Task title cannot be null or empty");
        }
    }

    private void validatePriority(Priority priority) {
        if (priority == null) {
            throw new InvalidTaskException("Task priority cannot be null");
        }
    }

    private Comparator<Task> getComparator(SortOption sortOption) {
        switch (sortOption) {
            case DUE_DATE_ASC:
                return Comparator.comparing(
                    task -> task.getDueDate().orElse(LocalDateTime.MAX),
                    Comparator.nullsLast(Comparator.naturalOrder())
                );
            case DUE_DATE_DESC:
                return Comparator.comparing(
                    task -> task.getDueDate().orElse(LocalDateTime.MIN),
                    Comparator.nullsFirst(Comparator.reverseOrder())
                );
            case PRIORITY_ASC:
                return Comparator.comparing(Task::getPriority);
            case PRIORITY_DESC:
                return Comparator.comparing(Task::getPriority).reversed();
            case TITLE_ASC:
                return Comparator.comparing(Task::getTitle);
            case TITLE_DESC:
                return Comparator.comparing(Task::getTitle).reversed();
            default:
                return Comparator.comparing(Task::getId);
        }
    }
}