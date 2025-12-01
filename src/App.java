import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import creatures.Creature;

public class App {
    public static void main(String[] args) throws Exception {
        World world = new World();
        WorldVisualizer visualizer = new WorldVisualizer();
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);
        
        // Show instructions on first run
        visualizer.showInstructions();
        System.out.print("Press Enter to start the simulation...");
        scanner.nextLine();
        
        System.out.println("Creating World: ");
        for (int i = 0; i < 5; i++) {
            world.createCreature();
        }
        
        // Get number of days from user
        System.out.print("\nEnter number of days to simulate (or press Enter for continuous mode): ");
        String daysInput = scanner.nextLine().trim();
        
        int totalTicks = -1; // -1 means continuous mode
        boolean continuousMode = false;
        
        if (daysInput.isEmpty()) {
            continuousMode = true;
            System.out.println("Running in continuous mode. Press Ctrl+C to stop.");
        } else {
            try {
                int days = Integer.parseInt(daysInput);
                if (days <= 0) {
                    System.out.println("Invalid input. Using continuous mode instead.");
                    continuousMode = true;
                } else {
                    totalTicks = days * 24; // Each day = 24 ticks
                    System.out.println("Simulating " + days + " day(s) (" + totalTicks + " ticks)...");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Using continuous mode instead.");
                continuousMode = true;
            }
        }
        
        System.out.print("Press Enter to start...");
        scanner.nextLine();
        
        double encounterChance = 0.30;
        double spawnChance = 0.20;
        double foodSpawnChance = 0.30;
        
        int tick = 0;
        boolean running = true;
        List<String> events = new ArrayList<>(); // Track events for this tick
        
        // Add shutdown hook for graceful Ctrl+C handling
        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nSimulation interrupted. Showing final state...");
            world.displayCreatures();
        }));
        
        while (running) {
            tick++;
            events.clear(); // Clear events for new tick
            
            // Calculate current day and tick within day
            int currentDay = (tick - 1) / 24 + 1;
            int tickInDay = ((tick - 1) % 24) + 1;
            
            // Update creature positions (simulate movement)
            world.updatePositions();
            
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
                
                events.add("ENCOUNTER: " + creature1.getName() + " meets " + creature2.getName() + "!");
                
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
            
            for (Creature creature : currentCreatures) {
                if (creature.isAlive()) {
                    Creature offspring = creature.reproduce();
                    if (offspring != null) {
                        // Assign position near parent
                        int parentX = creature.getX();
                        int parentY = creature.getY();
                        int offsetX = random.nextInt(5) - 2; // -2 to +2
                        int offsetY = random.nextInt(5) - 2;
                        int newX = Math.max(0, Math.min(59, parentX + offsetX));
                        int newY = Math.max(0, Math.min(24, parentY + offsetY));
                        offspring.setPosition(newX, newY);
                        
                        newCreatures.add(offspring);
                        events.add(creature.getName() + " reproduced! New: " + offspring.getName());
                    }
                }
            }
            
            for (Creature newCreature : newCreatures) {
                world.addCreature(newCreature);
            }
            
            if (random.nextDouble() < spawnChance) {
                Creature newCreature = world.createCreature();
                events.add("New creature spawned: " + newCreature.getName());
            }

            if (random.nextDouble() < foodSpawnChance) {
                world.spawnFood();
                events.add("Food spawned in the world!");
            }
            
            // Only visualize if something changed (visualize method returns true if it rendered)
            visualizer.visualize(world, currentDay, tickInDay, tick, events);
            
            // Check if we should continue
            if (!continuousMode && tick >= totalTicks) {
                running = false;
            } else if (continuousMode) {
                // In continuous mode, small delay between ticks
                try {
                    Thread.sleep(500); // 500ms delay between ticks
                } catch (InterruptedException e) {
                    running = false;
                    System.out.println("\nSimulation stopped by user.");
                }
            } else {
                // Manual step mode for non-continuous
                System.out.print("Press Enter to continue to next tick (or 'q' to quit)...");
                String input = scanner.nextLine().trim();
                if (input.equalsIgnoreCase("q")) {
                    running = false;
                }
            }
        }

        System.out.println("\n---Final State---");
        world.displayCreatures();
        scanner.close();
    }
}
