package com.iphayao.linetest.todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Todo implements Comparable<Todo> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private TodoType type;
    private String userId;
    private LocalDateTime dateTime;
    private String action;
    private boolean done;
    private boolean importance;

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", type=" + type +
                ", date='" + dateTime + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

    @Override
    public int compareTo(Todo o) {
        return this.dateTime.compareTo(o.getDateTime());
    }
}
