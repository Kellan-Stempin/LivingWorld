package creatures;

public class Monster extends Creature {

    public Monster(String name) {
        super(name);
        setSpeciesFromList(1);
    }

    @Override
    public Creature reproduce() {
        if (!isAlive()) {
            return null;
        }
        
        java.util.Random random = new java.util.Random();
        int randRoll = random.nextInt(0, 100);
        if (randRoll <= this.replicationChance) {
            return new Monster(getName() + " Jr.");
        }
        return null;

    }
}