package com.taskmanagement.repository;

import com.taskmanagement.model.Task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.function.Predicate;


public class InMemoryTaskRepository implements TaskRepository {
    private final Map<String, Task> tasks;

    public InMemoryTaskRepository() {
        this.tasks = new ConcurrentHashMap<>();
    }

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task findById(String id) {
        if (id == null) {
            return null;
        }
        return tasks.get(id);
    }

    @Override
    public List<Task> findAll() {
        return tasks.values().stream()
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> findAll(Predicate<Task> filter) {
        if (filter == null) {
            return findAll();
        }
        return tasks.values().stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        return tasks.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        if (id == null) {
            return false;
        }
        return tasks.containsKey(id);
    }
}

