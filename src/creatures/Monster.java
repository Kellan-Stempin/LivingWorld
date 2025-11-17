package creatures;

public class Monster extends Creature {

    public Monster(String name) {
        super(name);
        setSpeciesFromList(1);
    }

    @Override
    public Creature reproduce() {
        if (!isAlive() || !getReplicationChance()) {
            return null;
        }
        java.util.Random random = new java.util.Random();
        double reproductionRoll = random.nextDouble();
        double reproductionChance = 0.20;
        
        if (reproductionRoll < reproductionChance) {
            return new Monster(getName() + " Jr.");
        }
        
        return null;
    }
}