package com.iphayao.linetest.todo;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TodoSchedule {
    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private TodoService todoService;

    @Scheduled(cron = "${app.cron-exp}")
    public void handleSummary() {
        log.info("Schedule was triggered.");
        Map<String, List<Todo>> todoCompleted = todoService.groupTodoCompleted();

        todoCompleted.forEach((k, v) -> {
            String textMessage = String.format("Your todo was completed, %s", v.toString());
            push(k, new TextMessage(textMessage));
        });

    }

    private void push(String userId, Message message) {
        lineMessagingClient.pushMessage(new PushMessage(userId, message));
    }


}
