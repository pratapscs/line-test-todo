package com.iphayao.linetest.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/todos")
public class TodoWebController {
    @Autowired
    TodoService todoService;

    @GetMapping("/edit")
    public String editTodos(Principal principal, Model model) {
        model.addAttribute("todos", todoService.retrieveTodoByUserIdWithImportance(principal.getName()));
        return "todosEdit";
    }

    @GetMapping("/edit/{id}")
    public String editTodosById(Principal principal, @PathVariable int id, Model model) {
        model.addAttribute("todo", todoService.retrieveTodoByUserId(id, principal.getName()));
        return "todoEdit";
    }

    @PostMapping("/edit/{id}")
    public String saveTodos(Principal principal, @PathVariable int id, @ModelAttribute Todo form, Model model) {
        todoService.editTodo(principal.getName(), id, form);
        model.addAttribute("todos", todoService.retrieveTodoByUserIdWithImportance(principal.getName()));
        return "todosEdit";
    }
}
