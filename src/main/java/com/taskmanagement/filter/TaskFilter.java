package com.taskmanagement.filter;

import com.taskmanagement.model.Priority;
import com.taskmanagement.model.Status;
import com.taskmanagement.model.Task;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class TaskFilter {
    private Status status;
    private Priority priority;
    private LocalDateTime dueDateStart;
    private LocalDateTime dueDateEnd;

    private TaskFilter() {
    }

    public static TaskFilter builder() {
        return new TaskFilter();
    }

    public TaskFilter byStatus(Status status) {
        this.status = status;
        return this;
    }

    public TaskFilter byPriority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public TaskFilter byDueDateRange(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
        this.dueDateStart = start;
        this.dueDateEnd = end;
        return this;
    }

    public Predicate<Task> build() {
        Predicate<Task> predicate = task -> true;

        if (status != null) {
            predicate = predicate.and(task -> task.getStatus() == status);
        }

        if (priority != null) {
            predicate = predicate.and(task -> task.getPriority() == priority);
        }

        if (dueDateStart != null || dueDateEnd != null) {
            predicate = predicate.and(task -> {
                if (!task.getDueDate().isPresent()) {
                    return false;
                }
                LocalDateTime taskDueDate = task.getDueDate().get();
                boolean afterStart = dueDateStart == null || !taskDueDate.isBefore(dueDateStart);
                boolean beforeEnd = dueDateEnd == null || !taskDueDate.isAfter(dueDateEnd);
                return afterStart && beforeEnd;
            });
        }

        return predicate;
    }

    public static Predicate<Task> all() {
        return task -> true;
    }
}