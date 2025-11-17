package creatures;

public class Animal extends Creature {

    public Animal(String name) {
        super(name);
        setSpeciesFromList(0);
    }

    @Override
    public Creature reproduce() {
        if (!isAlive() || !getReplicationChance()) {
            return null;
        }
        java.util.Random random = new java.util.Random();
        double reproductionRoll = random.nextDouble();
        double reproductionChance = 0.30;
        
        if (reproductionRoll < reproductionChance) {
            return new Animal(getName() + " Jr.");
        }
        
        return null;
    }
}
    
        