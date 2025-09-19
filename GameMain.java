// Clase principal para ejecutar la simulación
public class GameMain {
    
    public static void main(String[] args) {
        System.out.println("=== Arena de Guerreros (Unidad 1: Concurrencia) ===");

        // 1) Crear el jugador con 100 puntos de vida
      Player p1 = new Player("Espectro", 100);
      Player p2 = new Player("Goblin", 100);
      Player p3 = new Player("Goku", 100);
      Player p4 = new Player("Demon", 100);
      Player p5 = new Player("Meke", 100);

       // crea las aramas y indica el damage de cada una
        Weapon pistol = new Weapon("Pistola", 15);
        Weapon sword = new Weapon("Espada", 10);
        Weapon laser = new Weapon("Láser", 20);
        Weapon axe = new Weapon("Hacha", 12);
        Weapon bow = new Weapon("Arco", 18);

        // Crear enemigos con armas 
        Enemy e1 = new Enemy(p1, "fantasma", pistol, 5);
        Enemy e2 = new Enemy(p2, "bestia", sword, 7);
        Enemy e3 = new Enemy(p3, "vegeta", laser, 9);
        Enemy e4 = new Enemy(p4, "angel", axe, 4);
        Enemy e5 = new Enemy(p5, "klim", bow, 7);

        // 3) Iniciar los hilos (cada enemy corre en su propio hilo)
        e1.start();
        e2.start();
        e3.start();
        e4.start();
        e5.start();

        // 4) Esperar a que terminen (join) para luego mostrar el resultado final
        try {
            e1.join();
            e2.join();
            e3.join();
            e4.join();
            e5.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 5) Mostrar resultado
       System.out.println("=== Resultados Finales ===");
       System.out.println("Jugador Espectro: " + (p1.isAlive() ? "Vivo con " + p1.getHp() + " hp" : "Derrotado"));
       System.out.println("Jugador Goblin: " + (p2.isAlive() ? "Vivo con " + p2.getHp() + " hp" : "Derrotado"));
       System.out.println("Jugador goku: " + (p3.isAlive() ? "Vivo con " + p3.getHp() + " hp" : "Derrotado"));
       System.out.println("Jugador demon: " + (p4.isAlive() ? "Vivo con " + p4.getHp() + " hp" : "Derrotado"));
       System.out.println("Jugador meke: " + (p5.isAlive() ? "Vivo con " + p5.getHp() + " hp" : "Derrotado"));

        System.out.println("Fin de la Unidad 1.");
    }
}                          