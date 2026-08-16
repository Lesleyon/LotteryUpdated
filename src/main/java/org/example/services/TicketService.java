package org.example.services;
import org.example.models.*;
import org.example.repositories.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class TicketService {
    private final TicketRepository ticketRepository = new TicketRepository();
    private final DrawRepository drawRepository = new DrawRepository();
    public Ticket buyTicket(Long drawId, List<Integer> numbers) throws SQLException {
        Draw draw = drawRepository.findById(drawId);
        if (draw == null || draw.status != Status.ACTIVE) {
            throw new IllegalStateException("Тираж не активен");
        }
        if (numbers.size() != 6) {
            throw new IllegalArgumentException("Нужно 6 чисел не больше,не меньше");
        }

        for (Integer n : numbers) {
            if (n < 1 || n > 45) {
                throw new IllegalArgumentException("Нужно 6 целых чисел от 1 до 45");
            }
        }
        Ticket ticket = new Ticket(
                null,
                drawId,
                numbers,
                Status.PENDING,
                new Timestamp(System.currentTimeMillis())
        );
        return ticketRepository.save(ticket);
    }
    public Ticket getTicketResult(Long ticketId) throws SQLException {
        Ticket ticket = ticketRepository.findById(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Билет не найден");
        }

        Draw draw = drawRepository.findById(ticket.drawId);
        if (draw == null || draw.status != Status.COMPLETED) {
            throw new IllegalStateException("Тираж ещё не завершён");
        }
        return ticket;
    }
}