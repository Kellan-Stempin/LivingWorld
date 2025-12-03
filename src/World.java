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
    
    // Board dimensions and max capacity
    private static final int BOARD_WIDTH = 60;
    private static final int BOARD_HEIGHT = 25;
    private static final int MAX_CREATURES = 1500; // Max creatures that can fit on board 
    private static final int MAX_FOOD = 50; // Max food items

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
        // Check if we're at max capacity
        int aliveCount = getAliveCreatureCount();
        if (aliveCount >= MAX_CREATURES) {
            return null; // Can't spawn more creatures
        }
        
        String name = creatureNames.get(random.nextInt(creatureNames.size()));

        Creature newCreature;
        if (random.nextBoolean()) {
            newCreature = new Animal(name);
        } else {
            newCreature = new Monster(name);
        }
        
        // Assign random position on grid
        int x = random.nextInt(BOARD_WIDTH);
        int y = random.nextInt(BOARD_HEIGHT);
        newCreature.setPosition(x, y);

        creatures.add(newCreature);
        return newCreature;
    }
    
    private int getAliveCreatureCount() {
        int count = 0;
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                count++;
            }
        }
        return count;
    }

    public void spawnFood() {
        // Check if we're at max food capacity
        int activeFoodCount = 0;
        for (Food f : food) {
            if (!f.isConsumed()) {
                activeFoodCount++;
            }
        }
        
        if (activeFoodCount >= MAX_FOOD) {
            return; // Can't spawn more food
        }
        
        String[] foodTypes = {"Berry", "Nut", "Leaf", "Seed", "Larry"};
        String type = foodTypes[random.nextInt(foodTypes.length)];
        int nutrition = 1 + random.nextInt(5);
        Food newFood = new Food(type, nutrition);
        
        // Assign food to random position 
        int x = random.nextInt(BOARD_WIDTH);
        int y = random.nextInt(BOARD_HEIGHT);
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

    public boolean addCreature(Creature creature) {
        // Check if we're at max capacity before adding
        int aliveCount = getAliveCreatureCount();
        if (aliveCount >= MAX_CREATURES) {
            return false; 
        }
        creatures.add(creature);
        return true;
    }
    
    public void updatePositions() {
        for (Creature creature : creatures) {
            if (creature.isAlive()) {
                int x = creature.getX();
                int y = creature.getY();
                
                // Random small movement (-1 to +1)
                int dx = random.nextInt(3) - 1;
                int dy = random.nextInt(3) - 1;
                
                int newX = Math.max(0, Math.min(BOARD_WIDTH - 1, x + dx));
                int newY = Math.max(0, Math.min(BOARD_HEIGHT - 1, y + dy));
                
                creature.setPosition(newX, newY);
            }
        }
    }
    
    public static int getMaxCreatures() {
        return MAX_CREATURES;
    }
}
