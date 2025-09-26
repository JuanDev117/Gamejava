package Main;
import java.io.*;
import java.net.*;


// Maneja la comunicación con un cliente (una conexión)
public class ClientHandler extends Thread {
    private String weapon = "Machete con tetano "; // arma por defecto
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ClientHandler opponent; // referencia al oponente cuando esté emparejado
    private String playerName;
    private int hp = 200; // estado simple del jugador en el servidor

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void setOpponent(ClientHandler opp) {
        this.opponent = opp;
        sendMessage("Tu oponente es: " + opp.getPlayerName());
    }

    // Enviar mensaje al cliente
    public void sendMessage(String msg) {
        out.println(msg);
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void run() {
        try {
            // Leemos el nombre del jugador (protocol: NAME:<nombre>)
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Recibido: " + line);

                if (line.startsWith("NAME:")) {
                    String nombre = line.substring(5).trim();
                    if (nombre.isEmpty()) {
                        sendMessage("El nombre no puede estar vacío. Por favor, ingresa un nombre válido.");
                        continue;
                    }
                    playerName = nombre;
                    sendMessage("BIENVENIDO, ADELANTE: " + playerName);
                } else if (line.startsWith("SELECT_WEAPON:")) {
                    weapon = line.substring("SELECT_WEAPON:".length());
                    sendMessage("Arma seleccionada: " + weapon);
                    System.out.println(playerName + " seleccionó el arma: " + weapon);
                } else if (line.equals("ATTACK")) {
                    // Validar que el oponente existe y ambos tienen nombre
                    if (opponent == null || opponent.getPlayerName() == null || opponent.getPlayerName().isEmpty() || this.playerName == null || this.playerName.isEmpty()) {
                        sendMessage("No puedes atacar hasta que ambos jugadores estén listos.");
                        return;
                    }
                    // No permitir atacar si la partida terminó
                    if (opponent.hp <= 0 || this.hp <= 0) {
                        sendMessage("La partida ya terminó.");
                        return;
                    }
                    synchronized (opponent) {
                        if (opponent.hp <= 0 || this.hp <= 0) {
                            sendMessage("La partida ya terminó.");
                            return;
                        }
                        opponent.hp -= 70;
                        if (opponent.hp < 0) opponent.hp = 0; // No permitir HP negativo

                        // Mensaje para el oponente
                        opponent.sendMessage(playerName + " te atacó con " + weapon + " (-70 HP, ahora tienes " + opponent.hp + ")");
                        // Mensaje para el atacante
                        sendMessage("Atacaste a " + opponent.playerName + " con " + weapon + " (-20 HP, ahora tiene " + opponent.hp + ")");
                        // Mensaje en consola del servidor
                        System.out.println(playerName + " atacó a " + opponent.playerName + " con " + weapon + " (-20 HP, " + opponent.playerName + " ahora tiene " + opponent.hp + ")");

                        if (opponent.hp == 0) {
                            sendMessage("YOU_WIN");
                            opponent.sendMessage("YOU_LOSE");
                            System.out.println(playerName + " ha ganado la partida. " + opponent.playerName + " ha perdido.");
                        }
                    }
                } else if (line.equals("STATUS")) {
                    sendMessage("HP:" + hp);
                } else {
                    sendMessage("NADA");
                }
            }
        } catch (IOException e) {
            System.out.println("Error en handler: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
 }}
}