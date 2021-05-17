package com.iphayao.linetest.todo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TodoServiceTest {
    @Autowired
    TodoRepository repository;

    @Autowired
    private TodoService todoService;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void testCreateTodoByDateTypeWithDateTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Buy milk";
        String expectDate = "2/5/18";
        String expectTime = "13:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/[yyyy][yy] HH:mm");
        LocalDateTime expectDateTime = LocalDateTime.parse(String.format("%s %s", expectDate, expectTime), formatter);

        String message = formatTodoMessage("date/month/year", expectAction, expectDate, expectTime);

        Todo expect = getTodoMock(TodoType.ByDate, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.ByDate, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testCreateTodoByDateTypeWithDateNoTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Buy milk";
        String expectDate = "2/5/18";
        String expectTime = "00:00"; // 12:00 PM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/[yyyy][yy] HH:mm");
        LocalDateTime expectDateTime = LocalDateTime.parse(String.format("%s %s", expectDate, expectTime), formatter);

        String message = formatTodoMessage("date/month/year", expectAction, expectDate);

        Todo expect = getTodoMock(TodoType.ByDate, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.ByDate, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testCreateTodoTodayTypeWithDateTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Finish writing shopping list";
        String expectDate = "today";
        String expectTime = "15:30";
        LocalDateTime expectDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(expectTime));

        String message = formatTodoMessage(expectDate, expectAction, expectDate, expectTime);

        Todo expect = getTodoMock(TodoType.Today, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.Today, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testCreateTodoTodayTypeWithDateNoTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Finish writing shopping list";
        String expectDate = "today";
        String expectTime = "00:00"; // 12:00 PM
        LocalDateTime expectDateTime = LocalDateTime.of(LocalDate.now(), LocalTime.parse(expectTime));

        String message = formatTodoMessage(expectDate, expectAction, expectDate, expectTime);

        Todo expect = getTodoMock(TodoType.Today, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.Today, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testCreateTodoTomorrowTypeWithDateTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Finish writing shopping list";
        String expectDate = "tomorrow";
        String expectTime = "15:30";
        LocalDateTime expectDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse(expectTime));

        String message = formatTodoMessage(expectDate, expectAction, expectDate, expectTime );

        Todo expect = getTodoMock(TodoType.Tomorrow, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.Tomorrow, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testCreateTodoTomorrowTypeWithDateNoTime() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Finish writing shopping list";
        String expectDate = "tomorrow";
        String expectTime = "00:00"; // 12:00 PM
        LocalDateTime expectDateTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.parse(expectTime));

        String message = formatTodoMessage(expectDate, expectAction, expectDate, expectTime );

        Todo expect = getTodoMock(TodoType.Tomorrow, userId, expectAction, expectDateTime);

        Todo result = todoService.createTodo(userId, message).orElse(null);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(TodoType.Tomorrow, result.getType());
        assertEquals(expect.getAction(), result.getAction());
        assertEquals(expect.getDateTime(), result.getDateTime());
        assertFalse(result.isDone());

    }

    @Test
    public void testRetrieveTodoByUserIdOrderAsAscending() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Buy milk";

        String[] days = {"5/5/18", "4/5/18", "3/5/18", "2/5/18", "1/5/18"};

        for(String day : days) {
            String message = formatTodoMessage("date/month/year", expectAction, day);
            todoService.createTodo(userId, message);
        }

        List<Todo> results = todoService.retrieveTodoByUserIdWithImportance(userId);

        assertEquals(5, results.size());
        assertEquals(1, results.get(0).getDateTime().getDayOfMonth());
        assertEquals(2, results.get(1).getDateTime().getDayOfMonth());
        assertEquals(3, results.get(2).getDateTime().getDayOfMonth());
        assertEquals(4, results.get(3).getDateTime().getDayOfMonth());
        assertEquals(5, results.get(4).getDateTime().getDayOfMonth());

    }

    @Test
    public void testMarkTodoDone() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Buy milk";
        String expectDate = "2/5/18";
        String expectTime = "13:00";

        String message = formatTodoMessage("date/month/year", expectAction, expectDate, expectTime);

        Todo todo = todoService.createTodo(userId, message).orElse(null);
        assertFalse(Objects.requireNonNull(todo).isDone()); // Make sure new create done is false

        Todo result = todoService.markTodoDone(Objects.requireNonNull(todo).getId());

        assertTrue(result.isDone());
    }

    @Test
    public void testMarkTodoImportance() throws TodoItemDuplicateException {
        String userId = "123456789";
        String expectAction = "Buy milk";
        String expectDate = "2/5/18";
        String expectTime = "13:00";

        String message = formatTodoMessage("date/month/year", expectAction, expectDate, expectTime);

        Todo todo = todoService.createTodo(userId, message).orElse(null);
        assertFalse(Objects.requireNonNull(todo).isImportance()); // Make sure new create importance is false

        Todo result = todoService.markTodoImportance(Objects.requireNonNull(todo).getId());

        assertTrue(result.isImportance());
    }

    private Todo getTodoMock(TodoType type, String userId, String expectAction, LocalDateTime expectDateTime) {
        return Todo.builder()
                .type(type)
                .userId(userId)
                .dateTime(expectDateTime)
                .action(expectAction)
                .build();
    }

    private String formatTodoMessage(String type, String action, String date, String time) {
        return String.format("task : %s : time e.g. %s : %s : %s", type, action, date, time);
    }

    private String formatTodoMessage(String type, String action, String date) {
        return String.format("task : %s : time e.g. %s : %s : ", type, action, date);
    }

}