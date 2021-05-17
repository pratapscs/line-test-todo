package com.iphayao.linetest.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public Optional<Todo> createTodo(String userId, String todoMessage) throws TodoItemDuplicateException {
        Todo todo = extractTodoMessage(userId, todoMessage);

        List<Todo> todos = todoRepository.findByUserIdAndTypeAndActionAndDateTime(
                todo.getUserId(), todo.getType(), todo.getAction(), todo.getDateTime());
        if(todos.size() > 0) {
            throw new TodoItemDuplicateException();
        }

        return Optional.of(todoRepository.save(todo));
    }

    public Todo extractTodoMessage(String userId, String todoMessage) {
        String[] messages = todoMessage.split("\\ : ");

        TodoType type;
        String date = messages[3];
        String time = "00:00";
        if(messages.length == 5) {
            time = messages[4];
        }
        LocalDateTime dateTime = null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/[yyyy][yy] HH:mm");

        switch (messages[1]) {
            case "date/month/year":
                type = TodoType.ByDate;
                dateTime = LocalDateTime.parse(String.format("%s %s", date, time), formatter);
                break;
            case "today":
                type = TodoType.Today;
                dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(time));
                break;
            case "tomorrow":
                type = TodoType.Tomorrow;
                dateTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse(time));
                break;
            default:
                type = TodoType.Other;
                break;
        }

        return Todo.builder()
                .userId(userId)
                .type(type)
                .action(messages[2].substring(9).trim())
                .dateTime(dateTime)
                .done(false)
                .importance(false)
                .build();
    }

    public List<Todo> retrieveTodoByUserId(String userId) {
        List<Todo> todos = todoRepository.findByUserId(userId);
        todos.sort(new Comparator<Todo>() {
            @Override
            public int compare(Todo a, Todo b) {
                return a.getDateTime().compareTo(b.getDateTime());
            }
        });
        return todos;
    }

    public Todo retrieveTodoByUserId(int id, String userId) {
        return todoRepository.findByIdAndUserId(id, userId);
    }

    public List<Todo> retrieveTodoByUserIdWithImportance(String userId) {
        List<Todo> todosImp = todoRepository.findByUserIdAndImportanceTrueOrderByDateTime(userId);
        List<Todo> todosNotImp = todoRepository.findByUserIdAndImportanceFalseOrderByDateTime(userId);

        return Stream.concat(todosImp.stream(), todosNotImp.stream())
                .collect(Collectors.toList());

    }

    public Todo markTodoDone(int todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if(todo.isPresent()) {
            todo.get().setDone(true);
            return todoRepository.save(todo.get());
        }
        return null;
    }

    public Todo markTodoImportance(int todoId) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if(todo.isPresent()) {
            todo.get().setImportance(true);
            return todoRepository.save(todo.get());
        }
        return null;
    }

    public Todo editTodo(String userId, int todoId, Todo editTodo) {
        Optional<Todo> todo = todoRepository.findById(todoId);
        if(todo.isPresent()) {
            editTodo.setUserId(userId);
            editTodo.setId(todoId);
            editTodo.setType(todo.get().getType());
            return todoRepository.save(editTodo);
        }

        return null;
    }

    public Map<String, List<Todo>> groupTodoCompleted() {
        List<Todo> todos = todoRepository.findByDoneTrueAndDateTimeBefore(LocalDateTime.now());
        return groupTodoByUserId(todos);
    }

    private Map<String, List<Todo>> groupTodoByUserId(List<Todo> todos) {
        Map<String, List<Todo>> group = new HashMap<>();
        for(Todo todo : todos) {
            String userId = todo.getUserId();
            if(group.isEmpty()) {
                group.put(userId, getList(todo));
            } else {
                if(group.containsKey(userId)) {
                    List<Todo> entry = group.get(userId);
                    entry.add(todo);
                    group.put(userId, entry);
                } else {
                    group.put(userId, getList(todo));
                }
            }
        }
        return group;
    }

    private LinkedList<Todo> getList(Todo todo) {
        return new LinkedList<>(Collections.singletonList(todo));
    }
}
