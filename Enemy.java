// Un enemigo que ataca al jugador periódicamente
public class Enemy extends Thread {
    private Player player;     // referencia al jugador que atacará
    private String name;       // nombre del enemigo
   
    private Weapon weapon;     // armas que utilizaran
    private int attacks;       // número de ataques que hará

    // Constructor: recibe a quién atacar, nombre, daño con armas y cuántas veces atacará
    public Enemy(Player player, String name, Weapon weapon, int attacks) {
        this.player = player;
        this.name = name;
        this.weapon = weapon;
        this.attacks = attacks;

    }

    // run() es lo que se ejecuta cuando se llama a start()
    @Override
    public void run() {
        System.out.println(name + " empieza a atacar con " + weapon.getName());
        for (int i = 0; i < attacks; i++) {
            // Antes de atacar, chequeamos si el jugador aún vive
            if (!player.isAlive()) {
                System.out.println(name + " ve que el jugador ya está muerto y deja de atacar.");
                break; // salimos del ciclo si el jugador está muerto
            }

            // Atacar: usamos el método sincronizado takeDamage
           player.takeDamage(weapon.getDamage(), name);

            try {
                // Dormimos el hilo para simular tiempo entre ataques
                Thread.sleep(500); // 500 ms
            } catch (InterruptedException e) {
                // Si el hilo fue interrumpido, restablecemos la bandera de interrupción
                Thread.currentThread().interrupt();
                System.out.println(name + " fue interrumpido.");
            }
        }

        System.out.println(name + " terminó sus ataques.");
    }
}
