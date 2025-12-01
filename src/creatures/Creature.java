package creatures;
import java.util.Random;

public abstract class Creature implements Attack {
    private String name;
    private String species;
    private String[] speciesList = {"Animal", "Monster"};
    private int health;
    private int hunger;
    private int strength;
    private boolean replicationChance;
    private int friendliness;
    private boolean alive;
    private Random random;
    private int x;
    private int y;


    public Creature(String name) {
        this.random = new Random();
        this.name = name;
        this.health = 100;
        this.hunger = 0;
        this.strength = this.random.nextInt(0, 100);
        this.replicationChance = this.random.nextBoolean(); //can the creature replicate
        this.friendliness = this.random.nextInt(0, 100);
        this.alive = true;
    }

    protected void setSpeciesFromList(int index) {
        if (index >= 0 && index < speciesList.length) {
            this.species = speciesList[index];
        }
    }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() { return health; }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHunger() { return hunger; }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getStrength() { return strength; }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public boolean getReplicationChance() { return replicationChance; }

    public boolean isAlive() { return alive; }

    public int getFriendliness() { return friendliness; }

    public void setFriendliness(int friendliness) {
        this.friendliness = friendliness;
    }

    public String getSpecies() { return species; }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void die() {
        this.health = 0;
        this.alive = false;
    }

    public void eat(int nutrition) {
        if (alive) {
            this.hunger += nutrition;
        }
    }

    public void takeDamage(int damage) {
        if (alive) {
            this.health -= damage;
            if (this.health <= 0) {
                this.health = 0;
                this.alive = false;
            }
        }
    }

    public void attack(Creature target) {
        if (alive && target != null && target.isAlive()) {
            int damage = this.strength;
            target.takeDamage(damage);
        }
    }

    public abstract Creature reproduce();
}
