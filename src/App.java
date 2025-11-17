import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import creatures.Creature;

public class App {
    public static void main(String[] args) throws Exception {
        World world = new World();
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Creating World: ");
        for (int i = 0; i < 5; i++) {
            world.createCreature();
        }
        world.displayStatus();
        System.out.println();
        
        int iterations = 10;   
        double encounterChance = 0.30;
        double spawnChance = 0.20;
        double foodSpawnChance = 0.30;
        
        for (int iteration = 1; iteration <= iterations; iteration++) {
            System.out.println("---Iteration " + iteration + "---");
            
            List<Creature> currentCreatures = new ArrayList<>(world.getCreatures());
            List<Creature> newCreatures = new ArrayList<>();

            List<Creature> aliveCreatures = new ArrayList<>();
            for (Creature c : currentCreatures) {
                if (c.isAlive()) {
                    aliveCreatures.add(c);
                }
            }
            
            if (aliveCreatures.size() >= 2 && random.nextDouble() < encounterChance) {
                Creature creature1 = aliveCreatures.get(random.nextInt(aliveCreatures.size()));
                Creature creature2;
                do {
                    creature2 = aliveCreatures.get(random.nextInt(aliveCreatures.size()));
                } while (creature1 == creature2);
                
                System.out.println("ENCOUNTER: " + creature1.getName() + " meets " + creature2.getName() + "!");
                System.out.println("  A fight to the death begins!");
                
                while (creature1.isAlive() && creature2.isAlive()) {
                    creature1.attack(creature2);
                    if (creature2.isAlive()) {
                        creature2.attack(creature1);
                    }
                }
                
                if (!creature1.isAlive() && !creature2.isAlive()) {
                    System.out.println("  Both " + creature1.getName() + " and " + creature2.getName() + " have been defeated!");
                } else if (!creature1.isAlive()) {
                    System.out.println("  " + creature1.getName() + " has been defeated! " + creature2.getName() + " wins!");
                } else {
                    System.out.println("  " + creature2.getName() + " has been defeated! " + creature1.getName() + " wins!");
                }
            }
            
            for (Creature creature : currentCreatures) {
                if (creature.isAlive()) {
                    Creature offspring = creature.reproduce();
                    if (offspring != null) {
                        newCreatures.add(offspring);
                        System.out.println(creature.getName() + " reproduced! New creature: " + offspring.getName());
                    }
                }
            }
            
            for (Creature newCreature : newCreatures) {
                world.addCreature(newCreature);
            }
            
            if (random.nextDouble() < spawnChance) {
                Creature newCreature = world.createCreature();
                System.out.println("A new creature spawned: " + newCreature.getName());
            }

            if (random.nextDouble() < foodSpawnChance) {
                world.spawnFood();
                System.out.println("Food has spawned in the world!");
            }

            world.displayStatus();
            System.out.println();
            
            if (iteration < iterations) {
                System.out.print("Press Enter to continue to next iteration...");
                scanner.nextLine();
            }
        }

        System.out.println("---Final State---");
        world.displayCreatures();
        scanner.close();
    }
}
