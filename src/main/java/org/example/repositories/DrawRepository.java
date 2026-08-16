package org.example.repositories;
import org.example.mainframe.DatabaseConnector;
import org.example.models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DrawRepository {
    public Draw save(Draw draw) throws SQLException {
        String sql = "INSERT INTO draws (name, status, winning_numbers, created_at, completed_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, draw.name);
            ps.setString(2, draw.status.name());
            ps.setArray(3, conn.createArrayOf("INTEGER", draw.winningNumbers != null ?
                    draw.winningNumbers.toArray() : null));
            ps.setTimestamp(4, draw.createdAt);
            ps.setTimestamp(5, draw.completedAt);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                draw.id = rs.getLong(1);
            }
        }
        return draw;
    }

    public List<Draw> findByStatus(Status status) throws SQLException {
        List<Draw> draws = new ArrayList<>();
        String sql = "SELECT * FROM draws WHERE status = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                draws.add(mapRow(rs));
            }
        }
        return draws;
    }

    public Draw findById(Long id) throws SQLException {
        String sql = "SELECT * FROM draws WHERE id = ?";
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

    public void update(Draw draw) throws SQLException {
        String sql = "UPDATE draws SET status = ?, winning_numbers = ?, completed_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, draw.status.name());
            ps.setArray(2, conn.createArrayOf("INTEGER", draw.winningNumbers != null ?
                    draw.winningNumbers.toArray() : null));
            ps.setTimestamp(3, draw.completedAt);
            ps.setLong(4, draw.id);
            ps.executeUpdate();
        }
    }

    private Draw mapRow(ResultSet rs) throws SQLException {
        List<Integer> winningNumbers = null;
        Array array = rs.getArray("winning_numbers");
        if (array != null) {
            Integer[] tempNumbers = (Integer[]) array.getArray();
            winningNumbers = Arrays.asList(tempNumbers);
        }

        return new Draw(
                rs.getLong("id"),
                rs.getString("name"),
                Status.valueOf(rs.getString("status")),
                winningNumbers,
                rs.getTimestamp("created_at"),
                rs.getTimestamp("completed_at")
        );
    }
}