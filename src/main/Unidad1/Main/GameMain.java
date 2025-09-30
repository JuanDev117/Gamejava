package Main;

import java.util.ArrayList;
import java.util.List;

public class GameMain {

    public static void main(String[] args) {
        System.out.println("=== Arena de Guerreros (Unidad 1: Concurrencia) ===");
        List<Player> players = new ArrayList<>();
        players.add(new Player("Niño de 14 años", 60));
        players.add(new Player("Militar", 200));
        players.add(new Player("Policia", 200));
        players.add(new Player("Gustavo", 200));
        players.add(new Player("Poncho", 200));

        //armas
        Weapon pistola = new Weapon("Pistola", 20);
        Weapon Teaser = new Weapon("Teaser", 10);
        Weapon Fusil = new Weapon("Fusil ", 25);
        Weapon Garrote = new Weapon("Garrote", 17);
        Weapon Peñon= new Weapon("Peñon", 22);

        //lista de enemigos y asignarlos a jugadores y armas
        List<Enemy> enemies = new ArrayList<>();
        enemies.add(new Enemy(players.get(0), "Asaltante", pistola, 5));
        enemies.add(new Enemy(players.get(1), "Loco", Teaser, 7));
        enemies.add(new Enemy(players.get(2), "Drogadicto ", Fusil, 9));
        enemies.add(new Enemy(players.get(3), "Canival", Garrote, 4));
        enemies.add(new Enemy(players.get(4), "Chirrete", Peñon, 7));

       // los hilos de todos los enemigos
        for (Enemy e : enemies) {
            e.start();
        }

        //esperar q todos los hilos usen el for
        for (Enemy enemy : enemies) {
            try {
                enemy.join();   //join hace que el hilo principal (el main ) espere a que cada hilo enemigo termine
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        //resultados
        System.out.println("\n---------- Resultados Finales ----------");
        for (Player p : players) {
            System.out.println("Jugador " + p.getName() + ": " + (p.isAlive() ? "Vivo con " + p.getHp() + " hp" : "Derrotado"));
        }

        System.out.println("Fin de la Unidad 1.");
    }
}
class Player {
    private int hp;
    private String name;

    public Player(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    public String getName() {
        return name;
    }
    public synchronized void takeDamage(int amount, String attacker) {
        hp -= amount;
        if (hp < 0) {
            hp = 0;
        }
        System.out.println(attacker + " ataca a " + this.name + " y hace " + amount + " de daño.");
        System.out.println("-----> Vida de " + this.name + ": " + hp);
    }

    // Consultar si el jugador sigue vivo de manera segura
    public synchronized boolean isAlive() {
        return hp > 0;
    }


    public synchronized int getHp() {
        return hp;
    }
}

// Clase que representa un arma
class Weapon {
    private String name;
    private int damage;

    public Weapon(String name, int damage) {
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

// Clase que representa un enemigo (hilo )
class Enemy extends Thread {
    private Player player;
    private String name;
    private Weapon weapon;
    private int attacks;

    public Enemy(Player player, String name, Weapon weapon, int attacks) {
        this.player = player;
        this.name = name;
        this.weapon = weapon;
        this.attacks = attacks;
    }

    @Override
    public void run() {
        System.out.println(name + " empieza a dar con" + weapon.getName());
        for (int i = 0; i < attacks; i++) {
            if (!player.isAlive()) {
                System.out.println(name + " ve que el jugador ya esta frio y se queda quieto ");
                break;
            }

            player.takeDamage(weapon.getDamage(), name);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(name + " fue interrumpido.");
            }
        }
        System.out.println(name + " ya termino sus ataques.");
    }
}



/////hasta este momento este proyecto de el juego antes de la programacion fucional