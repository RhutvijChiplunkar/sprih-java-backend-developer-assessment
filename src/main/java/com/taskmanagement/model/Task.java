package com.taskmanagement.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class Task {
    private final String id;
    private final String title;
    private final Optional<String> description;
    private final Optional<LocalDateTime> dueDate;
    private final Priority priority;
    private final Status status;

    private Task(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.status = builder.status;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Optional<String> getDescription() {
        return description;
    }

    public Optional<LocalDateTime> getDueDate() {
        return dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }


    public Task updateWith(Task updates) {
        Builder builder = new Builder(this);
        if (updates.title != null && !updates.title.isEmpty()) {
            builder.title(updates.title);
        }
        if (updates.description != null) {
            builder.description(updates.description.orElse(null));
        }
        if (updates.dueDate != null) {
            builder.dueDate(updates.dueDate.orElse(null));
        }
        if (updates.priority != null) {
            builder.priority(updates.priority);
        }
        if (updates.status != null) {
            builder.status(updates.status);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description=" + description.orElse("") +
                ", dueDate=" + dueDate.map(LocalDateTime::toString).orElse("") +
                ", priority=" + priority +
                ", status=" + status +
                '}';
    }


    public static class Builder {
        private String id;
        private String title;
        private Optional<String> description = Optional.empty();
        private Optional<LocalDateTime> dueDate = Optional.empty();
        private Priority priority;
        private Status status = Status.PENDING;

        public Builder() {
            this.id = UUID.randomUUID().toString();
        }

        public Builder(Task task) {
            this.id = task.id;
            this.title = task.title;
            this.description = task.description;
            this.dueDate = task.dueDate;
            this.priority = task.priority;
            this.status = task.status;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be null or empty for the task");
            }
            this.title = title.trim();
            return this;
        }

        public Builder description(String description) {
            this.description = description == null || description.trim().isEmpty() 
                ? Optional.empty() 
                : Optional.of(description.trim());
            return this;
        }

        public Builder dueDate(LocalDateTime dueDate) {
            this.dueDate = Optional.ofNullable(dueDate);
            return this;
        }

        public Builder priority(Priority priority) {
            if (priority == null) {
                throw new IllegalArgumentException("Priority cannot be null for the task");
            }
            this.priority = priority;
            return this;
        }

        public Builder status(Status status) {
            if (status == null) {
                throw new IllegalArgumentException("Status cannot be null for the task");
            }
            this.status = status;
            return this;
        }

        public Task build() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title is required for the task");
            }
            if (priority == null) {
                throw new IllegalArgumentException("Priority is required for the task");
            }
            return new Task(this);
        }
    }
}

