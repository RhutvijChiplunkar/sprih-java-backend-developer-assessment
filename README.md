# Task Management System

A simple Task Management System built with plain Java as a part of assessment.

## Features

### Core Features
- **Create Task**: Create tasks with title, description (optional), due date (optional), priority, and status
- **Update Task**: Modify any field of an existing task
- **Delete Task**: Remove tasks by ID
- **List Tasks**: List all tasks with optional filtering by:
  - Status (PENDING, IN_PROGRESS, COMPLETED)
  - Priority (LOW, MEDIUM, HIGH)
  - Due date range
- **Sorting**: Sort tasks by due date, priority, or title (ascending/descending)

### Additional Features
- Optional CLI interface for interactive use

## Building the Project

### Compile the project:
```bash
mvn compile
```

### Run tests:
```bash
mvn test
```

### Compile and run tests:
```bash
mvn clean test
```

### Package the project:
```bash
mvn package
```

This will create a JAR file in the `target` directory.

## Running the Application

### Using the CLI Interface

The application includes a simple command-line interface for interactive use:

```bash
mvn exec:java -Dexec.mainClass="com.taskmanagement.cli.TaskManagementCLI"
```

Or if you have the JAR file:
```bash
java -cp target/task-management-system-1.0.0.jar com.taskmanagement.cli.TaskManagementCLI
```

### CLI Commands

- Create a new task
- Update an existing task
- Delete a task
- List all tasks (with optional filters and sorting)
- Get a task by ID
- Show help message
- Exit the application


## Testing

Run all tests:
```bash
mvn test
```

Run tests with coverage (if you have JaCoCo plugin configured):
```bash
mvn clean test jacoco:report
```


