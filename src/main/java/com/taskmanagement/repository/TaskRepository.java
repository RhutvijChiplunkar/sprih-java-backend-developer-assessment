package com.taskmanagement.repository;

import com.taskmanagement.model.Task;

import java.util.List;
import java.util.function.Predicate;

public interface TaskRepository {
    Task save(Task task);
    Task findById(String id);
    List<Task> findAll();
    List<Task> findAll(Predicate<Task> filter);
    boolean deleteById(String id);
    boolean existsById(String id);
}

