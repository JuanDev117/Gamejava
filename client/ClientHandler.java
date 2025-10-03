package client;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private ClientHandler opponent;
    private String playerName;
    private int hp = 100;

    // Tiempo de inicio de la partida
    private long startTime;

    // Lista de armas seleccionadas
    private List<String> weapons = new ArrayList<>();

    // Mapa de daños
    private static final Map<String, Integer> DAMAGE_MAP = Map.of(
            "trompa", 10,
            "energia", 25,
            "ametralladora", 30,
            "navaja", 15
    );

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    private int calcularDanio(String arma) {
        return DAMAGE_MAP.getOrDefault(arma.toLowerCase(), 5);
    }

    public ClientHandler getOpponent() {
        return opponent;
    }

    public void setOpponent(ClientHandler opp) {
        this.opponent = opp;
        this.startTime = System.currentTimeMillis(); // cuando se asigna rival empieza la partida
        sendMessage("Tu oponente es: " + Optional.ofNullable(opp.getPlayerName()).orElse("Desconocido"));
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public String getPlayerName() {
        return playerName;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Recibido: " + line);

                final String input = line;

                Map<String, Consumer<String>> commands = Map.of(
                        "NAME:", this::procesarNombre,
                        "SELECT_WEAPONS:", this::procesarArmas, // aquí corregido plural
                        "ATTACK:", this::procesarAtaque,
                        "STATUS", s -> sendMessage("HP:" + hp)
                );

                Optional<Map.Entry<String, Consumer<String>>> cmd = commands.entrySet()
                        .stream()
                        .filter(e -> input.startsWith(e.getKey()))
                        .findFirst();

                cmd.ifPresentOrElse(
                        e -> e.getValue().accept(input),
                        () -> sendMessage("UNKNOWN_CMD")
                );
            }

        } catch (IOException e) {
            System.out.println("Error en handler: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private void procesarNombre(String line) {
        playerName = line.substring(5).trim();
        sendMessage("WELCOME " + playerName);
    }

    // Guardar varias armas
    private void procesarArmas(String line) {
        String lista = line.substring("SELECT_WEAPONS:".length()).trim();
        this.weapons = Arrays.stream(lista.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(DAMAGE_MAP::containsKey)
                .collect(Collectors.toList());

        sendMessage("Armas seleccionadas: " + String.join(", ", this.weapons));
    }

    private void procesarAtaque(String line) {
        String arma = line.substring("ATTACK:".length()).trim().toLowerCase();
        if (weapons.contains(arma)) {
            atacar(arma);
        } else {
            sendMessage("No tienes equipada el arma: " + arma);
        }
    }

   private void atacar(String arma) {
    if (opponent == null || opponent.hp <= 0 || this.hp <= 0) {
        sendMessage("No puedes atacar ahora.");
        return;
    }

    int damage = calcularDanio(arma);
    opponent.hp -= damage;
    if (opponent.hp < 0) opponent.hp = 0;

    // 📌 Registrar daño en el servidor
    server.GameServer.addDamage(playerName, damage);

    // 📌 Mostrar en consola del servidor con nombres reales
    System.out.println("[ATAQUE] " + playerName + " ataco a" + opponent.playerName +
            " con " + arma + " (-" + damage + " HP). " +
            "HP restante de " + opponent.playerName + ": " + opponent.hp);

    // 📌 Mensajes entre jugadores
    opponent.sendMessage(playerName + " te atacó con " + arma +
            " (-" + damage + " HP, ahora tienes " + opponent.hp + ")");
    sendMessage("Atacaste a " + opponent.playerName + " con " + arma +
            " (-" + damage + " HP, ahora tiene " + opponent.hp + ")");

    // 📌 Verificar si el oponente perdió
    if (opponent.hp == 0) {
        sendMessage("YOU_WIN");
        opponent.sendMessage("YOU_LOSE");

        // Registrar la partida en el servidor (victoria + tiempo real)
        server.GameServer.registerMatch(this.playerName, damage);
    }
}


}