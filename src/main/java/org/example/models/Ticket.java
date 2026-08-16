package org.example.models;
import java.sql.Timestamp;
import java.util.List;

public class Ticket {
    public Long id;
    public Long drawId;
    public List<Integer> numbers;
    public Status status;
    public Timestamp createdAt;
    public Ticket(Long id, Long drawId, List<Integer> numbers, Status status, Timestamp createdAt) {
        this.id = id;
        this.drawId = drawId;
        this.numbers = numbers;
        this.status = status;
        this.createdAt = createdAt;
    }
}
