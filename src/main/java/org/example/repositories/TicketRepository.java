package org.example.repositories;
import org.example.mainframe.DatabaseConnector;
import org.example.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TicketRepository {

    public Ticket save(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets (draw_id, numbers, status, created_at) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ticket.drawId);
            ps.setArray(2, conn.createArrayOf("INTEGER", ticket.numbers.toArray()));
            ps.setString(3, ticket.status.name());
            ps.setTimestamp(4, ticket.createdAt);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket.id = rs.getLong(1);
            }
        }
        return ticket;
    }

    public List<Ticket> findByDrawId(Long drawId) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets WHERE draw_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, drawId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tickets.add(mapRow(rs));
            }
        }
        return tickets;
    }

    public Ticket findById(Long id) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    public void updateStatus(Long id, Status status) throws SQLException {
        String sql = "UPDATE tickets SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    private Ticket mapRow(ResultSet rs) throws SQLException {
        List<Integer> numbers = null;
        Array array = rs.getArray("numbers");
        if (array != null) {
            Integer[] tempNumbers = (Integer[]) array.getArray();
            numbers = Arrays.asList(tempNumbers);
        }

        return new Ticket(
                rs.getLong("id"),
                rs.getLong("draw_id"),
                numbers,
                Status.valueOf(rs.getString("status")),
                rs.getTimestamp("created_at")
        );
    }
}