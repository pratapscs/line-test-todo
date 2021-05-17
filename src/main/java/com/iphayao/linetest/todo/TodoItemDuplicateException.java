package com.iphayao.linetest.todo;

public class TodoItemDuplicateException extends Exception {
    public TodoItemDuplicateException() {
        super("TODO item is duplicate.");
    }
}
