package com.iphayao.linetest.todo;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Optional;

@Slf4j
@LineMessageHandler
public class TodoBotController {
    @Value("${app.edit-path}")
    private String editPath;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private TodoService todoService;

    @EventMapping
    public void handleTextMessage(MessageEvent<TextMessageContent> event) {
        log.info(event.toString());
        handleTextContent(event.getReplyToken(), event, event.getMessage());
    }

    public void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String userId = event.getSource().getUserId();
        String message = content.getText();
        String method = message.substring(0, 4).toLowerCase().trim();

        String textMessage = null;

        try {
            if (method.equals("task")) {
                Optional<Todo> todo = todoService.createTodo(userId, message);
                if (todo.isPresent()) {
                    textMessage = String.format("Your todo id %s was created %s", todo.get().getId(), todo.get().toString());
                }
            } else if (method.equals("edit")) {
                textMessage = editPath;
            } else {
                textMessage = message;
            }
        } catch (TodoItemDuplicateException e) {
            textMessage = "Your input task was duplicate.";
        }

        reply(replyToken, new TextMessage(textMessage));
    }

    private void reply(String reployToken, Message message) {
        lineMessagingClient.replyMessage(new ReplyMessage(reployToken, message));
    }



}
