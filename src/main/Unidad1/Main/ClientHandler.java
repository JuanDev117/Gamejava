package Main;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

// Clase simple para contener las propiedades de un personaje para el servidor
class CharacterProfile {
    public final String defaultWeaponName;
    public final int defaultDamage;
    public final int startingHP;

    public CharacterProfile(String weaponName, int damage, int hp) {
        this.defaultWeaponName = weaponName;
        this.defaultDamage = damage;
        this.startingHP = hp;
    }
}

// Clase simple para representar el arma en el contexto del servidor
class ServerWeapon {
    private String name;
    private int damage;

    public ServerWeapon(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }
}


// Maneja la comunicación con un cliente (una conexión)
public class ClientHandler extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientHandler opponent;
    private String playerName;
    private int hp; // HP será dinámico según el personaje
    private ServerWeapon playerWeapon; // Arma/daño dinámico


    // En tu ClientHandler.java, agrega este método:
    public String getPlayerName() {
        return playerName != null ? playerName : "Jugador";
    }
    // ⚔️ MAPEO DE PERSONAJES Y SUS ESTADÍSTICAS (HP y Arma) ⚔️
    private static final Map<String, CharacterProfile> CHARACTER_PROFILES = new HashMap<>();
    static {
        // Los nombres de las claves deben coincidir con los que envía el cliente (en mayúsculas)
        CHARACTER_PROFILES.put("CHIRRETE", new CharacterProfile("Puño Default", 15, 60));
        CHARACTER_PROFILES.put("MILITAR", new CharacterProfile("Fusil ", 25, 200));
        CHARACTER_PROFILES.put("POLICIA", new CharacterProfile("Pistola", 20, 200));
        CHARACTER_PROFILES.put("GUSTAVO", new CharacterProfile("Garrote", 17, 200));
        CHARACTER_PROFILES.put("PONCHO", new CharacterProfile("Peñon", 22, 200));
    }

    // 🔫 MAPEO DE ARMAS SUELTAS (para el comando ARMAR) 🔫
    private static final Map<String, ServerWeapon> AVAILABLE_WEAPONS = new HashMap<>();
    static {
        AVAILABLE_WEAPONS.put("PISTOLA", new ServerWeapon("Pistola", 20));
        AVAILABLE_WEAPONS.put("FUSIL", new ServerWeapon("Fusil ", 25));
        AVAILABLE_WEAPONS.put("TEASER", new ServerWeapon("Teaser", 10));
        AVAILABLE_WEAPONS.put("GARROTE", new ServerWeapon("Garrote", 17));
        AVAILABLE_WEAPONS.put("PEÑON", new ServerWeapon("Peñon", 22));
    }


    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setOpponent(ClientHandler opp) {
        this.opponent = opp;
        sendMessage("OPPONENT_FOUND:" + (opp != null ? opp.playerName : "none"));
    }

    // Enviar mensaje al cliente
    public void sendMessage(String msg) {
        out.println(msg);
    }

    /**
     * Intenta establecer el perfil de personaje (cambia HP) o solo el arma (no cambia HP).
     */
    private void setPlayerProfile(String profileOrWeapon) {
        profileOrWeapon = profileOrWeapon.toUpperCase().trim();

        // 1. Intentar establecer el Personaje/Perfil (ELEGIR)
        CharacterProfile profile = CHARACTER_PROFILES.get(profileOrWeapon);
        if (profile != null) {
            this.hp = profile.startingHP; // <--- 🎯 AQUI SE ESTABLECE EL HP DEL PERSONAJE
            this.playerWeapon = new ServerWeapon(profile.defaultWeaponName, profile.defaultDamage);
            sendMessage("PROFILE_SET:" + profileOrWeapon + ", HP:" + this.hp + ", Arma:" + playerWeapon.getName());
            return;
        }

        // 2. Intentar establecer un Arma suelta (ARMAR)
        ServerWeapon weapon = AVAILABLE_WEAPONS.get(profileOrWeapon);
        if (weapon != null) {
            this.playerWeapon = weapon;
            // El HP NO se cambia al solo cambiar el arma.
            sendMessage("WEAPON_SET:" + weapon.getName() + ", Damage:" + weapon.getDamage() + ", HP actual:" + this.hp);
            return;
        }


    }


    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                String upperLine = line.toUpperCase().trim();
                System.out.println("Recibido de " + (playerName != null ? playerName : "un cliente") + ": " + line);

                if (upperLine.startsWith("NAME:")) {
                    playerName = line.substring(5).trim();
                    sendMessage("WELCOME " + playerName + ". HP inicial: " + hp);

                } else if (upperLine.startsWith("SELECT_WEAPON:")) { // Viene de ELEGIR o ARMAR
                    String profileOrWeapon = line.substring(14).trim();
                    setPlayerProfile(profileOrWeapon);

                } else if (upperLine.equals("ATTACK") && opponent != null) { // Viene de GOLPE

                    // 💥 DAÑO DINÁMICO: Usamos el daño del arma actual
                    int damageDealt = playerWeapon.getDamage();
                    String weaponName = playerWeapon.getName();

                    synchronized (opponent) {
                        opponent.hp -= damageDealt; // <--- 🎯 DAÑO VARIABLE APLICADO

                        // Notificar al oponente con el daño real y el arma
                        opponent.sendMessage("DAMAGE:" + damageDealt + ":" + playerName + ":" + weaponName);
                        opponent.sendMessage("HP_LEFT:" + opponent.hp);

                        if (opponent.hp <= 0) {
                            opponent.hp = 0;
                            sendMessage("YOU_WIN");
                            opponent.sendMessage("YOU_LOSE");
                        } else {
                            sendMessage("ATTACK_SUCCESS:" + damageDealt + " con " + weaponName);
                        }
                    }
                } else if (upperLine.equals("STATUS")) { // Viene de ESTADO
                    sendMessage("HP:" + hp);
                    sendMessage("WEAPON:" + playerWeapon.getName() + ":" + playerWeapon.getDamage());
                } else {
                    sendMessage("UNKNOWN_CMD");
                }
            }
        } catch (IOException e) {
            System.out.println("Error en handler de " + playerName + ": " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
            System.out.println((playerName != null ? playerName : "Cliente") + " se ha desconectado.");
        }
    }
}