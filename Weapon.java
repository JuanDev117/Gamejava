//clase weapon que reprenta un arma
public class Weapon {
    // se declaran las variables
    private String name;
    private int damage;

    // se crea un contructor y se inicializan
    public Weapon(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }
    // para que lea el nombre y lo retorne
    public String getName() {
        return name;
    }
    // para que lea el valor y lo retorne
    public int getDamage() {
        return damage;
    }
}
    

