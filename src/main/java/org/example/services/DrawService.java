package org.example.services;
import org.example.models.*;
import org.example.repositories.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;

public class DrawService {
    private final DrawRepository drawRepository = new DrawRepository();
    private final TicketRepository ticketRepository = new TicketRepository();
    public Draw createDraw(String name) throws SQLException {
        Draw draw = new Draw(
                null,
                name,
                Status.ACTIVE,
                null,
                new Timestamp(System.currentTimeMillis()),
                null
        );
        return drawRepository.save(draw);
    }
    public List<Draw> getActiveDraws() throws SQLException {
        return drawRepository.findByStatus(Status.ACTIVE);
    }
    public Draw completeDraw(Long drawId) throws SQLException {
        Draw draw = drawRepository.findById(drawId);
        if (draw == null || draw.status == Status.COMPLETED) {
            throw new IllegalStateException("Тираж не найден или уже завершён");
        }
        List<Integer> winning = generateWinningNumbers();
        draw.winningNumbers = winning;
        draw.status = Status.COMPLETED;
        draw.completedAt = new Timestamp(System.currentTimeMillis());
        drawRepository.update(draw);
        List<Ticket> tickets = ticketRepository.findByDrawId(drawId);
        for (Ticket ticket : tickets) {
            if (ticket.numbers.equals(winning)) {
                ticketRepository.updateStatus(ticket.id, Status.WIN);
            } else {
                ticketRepository.updateStatus(ticket.id, Status.LOSE);
            }
        }
        return draw;
    }

    private List<Integer> generateWinningNumbers() {
        List<Integer> numbers = new ArrayList<>();
        Random random = new Random();
        while (numbers.size() < 6) {
            int n = random.nextInt(45) + 1;
            numbers.add(n);
        }
        return numbers;
    }
}