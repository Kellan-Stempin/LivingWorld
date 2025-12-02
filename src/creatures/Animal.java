package creatures;

public class Animal extends Creature {

    public Animal(String name) {
        super(name);
        setSpeciesFromList(0);
    }

    @Override
    public Creature reproduce() {
        if (!isAlive()) {
            return null;
        }

        java.util.Random random = new java.util.Random();
        int randRoll = random.nextInt(0, 100);
        if (randRoll <= this.replicationChance) {
            return new Animal(getName() + " Jr.");
        }
        return null;

    }

    @Override
    public String getType() {
        return "Animal";
    }
}
    
        