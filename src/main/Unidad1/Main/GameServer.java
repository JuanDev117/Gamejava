package Main;

import java.io.*;
import java.net.*;
import java.util.*;

// Servidor simple que empareja a dos jugadores por batalla
public class GameServer {
    private static final List<ClientHandler> waiting = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        int port = 5000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado en puerto " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nuevo cliente conectado: " + clientSocket.getRemoteSocketAddress());

            ClientHandler handler = new ClientHandler(clientSocket);
            handler.start();

            synchronized (waiting) {
                waiting.add(handler);
                if (waiting.size() >= 2) {
                    ClientHandler a = waiting.remove(0);
                    ClientHandler b = waiting.remove(0);
                    a.setOpponent(b);
                    b.setOpponent(a);
                   a.sendMessage("MATCH_START contra " + b.getPlayerName());
                    b.sendMessage("MATCH_START contra " + a.getPlayerName());
                }
            }
        }
    }
}