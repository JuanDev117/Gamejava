package Main;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class GameClient {

    private static final String[] AVAILABLE_CHARACTERS =
            {"CHIRRETE", "MILITAR", "POLICIA", "GUSTAVO", "PONCHO"};

    private static final String[] AVAILABLE_WEAPONS =
            {"PISTOLA", "FUSIL", "TEASER", "GARROTE", "PEÑON"};

    public static void main(String[] args) throws IOException {
        String host = "10.10.10.182"; // Cambia si el server está en otra máquina
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
                        // Cuando recibes daño
                        String[] parts = s.split(":");
                        if (parts.length >= 4) {
                            System.out.println("[SERVER] ¡HAS SIDO ATACADO!");
                            System.out.println("  -> Daño recibido: -" + parts[1] + " (por " + parts[2] + " con " + parts[3] + ")");
                        } else {
                            System.out.println("[SERVER] " + s);
                        }
                    } else if (s.startsWith("HP_LEFT:")) {
                        // Vida restante
                        String hpValue = s.substring(8);
                        System.out.println("  -> Tu vida actual es: " + hpValue + " HP.");
                    } else if (s.startsWith("ATTACK_SUCCESS:")) {
                        // Ataque exitoso (formato esperado)
                        String damageInfo = s.substring(15);
                        System.out.println("[SERVER] Ataque exitoso. Daño infligido: " + damageInfo);
                    } else if (s.startsWith("YOU_ATTACKED:")) {
                        // ⭐ NUEVO: Procesar mensajes de ataque del servidor actual
                        String[] parts = s.split(":");
                        if (parts.length >= 3) {
                            System.out.println("[SERVER] ¡ATAQUE EXITOSO!");
                            System.out.println("  -> Daño infligido: " + parts[2] + " al enemigo " + parts[1]);
                        }
                    } else if (s.startsWith("YOU_WIN")) {
                        System.out.println("[SERVER]  ¡HAS GANADO LA BATALLA!");
                    } else if (s.startsWith("YOU_LOSE")) {
                        System.out.println("[SERVER]  ¡HAS PERDIDO LA BATALLA!");
                    } else if (s.startsWith("MATCH_START")) {
                        System.out.println("[SERVER] ⚔️ " + s);
                    } else if (s.startsWith("OPPONENT_FOUND:")) {
                        System.out.println("[SERVER]  " + s);
                    } else if (s.startsWith("PROFILE_SET:") || s.startsWith("WEAPON_SET:")) {
                        System.out.println("[SERVER]  " + s);
                    } else if (s.startsWith("WELCOME")) {
                        System.out.println("[SERVER]  " + s);
                    } else if (s.startsWith("ERROR:")) {
                        System.out.println("[SERVER]  " + s);
                    } else {
                        // Muestra todos los demás mensajes
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

        // Selección de Personaje - CON NÚMEROS
        System.out.println("\n--- 1. Elige tu Personaje/Clase ---");
        for (int i = 0; i < AVAILABLE_CHARACTERS.length; i++) {
            System.out.println((i + 1) + ". " + AVAILABLE_CHARACTERS[i]);
        }
        System.out.print("Escribe el número del personaje (1-5): ");
        String characterChoice = sc.nextLine().trim();

        // Procesar número a nombre del personaje
        String characterName = processNumberSelection(characterChoice, AVAILABLE_CHARACTERS);
        out.println("SELECT_WEAPON:" + characterName);

        System.out.println("Esperando confirmación del personaje...");

        // Selección de Arma - CON NÚMEROS
        System.out.println("\n--- 2. Elige tu Arma ---");
        for (int i = 0; i < AVAILABLE_WEAPONS.length; i++) {
            System.out.println((i + 1) + ". " + AVAILABLE_WEAPONS[i]);
        }
        System.out.print("Escribe el número del arma (1-5): ");
        String weaponChoice = sc.nextLine().trim();

        // Procesar número a nombre del arma
        String weaponName = processNumberSelection(weaponChoice, AVAILABLE_WEAPONS);
        out.println("SELECT_WEAPON:" + weaponName);

        // Bucle principal de comandos - SIN CAMBIOS
        while (true) {
            System.out.print("\nComando (GOLPE(1)/ESTADO(2)/ELEGIR:<personaje>/ARMAR:<arma>/SALIR(3)): ");
            String cmd = sc.nextLine().trim();
            String upperCmd = cmd.toUpperCase();

            String serverCommand = "";

            if (upperCmd.equals("3")) {
                serverCommand = "EXIT";
                out.println(serverCommand);
                break;
            } else if (upperCmd.equals("1")) {
                serverCommand = "ATTACK";
                out.println(serverCommand);
            } else if (upperCmd.equals("2")) {
                serverCommand = "STATUS";
                out.println(serverCommand);
            } else if (upperCmd.startsWith("ELEGIR:")) {
                String characterNameCmd = cmd.substring(7).trim();
                serverCommand = "SELECT_WEAPON:" + characterNameCmd;
                out.println(serverCommand);
            } else if (upperCmd.startsWith("ARMAR:")) {
                String weaponNameCmd = cmd.substring(6).trim();
                serverCommand = "SELECT_WEAPON:" + weaponNameCmd;
                out.println(serverCommand);
            } else {
                System.out.println("Comando desconocido..");
            }
        }

        System.out.println("Cerrando conexión...");
        socket.close();
        sc.close();
    }

    private static String processNumberSelection(String input, String[] availableOptions) {
        try {
            int number = Integer.parseInt(input.trim());
            if (number >= 1 && number <= availableOptions.length) {
                return availableOptions[number - 1];
            } else {
                System.out.println("Número inválido. Usando: " + input);
                return input;
            }
        } catch (NumberFormatException e) {
            // Si no es número, devolver el texto tal cual
            return input;
        }
    }
}