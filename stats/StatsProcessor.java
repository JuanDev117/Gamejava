package stats;

import java.util.*;
import java.util.stream.Collectors;

public class StatsProcessor {
    public static void showStats(List<MatchResult> results) {
        if (results.isEmpty()) {
            System.out.println("No hay partidas registradas aún.");
            return;
        }

        // === Ranking por Daño Total ===
        System.out.println("\n=== Ranking por Daño Total ===");
        results.stream()
            .collect(Collectors.groupingBy(MatchResult::getPlayerName,
                    Collectors.summingInt(MatchResult::getTotalDamage)))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue() + " daño"));

        // === Jugadores con más Victorias ===
        System.out.println("\n=== Jugadores con más Victorias ===");
        results.stream()
            .collect(Collectors.groupingBy(MatchResult::getPlayerName,
                    Collectors.summingInt(MatchResult::getVictories)))
            .entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(e -> System.out.println(e.getKey() + " -> " + e.getValue() + " victorias"));

        // === Duración Promedio de las Partidas ===
        System.out.println("\n=== Duración Promedio de las Partidas ===");
        double avgDuration = results.stream()
            .mapToLong(MatchResult::getDuration)
            .average()
            .orElse(0);
        System.out.printf("Promedio: %.2f segundos%n", avgDuration / 1000.0);
    }
}