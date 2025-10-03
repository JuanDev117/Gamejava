package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class GameClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        String host = "10.10.10.182"; 
        int port = 5000;

        try {
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Hilo lector para recibir mensajes
            Thread reader = new Thread(() -> {
                try {
                    in.lines().forEach(s -> {
                        switch (s) {
                            case "YOU_WIN" -> {
                                System.out.println("\n ¡Felicidades! Ganaste la partida.");
                                cerrar(socket);
                            }
                            case "YOU_LOSE" -> {
                                System.out.println("\n Lo siento, perdiste la partida.");
                                cerrar(socket);
                            }
                            default -> System.out.println("SERVER:" + s);
                        }
                    });
                } catch (Exception e) {
                    System.out.println("Conexión cerrada.");
                }
            });
            reader.start();

            // Registro
            System.out.println("\n=== REGISTRO DE JUGADOR ===");
            System.out.print("Tu nombre: ");
            out.println("NAME:" + sc.nextLine());

            // Selección armas
            String[] armas = {"Trompa", "Energia", "Ametralladora", "Navaja"};
            System.out.println("\n=== SELECCIÓN DE ARMAS ===");
            IntStream.range(0, armas.length)
                     .forEach(i -> System.out.println((i + 1) + ". " + armas[i]));

            System.out.println("Puedes elegir varias armas (ej: 1,3 para Trompa y Ametralladora): ");
            String armasSeleccionadas = Arrays.stream(sc.nextLine().split(","))
                    .map(String::trim)
                    .mapToInt(i -> {
                        try { return Integer.parseInt(i) - 1; }
                        catch (NumberFormatException e) { return -1; }
                    })
                    .filter(i -> i >= 0 && i < armas.length)
                    .mapToObj(i -> armas[i])
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            out.println("SELECT_WEAPONS:" + armasSeleccionadas);

            // Mapeo de comandos
            Map<String, Consumer<String>> commands = Map.of(
                "A", s -> atacar(sc, out, armasSeleccionadas),
                "ATTACK", s -> atacar(sc, out, armasSeleccionadas),
                "S", s -> out.println("STATUS"),
                "STATUS", s -> out.println("STATUS"),
                "E", s -> salir(sc, out, socket),
                "EXIT", s -> salir(sc, out, socket)
            );

            // Loop comandos
            System.out.println("\n=== COMANDOS ===");
            System.out.println("A / ATTACK - Atacar a tu oponente");
            System.out.println("S / STATUS - Ver tu vida");
            System.out.println("E / EXIT   - Salir del juego\n");

            while (true) {
                System.out.print("Comando: ");
                String cmd = sc.nextLine().trim().toUpperCase();
                commands.getOrDefault(cmd, s -> System.out.println("Comando no válido."))
                        .accept(cmd);
            }

        } catch (IOException e) {
            System.out.println("Error conectando al servidor: " + e.getMessage());
        }
    }

   private static void atacar(Scanner sc, PrintWriter out, String armasSeleccionadas) {
    String[] armasArr = armasSeleccionadas.split(",");
    IntStream.range(0, armasArr.length)
             .forEach(i -> System.out.println((i + 1) + ". " + armasArr[i]));
    System.out.print("Opción: ");
    try {
        int opc = Integer.parseInt(sc.nextLine().trim());
        if (opc >= 1 && opc <= armasArr.length) {
            out.println("ATTACK:" + armasArr[opc - 1].toLowerCase());
        } else {
            System.out.println("Arma inválida.");
        }
    } catch (NumberFormatException e) {
        System.out.println("Entrada inválida.");
    }
}


    private static void salir(Scanner sc, PrintWriter out, Socket socket) {
        out.println("EXIT");
        sc.close();
        cerrar(socket);
        System.exit(0);
    }

    private static void cerrar(Socket socket) {
        try { socket.close(); } catch (IOException ignored) {}
        System.out.println("\n=== Fin de la partida ===");
    }
}
