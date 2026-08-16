package org.example.controller;
import com.google.gson.Gson;
import org.example.models.*;
import org.example.services.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;

public class LotteryController {
    private final DrawService drawService = new DrawService();
    private final TicketService ticketService = new TicketService();
    private final Gson gson = new Gson();
    public void startServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            try {
                if (path.equals("/draws") && method.equals("POST")) {
                    Draw draw = drawService.createDraw("Тираж №" + System.currentTimeMillis());
                    sendJsonResponse(exchange, 201, draw);
                    return;
                }
                if (path.equals("/draws") && method.equals("GET")) {
                    List<Draw> draws = drawService.getActiveDraws();
                    sendJsonResponse(exchange, 200, draws);
                    return;
                }
                if (path.matches("/draws/\\d+/tickets") && method.equals("POST")) {
                    String[] parts = path.split("/");
                    Long drawId = Long.parseLong(parts[2]);
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Map<String, List<Double>> tempMap = gson.fromJson(body, new TypeToken<Map<String, List<Double>>>(){}.getType());
                    List<Double> doubleNumbers = tempMap.get("numbers");
                    List<Integer> numbers = new ArrayList<>();
                    for (Double tempNumbers : doubleNumbers) {
                        numbers.add(tempNumbers.intValue());
                    }
                    Ticket ticket = ticketService.buyTicket(drawId, numbers);
                    sendJsonResponse(exchange, 201, ticket);
                    return;
                }
                if (path.matches("/draws/\\d+/complete") && method.equals("POST")) {
                    String[] parts = path.split("/");
                    Long drawId = Long.parseLong(parts[2]);
                    Draw draw = drawService.completeDraw(drawId);
                    sendJsonResponse(exchange, 200, draw);
                    return;
                }
                if (path.matches("/tickets/\\d+") && method.equals("GET")) {
                    String[] parts = path.split("/");
                    Long ticketId = Long.parseLong(parts[2]);
                    Ticket ticket = ticketService.getTicketResult(ticketId);
                    sendJsonResponse(exchange, 200, ticket);
                    return;
                }
                sendError(exchange, 404, "Endpoint not found");
            } catch (SQLException e) {
                sendError(exchange, 500, "Database error: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                sendError(exchange, 400, e.getMessage());
            }
        });
        server.setExecutor(null);
        server.start();
        System.out.println("Сервер запущен на http://localhost:8080");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String response = gson.toJson(data);
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBytes.length));
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.flush();
        os.close();
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = "{\"error\":\"" + message + "\"}";
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Content-Length", String.valueOf(responseBytes.length));
        exchange.sendResponseHeaders(code, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.flush();
        os.close();
    }
}