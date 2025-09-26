
package Main;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 🔑 Servidor fijo (cámbialo si tu compañero usa otra IP/puerto)
        String host = "10.10.10.182";
        int port = 5000;

        try {
            Socket socket = new Socket(host, port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Hilo lector para recibir mensajes del servidor
            Thread reader = new Thread(() -> {
                try {
                    String s;
                    while ((s = in.readLine()) != null) {
                        if (s.equals("YOU_WIN")) {
                            System.out.println("\nASI ES QUE ES !! GANASTE!!.");
                            break;
                        } else if (s.equals("YOU_LOSE")) {
                            System.out.println("\n💀 TE MATARON.");
                            break;
                        } else {
                            System.out.println(" " + s);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada.");
                } finally {
                    try { socket.close(); } catch (IOException ignored) {}
                    System.out.println("\n=== Fin de la partida ===");
                    System.exit(0);
                }
            });
            reader.start();

            // Paso 1: elegir nombre
            System.out.println("\n=== REGISTRO DE JUGADOR ===");
            System.out.print("Tu nombre: ");
            String name = sc.nextLine();
            out.println("NAME:" + name);

            // Paso 2: elegir arma
            String[] armas = {"PEÑON", "CHUZO PARA HISCOTEA ", "REVOLVER", "HIERRO CALIENTE"};
            System.out.println("\n=== SELECCIÓN DE ARMA ===");
            for (int i = 0; i < armas.length; i++) {
                System.out.println((i + 1) + ". " + armas[i]);
            }
            System.out.print("Elige arma (1-" + armas.length + "): ");
            int choice = sc.nextInt();
            sc.nextLine(); // limpiar buffer
            if (choice < 1 || choice > armas.length) {
                choice = 1; // por defecto "Puños"
            }
            String weapon = armas[choice - 1];
            out.println("SELECT_WEAPON:" + weapon);

            // Paso 3: menú principal
            System.out.println("\n=== COMANDOS ===");
            System.out.println("ATTACK(1)- Atacar a tu oponente");
            System.out.println("STATUS(2)- Ver tu vida");
            System.out.println("EXIT   - Salir del juego\n");

            while (true)  {
                System.out.print("Comando: ");
                String cmd = sc.nextLine().trim().toUpperCase();

                switch (cmd) {
                    case "1":
                    case "ATTACK":
                        out.println("ATTACK");
                        break;

                    case "2":
                    case "STATUS":
                        out.println("STATUS");
                        break;

                    case "3":
                    case "EXIT":
                        out.println("EXIT");
                        sc.close();
                        return; // salir del while y cerrar cliente

                    default:
                        System.out.println("Comando no válido. Usa A, S, E o ATTACK, STATUS, EXIT.");
                        break;
                }

            }


        } catch (IOException e) {
            System.out.println("Error conectando al servidor: " + e.getMessage());
 }}
}