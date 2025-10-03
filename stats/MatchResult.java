package stats;

public class MatchResult {
    private String playerName;
    private int totalDamage;
    private int victories;
    private long duration; // en segundos

    public MatchResult(String playerName, int totalDamage, long duration) {
        this.playerName = playerName;
        this.totalDamage = totalDamage;
        this.duration = duration;
        this.victories = 0; // al iniciar no tiene victorias
    }
    public void addDamage(int dmg) {
        this.totalDamage += dmg;
    }

    public void addVictory() {
        this.victories++;
    }

    public void setDuration(long d) {
        this.duration = d;
    }

    
    public String getPlayerName() {
        return playerName;
    }

    public int getTotalDamage() {
        return totalDamage;
    }

    public int getVictories() {
        return victories;
    }

    public long getDuration() {
        return duration;
    }
}