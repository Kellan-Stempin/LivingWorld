import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import creatures.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;


public class World {
    private List<Creature> creatures;
    private List<Food> food;
    private Random random;
    private List<String> creatureNames;

    public World() {
        this.creatures = new ArrayList<>();
        this.food = new ArrayList<>();
        this.random = new Random();
        this.creatureNames = new ArrayList<>();
        loadCreatureNames();
    }

    private void loadCreatureNames() {
        String[] possiblePaths = {
            "src" + File.separator + "names.txt",
            "names.txt",
            "LivingWorld" + File.separator + "src" + File.separator + "names.txt"
        };
        
        boolean fileFound = false;
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (!line.isEmpty()) {
                            creatureNames.add(line);
                        }
                    }
                    fileFound = true;
                    break;
                } catch (IOException e) {
                }
            }
        }
        
        if (!fileFound) {
            for (int i = 1; i < 11; i++) {
                creatureNames.add("C" + i);
            }
        }
    }

    public Creature createCreature() {
        String name = creatureNames.get(random.nextInt(creatureNames.size()));

        Creature newCreature;
        if (random.nextBoolean()) {
            newCreature = new Animal(name);
        } else {
            newCreature = new Monster(name);
        }
        
        // Assign random position (60x25 grid)
        int x = random.nextInt(60);
        int y = random.nextInt(25);
        newCreature.setPosition(x, y);

        creatures.add(newCreature);
        return newCreature;
    }

    public void spawnFood() {
        String[] foodTypes = {"Berry", "Nut", "Leaf", "Seed", "Larry"};
        String type = foodTypes[random.nextInt(foodTypes.length)];
        int nutrition = 1 + random.nextInt(5);
        Food newFood = new Food(type, nutrition);
        
        // Assign random position (60x25 grid)
        int x = random.nextInt(60);
        int y = random.nextInt(25);
        newFood.setPosition(x, y);
        
        food.add(newFood);
    }
    
    public List<Food> getFood() {
        return food;
    }

    public void displayStatus() {
        int aliveCount = 0;

        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                aliveCount++;
            }
        }

        System.out.println("---World Status---");
        System.out.println("Living Creatures: " + aliveCount + " / " + creatures.size());
        System.out.println("Food Items: " + food.size());
    }

    public void displayCreatures() {
        System.out.println("---All Creatures---");
        for (Creature creature : creatures) {
            System.out.println(creature.getName() + ": ");
            System.out.println("Species: " + creature.getSpecies());
            System.out.println("Health: " + creature.getHealth());
            System.out.println("Hunger: " + creature.getHunger());
            System.out.println("Strength: " + creature.getStrength());
            System.out.println("Can Reproduce: " + creature.getReplicationChance());
            System.out.println("Friendliness: " + creature.getFriendliness());
            System.out.println(creature.isAlive() ? "Alive" : "Dead");
        }
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public void addCreature(Creature creature) {
        creatures.add(creature);
    }
    
    // Move creatures slightly each iteration to simulate movement
    public void updatePositions() {
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                int x = creature.getX();
                int y = creature.getY();
                
                // Random small movement (-1 to +1)
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                
                int newX = Math.max(0, Math.min(59, x + dx));
                int newY = Math.max(0, Math.min(24, y + dy));
                
                creature.setPosition(newX, newY);
            }
        }
    }
}
