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


            String[] armas = {
                    "PEÑON",
                    "CHUZO PARA HISCOTEA",
                    "REVOLVER",
                    "HIERRO CALIENTE",
                    "MONA",
                    "ESCOPETA",
                    "M60",
                    "NAVAJA",
                    "BATE DE BEISBOL",
                    "ARCO Y FLECHA"
                                };

            // Hilo lector para recibir mensajes del servidor
            Thread reader = new Thread(() -> {
                try {
                    String s;
                    while ((s = in.readLine()) != null) {
                        if (s.equals("YOU_WIN")) {
                            System.out.println("\nASI ES QUE ES !! GANASTE!!.");
                            break;
                        } else if (s.equals("YOU_LOSE")) {
                            System.out.println("\n💀 TE MATARON POR PAQUETE!!!!.");
                            break;
                        } else if (s.startsWith("Arma seleccionada:")) {
                            System.out.println("✅ " + s);
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
            System.out.println("\n***=== REGISTRO DE JUGADOR ===***");
            System.out.print("Tu nombre: ");
            String name = sc.nextLine();
            out.println("NAME:" + name);

            // Paso 2: elegir arma INICIAL
            System.out.println("\n=== SELECCIÓN DE ARMA INICIAL ===");
            for (int i = 0; i < armas.length; i++) {
                System.out.println((i + 1) + ". " + armas[i]);
            }
            System.out.print("Elige arma inicial (1-" + armas.length + "): ");
            int choice = sc.nextInt();
            sc.nextLine(); // limpiar buffer
            if (choice < 1 || choice > armas.length) {
                choice = 1;
            }
            String currentWeapon = armas[choice - 1];
            out.println("SELECT_WEAPON:" + currentWeapon);
            System.out.println("✅ Arma inicial seleccionada: " + currentWeapon);

            // 🆕 PASO 3: MENÚ PRINCIPAL MEJORADO
            System.out.println("\n" + "=".repeat(50));
            System.out.println("=== ¡COMIENZA LA BATALLA! ===");
            System.out.println("Arma actual: " + currentWeapon);
            System.out.println("=".repeat(50));

            while (true)  {
                System.out.println("\n--- MENÚ DE COMANDOS ---");
                System.out.println("ATTACK (1) - Atacar a tu oponente");
                System.out.println("STATUS (2) - Ver tu vida y estado");
                System.out.println("CAMBIAR DE ARMA  (3) - Cambiar de arma");
                System.out.println("ARMAS (4) - Ver lista de armas disponibles");
                System.out.println("EXIT (5) - Salir del juego");
                System.out.print("\nComando: ");

                String cmd = sc.nextLine().trim().toUpperCase();

                switch (cmd) {
                    case "1":
                    case "ATTACK":
                        out.println("ATTACK");
                        System.out.println("⚔️ ¡Atacando con " + currentWeapon + "!");
                        break;

                    case "2":
                    case "STATUS":
                        out.println("STATUS");
                        System.out.println("📊 Solicitando estado...");
                        break;

                    case "3":
                    case "CHANGE_WEAPON":
                    case "CAMBIAR ARMA":
                        // 🆕 CAMBIAR ARMA DURANTE LA PARTIDA
                        System.out.println("\n--- CAMBIO DE ARMA ---");
                        System.out.println("Armas disponibles:");
                        for (int i = 0; i < armas.length; i++) {
                            System.out.println((i + 1) + ". " + armas[i]);
                        }
                        System.out.print("Elige nueva arma (1-" + armas.length + "): ");
                        try {
                            int newChoice = Integer.parseInt(sc.nextLine().trim());
                            if (newChoice >= 1 && newChoice <= armas.length) {
                                currentWeapon = armas[newChoice - 1];
                                out.println("SELECT_WEAPON:" + currentWeapon);
                                System.out.println("🔄 Cambiando a: " + currentWeapon);
                            } else {
                                System.out.println("❌ Número inválido. Usando arma actual.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("❌ Entrada inválida. Usando arma actual.");
                        }
                        break;

                    case "4":
                    case "WEAPONS":
                    case "ARMAS":
                        // 🆕 VER LISTA DE ARMAS
                        System.out.println("\n--- ARMAS DISPONIBLES ---");
                        for (int i = 0; i < armas.length; i++) {
                            System.out.println((i + 1) + ". " + armas[i] +
                                    (armas[i].equals(currentWeapon) ? " (ACTUAL)" : ""));
                        }
                        System.out.println("Arma actual: " + currentWeapon);
                        break;

                    case "5":
                    case "EXIT":
                    case "SALIR":
                        out.println("EXIT");
                        System.out.println("👋 Saliendo del juego...");
                        sc.close();
                        return;

                    default:
                        System.out.println("❌ Comando no válido. Comandos disponibles:");
                        System.out.println("   ATTACK(1), STATUS(2), CHANGE_WEAPON(3), WEAPONS(4), EXIT(5)");
                        break;
                }

            }


        } catch (IOException e) {
            System.out.println("Error conectando al servidor: " + e.getMessage());
        }
    }
}