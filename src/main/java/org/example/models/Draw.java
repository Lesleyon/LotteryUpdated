package org.example.models;
import java.sql.Timestamp;
import java.util.List;

public class Draw {
    public Long id;
    public String name;
    public Status status;
    public List<Integer> winningNumbers;
    public Timestamp createdAt;
    public Timestamp completedAt;
    public Draw(Long id, String name, Status status, List<Integer> winningNumbers,
                Timestamp createdAt, Timestamp completedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.winningNumbers = winningNumbers;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

}