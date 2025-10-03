package server;

import client.ClientHandler;
import stats.MatchResult;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    public static final List<MatchResult> results = new ArrayList<>();

    private static long startTime; // tiempo inicio partida

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor iniciado en el puerto " + port);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + socket);

                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);

                if (clients.size() == 2) {
                    // cuando hay 2 jugadores, empieza la partida
                    clients.get(0).setOpponent(clients.get(1));
                    clients.get(1).setOpponent(clients.get(0));
                    startTime = System.currentTimeMillis();
                }

                handler.start();
            }

        } catch (IOException e) {
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }

    // 📌 Acumular daño total de cada jugador
    public static void addDamage(String playerName, int damage) {
        synchronized (results) {
            MatchResult existing = results.stream()
                    .filter(r -> r.getPlayerName().equals(playerName))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.addDamage(damage);
            } else {
                results.add(new MatchResult(playerName, damage, 0));
            }
        }
    }

    public static void registerMatch(String winnerName, int lastDamage) {
        synchronized (results) {
            long duration = (System.currentTimeMillis() - startTime) / 1000; // duración real en seg

            MatchResult existing = results.stream()
                    .filter(r -> r.getPlayerName().equals(winnerName))
                    .findFirst()
                    .orElse(null);

            if (existing != null) {
                existing.addVictory();
                existing.addDamage(lastDamage);
                existing.setDuration(duration);
            } else {
                MatchResult newResult = new MatchResult(winnerName, lastDamage, duration);
                newResult.addVictory();
                results.add(newResult);
            }
            showGameStats();
        }
    }

    // Mostrar estadísticas
    public static synchronized void showGameStats() {
        System.out.println("\n=== Ranking por Daño Total ===");
        results.stream()
                .sorted((a, b) -> b.getTotalDamage() - a.getTotalDamage())
                .forEach(r -> System.out.println(r.getPlayerName() + " -> " + r.getTotalDamage() + " daño"));

        System.out.println("\n=== Jugadores con más Victorias ===");
        results.stream()
                .sorted((a, b) -> b.getVictories() - a.getVictories())
                .forEach(r -> System.out.println(r.getPlayerName() + " -> " + r.getVictories() + " victorias"));

        System.out.println("\n=== Duración Promedio de las Partidas ===");
        double promedio = results.stream()
                .mapToLong(MatchResult::getDuration)
                .average()
                .orElse(0.0);
        System.out.printf("Promedio: %.2f segundos%n", promedio);
    }

    // 📌 Método principal para iniciar el servidor
    public static void main(String[] args) {
        int port = 5000; // Puerto por defecto
        GameServer server = new GameServer(port);
        server.start();
    }
}