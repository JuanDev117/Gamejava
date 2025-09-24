package Main;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {

    private static final String[] AVAILABLE_CHARACTERS =
            {"niño de 14 años", "MILITAR", "POLICIA", "GUSTAVO", "PONCHO"};

    private static final String[] AVAILABLE_WEAPONS =
            {"PISTOLA", "FUSIL", "TEASER", "GARROTE", "PEÑON"};

    public static void main(String[] args) throws IOException {
        String host = "10.10.9.201"; // Cambia si el server está en otra máquina
        int port = 5000;

        Socket socket = new Socket(host, port);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);


        Thread reader = new Thread(() -> {
            try {
                String s;
                while ((s = in.readLine()) != null) {


                    if (s.startsWith("DAMAGE:")) {
                        // Formato esperado: DAMAGE:<daño>:<atacante>:<arma>
                        String[] parts = s.split(":");
                        if (parts.length >= 4) {
                            System.out.println("[SERVER] ¡HAS SIDO ATACADO!");
                            System.out.println("  -> Daño recibido: -" + parts[1] + " (por " + parts[2] + " con " + parts[3] + ")");
                        } else {
                            System.out.println("[SERVER] " + s);
                        }
                    } else if (s.startsWith("HP_LEFT:")) {
                        // Mensaje del servidor que indica la HP restante después de un ataque
                        String hpValue = s.substring(8);
                        System.out.println("  -> Tu vida actual es: " + hpValue + " HP.");
                    } else if (s.startsWith("ATTACK_SUCCESS:")) {
                        // Mensaje para el atacante
                        String damageInfo = s.substring(15);
                        System.out.println("[SERVER] Ataque exitoso. Daño infligido: " + damageInfo);
                    } else {
                        // Muestra todos los demás mensajes (WELCOME, PROFILE_SET, YOU_WIN, etc.)
                        System.out.println("[SERVER] " + s);
                    }


                }
            } catch (IOException e) {
                System.out.println("Conexión con el servidor cerrada.");
            }
        });
        reader.start();

        System.out.println("=== Cliente de la Arena de Guerreros ===");
        System.out.print("Tu nombre: ");
        String name = sc.nextLine();
        out.println("NAME:" + name);

        // Selección de Personaje
        System.out.println("\n--- 1. Elige tu Personaje/Clase ---");
        System.out.println("Personajes: " + String.join(", ", AVAILABLE_CHARACTERS));
        System.out.print("Comando ELEGIR (ej: ELEGIR:MILITAR): ");
        String characterChoice = sc.nextLine().trim();

        if (characterChoice.toUpperCase().startsWith("ELEGIR:")) {
            out.println("SELECT_WEAPON:" + characterChoice.substring(7).trim());
        } else {
            out.println("SELECT_WEAPON:" + characterChoice);
        }

        System.out.println("Esperando confirmación del personaje...");

        // Mostrar armas disponibles antes del bucle principal
        System.out.println("\n--- 2. Puedes elegir tu arma en cualquier momento ---");
        System.out.println("Armas disponibles: " + String.join(", ", AVAILABLE_WEAPONS));
        System.out.println("Usa el comando ARMAR:<arma> (ej: ARMAR:FUSIL)");

        // Bucle principal de comandos
        while (true) {
            System.out.print("\nComando (GOLPE/ESTADO/ELEGIR:<personaje>/ARMAR:<arma>/SALIR): ");
            String cmd = sc.nextLine().trim();
            String upperCmd = cmd.toUpperCase();

            String serverCommand = "";

            if (upperCmd.equals("SALIR")) {
                serverCommand = "EXIT";
                out.println(serverCommand);
                break;
            } else if (upperCmd.equals("GOLPE")) {
                serverCommand = "ATTACK";
                out.println(serverCommand);
            } else if (upperCmd.equals("ESTADO")) {
                serverCommand = "STATUS";
                out.println(serverCommand);
            } else if (upperCmd.startsWith("ELEGIR:")) {
                String characterName = cmd.substring(7).trim();
                serverCommand = "SELECT_WEAPON:" + characterName;
                out.println(serverCommand);
            } else if (upperCmd.startsWith("ARMAR:")) {
                String weaponName = cmd.substring(6).trim();
                serverCommand = "SELECT_WEAPON:" + weaponName;
                out.println(serverCommand);
            } else {
                System.out.println("Comando desconocido..");
            }
        }

        System.out.println("Cerrando conexión...");
        socket.close();
        sc.close();
    }
}