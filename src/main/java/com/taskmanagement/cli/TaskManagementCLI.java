package com.taskmanagement.cli;

import com.taskmanagement.filter.TaskFilter;
import com.taskmanagement.model.Priority;
import com.taskmanagement.model.Status;
import com.taskmanagement.model.Task;
import com.taskmanagement.repository.InMemoryTaskRepository;
import com.taskmanagement.service.SortOption;
import com.taskmanagement.service.TaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class TaskManagementCLI {
    private final TaskService taskService;
    private final Scanner scanner;
    private final DateTimeFormatter dateFormatter;

    public TaskManagementCLI() {
        this.taskService = new TaskService(new InMemoryTaskRepository());
        this.scanner = new Scanner(System.in);
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public void run() {
        System.out.println("=== Task Management System ===");
        System.out.println("Welcome! Type 'help' for available commands.\n");

        boolean running = true;
        while (running) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            if (command.isEmpty()) {
                continue;
            }

            String[] parts = command.split("\\s+", 2);
            String action = parts[0];
            String args = parts.length > 1 ? parts[1] : "";

            try {
                switch (action) {
                    case "c":
                        createTask();
                        break;
                    case "u":
                        updateTask();
                        break;
                    case "d":
                        deleteTask();
                        break;
                    case "l":
                        listTasks();
                        break;
                    case "g":
                        getTask();
                        break;
                    case "h":
                        printHelp();
                        break;
                    case "q":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Unknown command. Type 'help' for available commands.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void createTask() {
        System.out.println("--- Create New Task ---");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Error: Title is required");
            return;
        }

        System.out.print("Description (optional): ");
        String description = scanner.nextLine().trim();
        Optional<String> descOpt = description.isEmpty() ? Optional.empty() : Optional.of(description);

        System.out.print("Due date (yyyy-MM-dd HH:mm, optional): ");
        String dueDateStr = scanner.nextLine().trim();
        Optional<LocalDateTime> dueDateOpt = Optional.empty();
        if (!dueDateStr.isEmpty()) {
            try {
                dueDateOpt = Optional.of(LocalDateTime.parse(dueDateStr, dateFormatter));
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Task created without due date.");
            }
        }

        System.out.print("Priority (LOW, MEDIUM, HIGH) [MEDIUM]: ");
        String priorityStr = scanner.nextLine().trim().toUpperCase();
        Priority priority = Priority.MEDIUM;
        if (!priorityStr.isEmpty()) {
            try {
                priority = Priority.valueOf(priorityStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid priority. Using MEDIUM.");
            }
        }

        Task task = taskService.createTask(title, descOpt, dueDateOpt, priority);
        System.out.println("Task created successfully!");
        printTask(task);
    }

    private void updateTask() {
        System.out.println("--- Update Task ---");
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Error: Task ID is required");
            return;
        }

        try {
            Task existing = taskService.getTask(id);
            System.out.println("Current task:");
            printTask(existing);
            System.out.println("\nLeave fields empty to keep current values.");

            System.out.print("New title (optional): ");
            String title = scanner.nextLine().trim();
            title = title.isEmpty() ? null : title;

            System.out.print("New description (optional, type 'clear' to remove): ");
            String description = scanner.nextLine().trim();
            Optional<String> descOpt = null;
            if (description.equals("clear")) {
                descOpt = Optional.empty();
            } else if (!description.isEmpty()) {
                descOpt = Optional.of(description);
            }

            System.out.print("New due date (yyyy-MM-dd HH:mm, optional, type 'clear' to remove): ");
            String dueDateStr = scanner.nextLine().trim();
            Optional<LocalDateTime> dueDateOpt = null;
            if (dueDateStr.equals("clear")) {
                dueDateOpt = Optional.empty();
            } else if (!dueDateStr.isEmpty()) {
                try {
                    dueDateOpt = Optional.of(LocalDateTime.parse(dueDateStr, dateFormatter));
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Due date not updated.");
                }
            }

            System.out.print("New priority (LOW, MEDIUM, HIGH, optional): ");
            String priorityStr = scanner.nextLine().trim().toUpperCase();
            Priority priority = null;
            if (!priorityStr.isEmpty()) {
                try {
                    priority = Priority.valueOf(priorityStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid priority. Priority not updated.");
                }
            }

            System.out.print("New status (PENDING, IN_PROGRESS, COMPLETED, optional): ");
            String statusStr = scanner.nextLine().trim().toUpperCase();
            Status status = null;
            if (!statusStr.isEmpty()) {
                try {
                    status = Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status. Status not updated.");
                }
            }

            Task updated = taskService.updateTask(id, title, descOpt, dueDateOpt, priority, status);
            System.out.println("Task updated successfully!");
            printTask(updated);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteTask() {
        System.out.println("--- Delete Task ---");
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Error: Task ID is required");
            return;
        }

        try {
            taskService.deleteTask(id);
            System.out.println("Task deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void getTask() {
        System.out.println("--- Get Task ---");
        System.out.print("Task ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("Error: Task ID is required");
            return;
        }

        try {
            Task task = taskService.getTask(id);
            printTask(task);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listTasks() {
        System.out.println("--- List Tasks ---");
        System.out.println("Apply filters? (y/n) [n]: ");
        String filterChoice = scanner.nextLine().trim().toLowerCase();
        
        TaskFilter filter = null;
        if (filterChoice.equals("y") || filterChoice.equals("yes")) {
            filter = TaskFilter.builder();
            
            System.out.print("Filter by status (PENDING, IN_PROGRESS, COMPLETED, optional): ");
            String statusStr = scanner.nextLine().trim().toUpperCase();
            if (!statusStr.isEmpty()) {
                try {
                    filter.byStatus(Status.valueOf(statusStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid status. Status filter not applied.");
                }
            }

            System.out.print("Filter by priority (LOW, MEDIUM, HIGH, optional): ");
            String priorityStr = scanner.nextLine().trim().toUpperCase();
            if (!priorityStr.isEmpty()) {
                try {
                    filter.byPriority(Priority.valueOf(priorityStr));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid priority. Priority filter not applied.");
                }
            }

            System.out.print("Filter by due date range start (yyyy-MM-dd HH:mm, optional): ");
            String startStr = scanner.nextLine().trim();
            LocalDateTime start = null;
            if (!startStr.isEmpty()) {
                try {
                    start = LocalDateTime.parse(startStr, dateFormatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. Start date filter not applied.");
                }
            }

            System.out.print("Filter by due date range end (yyyy-MM-dd HH:mm, optional): ");
            String endStr = scanner.nextLine().trim();
            LocalDateTime end = null;
            if (!endStr.isEmpty()) {
                try {
                    end = LocalDateTime.parse(endStr, dateFormatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format. End date filter not applied.");
                }
            }

            if (start != null || end != null) {
                filter.byDueDateRange(start, end);
            }
        }

        System.out.print("Sort by (DUE_DATE_ASC, DUE_DATE_DESC, PRIORITY_ASC, PRIORITY_DESC, TITLE_ASC, TITLE_DESC, optional): ");
        String sortStr = scanner.nextLine().trim().toUpperCase();
        SortOption sortOption = null;
        if (!sortStr.isEmpty()) {
            try {
                sortOption = SortOption.valueOf(sortStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid sort option. Results not sorted.");
            }
        }

        List<Task> tasks;
        if (sortOption != null) {
            tasks = taskService.listTasks(filter, sortOption);
        } else {
            tasks = taskService.listTasks(filter);
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
        } else {
            System.out.println("\nFound " + tasks.size() + " task(s):");
            tasks.forEach(this::printTask);
        }
    }

    private void printTask(Task task) {
        System.out.println("----------------------------------------");
        System.out.println("ID: " + task.getId());
        System.out.println("Title: " + task.getTitle());
        task.getDescription().ifPresent(desc -> System.out.println("Description: " + desc));
        task.getDueDate().ifPresent(date -> 
            System.out.println("Due Date: " + date.format(dateFormatter))
        );
        System.out.println("Priority: " + task.getPriority());
        System.out.println("Status: " + task.getStatus());
        System.out.println("----------------------------------------");
    }

    private void printHelp() {
        System.out.println("Available commands:");
        System.out.println("c - Create a new task");
        System.out.println("u - Update an existing task");
        System.out.println("d - Delete a task");
        System.out.println("l - List all tasks (with optional filters and sorting)");
        System.out.println("g - Get a task by ID");
        System.out.println("h - Show this help message");
        System.out.println("q - Exit the application");
    }

    public static void main(String[] args) {
        TaskManagementCLI cli = new TaskManagementCLI();
        cli.run();
    }
}

