
// Representa al jugador en la arena
public class Player {
    // Vida (hit points) del jugador
    private int hp;
    // nombre propio del jugador
    private String name;

    // Constructor: inicializa la vida del jugador
    public Player(String name,int hp) {
        this.hp = hp; // guardar el valor inicial
        this.name = name; // inicializar y guardar el nombre
    }
     // para que lea el nombre y lo retorne
     public String getName() {
        return name;
    }

    // Método sincronizado para que múltiples hilos no modifiquen hp al mismo tiempo
    public synchronized void takeDamage(int amount, String attacker) {
        hp -= amount; // resta la vida del jugador
        if (hp < 0) hp = 0; // evita vida negativa
        System.out.println(attacker + " ataca a " + name + " y hace " + amount + " de daño.");
        System.out.println("-> Vida de " + name + ": " + hp);
    }

    // Consultar si el jugador sigue vivo
    public synchronized boolean isAlive() {
        return hp > 0; // devuelve true si hp es mayor a 0
    }
    // orgasniza la fluides de los mesajes al ejecutar
    public synchronized void print(String msg) {
    System.out.println(msg);// muestra los mensajes segun so orden
}


    // Método para obtener la vida restante (no modificador)
    public synchronized int getHp() {
        return hp;
    }

}

