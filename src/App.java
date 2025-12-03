import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import creatures.Creature;
import creatures.Animal;
import creatures.Monster;

public class App {
    public static void main(String[] args) throws Exception {
        World world = new World();
        WorldVisualizer visualizer = new WorldVisualizer();
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nPress Enter to start the simulation...");
        scanner.nextLine();
        
        System.out.println("\nCreating World... ");
        for (int i = 0; i < 10; i++) {
            world.createCreature();
        }
        
        // Get number of days from user
        System.out.print("\nEnter number of days to simulate (or press Enter for continuous mode): ");
        String daysInput = scanner.nextLine().trim();
        
        int totalTicks = -1; // -1 means continuous mode
        boolean continuousMode = false;
        
        if (daysInput.isEmpty()) {
            continuousMode = true;
            System.out.println("\nRunning in continuous mode. Press Ctrl+C to stop.");
        } else {
            try {
                int days = Integer.parseInt(daysInput);
                if (days <= 0) {
                    System.out.println("\nInvalid input. Using continuous mode instead.");
                    continuousMode = true;
                } else {
                    totalTicks = days * 24; // Each day = 24 ticks
                    System.out.println("\nSimulating " + days + " day(s) (" + totalTicks + " ticks)...");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Using continuous mode instead.");
                continuousMode = true;
            }
        }
        
        System.out.print("Press Enter to start...");
        scanner.nextLine();
        
        double encounterChance = 0.30;
        double spawnChance = 0.0; // Disabled - creatures only come from reproduction now
        double foodSpawnChance = 0.30;
        
        int tick = 0;
        boolean running = true;
        List<String> events = new ArrayList<>();
        
        while (running) {
            tick++;
            events.clear();

            int currentDay = (tick - 1) / 24 + 1;
            int tickInDay = ((tick - 1) % 24) + 1;

            world.updatePositions();
            
            List<Creature> currentCreatures = new ArrayList<>(world.getCreatures());
            List<Creature> newCreatures = new ArrayList<>();

            List<Creature> aliveCreatures = new ArrayList<>();
            for (Creature c : currentCreatures) {
                if (c.isAlive()) {
                    aliveCreatures.add(c);
                }
            }
            
            // Check for encounters based on proximity (creatures touching or very close)
            List<Creature[]> nearbyPairs = findNearbyCreatures(aliveCreatures, 3); // 3 = up to 3 cells away (more encounters)
            
            if (!nearbyPairs.isEmpty() && random.nextDouble() < encounterChance) {
                // Pick a random pair of nearby creatures
                Creature[] pair = nearbyPairs.get(random.nextInt(nearbyPairs.size()));
                Creature creature1 = pair[0];
                Creature creature2 = pair[1];
                
                // Check if same type (both Animal or both Monster)
                boolean sameType = creature1.getType().equals(creature2.getType());
                
                if (sameType) {
                    // Same type: reproduction only (no attack) - requires BOTH creatures
                    events.add("ENCOUNTER: " + creature1.getName() + " (" + creature1.getType() + 
                              ") meets " + creature2.getName() + " (" + creature2.getType() + ")!");
                    
                    // Reproduction requires BOTH creatures to be present
                    // Try reproduction - both must be able to reproduce for it to work
                    // Use a combined chance or require both to succeed
                    Random rand = new Random();
                    double reproductionChance = 0.5; // 50% chance when two same-type creatures meet
                    
                    if (rand.nextDouble() < reproductionChance) {
                        // Create offspring based on the type (requires BOTH parents)
                        // Generate a simple name to avoid exponential name growth
                        String offspringName = generateOffspringName(creature1.getName(), creature2.getName());
                        
                        Creature offspring;
                        if (creature1.getType().equals("Animal")) {
                            offspring = new Animal(offspringName);
                        } else {
                            offspring = new Monster(offspringName);
                        }
                        
                        // Position offspring - can be on same position as parents or nearby
                        int newX, newY;
                        double positionRoll = rand.nextDouble();
                        if (positionRoll < 0.33) {
                            // 33% chance: same position as parent1
                            newX = creature1.getX();
                            newY = creature1.getY();
                        } else if (positionRoll < 0.66) {
                            // 33% chance: same position as parent2
                            newX = creature2.getX();
                            newY = creature2.getY();
                        } else {
                            // 34% chance: nearby (between parents or adjacent)
                            int parentX = (creature1.getX() + creature2.getX()) / 2;
                            int parentY = (creature1.getY() + creature2.getY()) / 2;
                            int offsetX = rand.nextInt(3) - 1; // -1 to +1
                            int offsetY = rand.nextInt(3) - 1;
                            newX = Math.max(0, Math.min(59, parentX + offsetX));
                            newY = Math.max(0, Math.min(24, parentY + offsetY));
                        }
                        offspring.setPosition(newX, newY);
                        
                        newCreatures.add(offspring);
                        events.add("  " + creature1.getType() + "s reproduced! New: " + offspring.getName());
                    } else {
                        events.add("  No reproduction occurred.");
                    }
                } else {
                    // Different types: attack only (no reproduction)
                    events.add("ENCOUNTER: " + creature1.getName() + " (" + creature1.getType() + 
                              ") meets " + creature2.getName() + " (" + creature2.getType() + ")!");
                    events.add("  A fight to the death begins!");
                    
                    while (creature1.isAlive() && creature2.isAlive()) {
                        creature1.attack(creature2);
                        if (creature2.isAlive()) {
                            creature2.attack(creature1);
                        }
                    }
                    
                    if (!creature1.isAlive() && !creature2.isAlive()) {
                        events.add("  Both " + creature1.getName() + " and " + creature2.getName() + " defeated!");
                    } else if (!creature1.isAlive()) {
                        events.add("  " + creature1.getName() + " defeated! " + creature2.getName() + " wins!");
                    } else {
                        events.add("  " + creature2.getName() + " defeated! " + creature1.getName() + " wins!");
                    }
                }
            }
            
            // Add new creatures (only if under capacity limit)
            for (Creature newCreature : newCreatures) {
                if (world.addCreature(newCreature)) {
                    events.add(newCreature.getName() + " was born!");
                }
                // If addCreature returns false, creature wasn't added (at capacity)
            }
            
            // Check for food consumption (creatures near food)
            List<Food> foodList = world.getFood();
            for (Creature creature : aliveCreatures) {
                for (Food food : foodList) {
                    if (!food.isConsumed()) {
                        int creatureX = creature.getX();
                        int creatureY = creature.getY();
                        int foodX = food.getX();
                        int foodY = food.getY();
                        
                        // Calculate distance (Manhattan distance)
                        int distance = Math.abs(creatureX - foodX) + Math.abs(creatureY - foodY);
                        
                        // If creature is within 2 cells of food, consume it
                        if (distance <= 2) {
                            int nutrition = food.consume();
                            if (nutrition > 0) {
                                creature.heal(10); // Increase health by 10
                                events.add(creature.getName() + " ate " + food.getType() + "! (+10 health)");
                                break; // Creature can only eat one food per tick
                            }
                        }
                    }
                }
            }
            
            // Random spawning disabled - creatures only come from reproduction
            // This ensures all creatures have parents and the simulation is more realistic
            // if (random.nextDouble() < spawnChance) {
            //     Creature newCreature = world.createCreature();
            //     if (newCreature != null) {
            //         events.add("New creature spawned: " + newCreature.getName());
            //     }
            // }

            if (random.nextDouble() < foodSpawnChance) {
                world.spawnFood();
                events.add("Food spawned in the world!");
            }

            // Always visualize to show current state
            visualizer.visualize(world, currentDay, tickInDay, tick, events);

            if (!continuousMode && tick >= totalTicks) {
                running = false;
            } else if (continuousMode) {
                try {
                    Thread.sleep(1000); // delay
                } catch (InterruptedException e) {
                    running = false;
                    System.out.println("\nSimulation stopped by user.");
                }
            } else {
                System.out.print("Press Enter to continue to next tick (or 'q' to quit)...");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) {
                    running = false;
                }
            }
        }

        System.out.println("\nSimulation ended.");
        scanner.close();
    }
    
    /**
     * Find pairs of creatures that are near each other on the board
     * @param creatures List of alive creatures
     * @param maxDistance Maximum distance (in cells) for creatures to be considered "nearby"
     * @return List of creature pairs that are within maxDistance of each other
     */
    private static List<Creature[]> findNearbyCreatures(List<Creature> creatures, int maxDistance) {
        List<Creature[]> nearbyPairs = new ArrayList<>();
        
        for (int i = 0; i < creatures.size(); i++) {
            Creature c1 = creatures.get(i);
            if (!c1.isAlive()) continue;
            
            int x1 = c1.getX();
            int y1 = c1.getY();
            
            for (int j = i + 1; j < creatures.size(); j++) {
                Creature c2 = creatures.get(j);
                if (!c2.isAlive()) continue;
                
                int x2 = c2.getX();
                int y2 = c2.getY();
                
                // Calculate Manhattan distance (sum of horizontal and vertical distance)
                int manhattanDistance = Math.abs(x1 - x2) + Math.abs(y1 - y2);
                
                // Also check if they're on the same cell (touching directly)
                boolean sameCell = (x1 == x2 && y1 == y2);
                
                // Check if within maxDistance OR on same cell
                if (manhattanDistance <= maxDistance || sameCell) {
                    nearbyPairs.add(new Creature[]{c1, c2});
                }
            }
        }
        
        return nearbyPairs;
    }
    
    /**
     * Generate a simple name for offspring by appending " Jr." to parent's name
     */
    private static String generateOffspringName(String parent1Name, String parent2Name) {
        // Simply append " Jr." to the first parent's name
        // This will create: "Draco", "Draco Jr.", "Draco Jr. Jr.", etc.
        return parent1Name + " Jr.";
    }
}
