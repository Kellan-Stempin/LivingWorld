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
        System.out.print("\nPress Enter to start the simulation...");
        scanner.nextLine();
        
        System.out.println("\nCreating World... ");
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
        double spawnChance = 0.20;
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
            
            if (aliveCreatures.size() >= 2 && random.nextDouble() < encounterChance) {
                Creature creature1 = aliveCreatures.get(random.nextInt(aliveCreatures.size()));
                Creature creature2;
                do {
                    creature2 = aliveCreatures.get(random.nextInt(aliveCreatures.size()));
                } while (creature1 == creature2);
                
                events.add("ENCOUNTER: " + creature1.getName() + " meets " + creature2.getName() + "!");

                // add reproduction here? 
                if (creature1.getType() == creature2.getType()) {
                    Creature offspring = creature1.reproduce();
                    if (offspring != null) {
                        int parentX = creature1.getX();
                        int parentY = creature1.getY();
                        int offsetX = random.nextInt(5) - 2;
                        int offsetY = random.nextInt(5) - 2;
                        int newX = Math.max(0, Math.min(59, parentX + offsetX)); // 60 width (0-59)
                        int newY = Math.max(0, Math.min(24, parentY + offsetY)); // 25 height (0-24)
                        offspring.setPosition(newX, newY);
                        
                        newCreatures.add(offspring);
                    }
                }
                
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
            
            // for (Creature creature : currentCreatures) {
            //     if (creature.isAlive()) {
            //         Creature offspring = creature.reproduce();
            //         if (offspring != null) {
            //             int parentX = creature.getX();
            //             int parentY = creature.getY();
            //             int offsetX = random.nextInt(5) - 2;
            //             int offsetY = random.nextInt(5) - 2;
            //             int newX = Math.max(0, Math.min(59, parentX + offsetX)); // 60 width (0-59)
            //             int newY = Math.max(0, Math.min(24, parentY + offsetY)); // 25 height (0-24)
            //             offspring.setPosition(newX, newY);
                        
            //             newCreatures.add(offspring);
            //         }
            //     }
            // }
            
            // Add new creatures (only if under capacity limit)
            for (Creature newCreature : newCreatures) {
                if (world.addCreature(newCreature)) {
                    events.add(newCreature.getName() + " was born!");
                }
                // If addCreature returns false, creature wasn't added (at capacity)
            }
            
            if (random.nextDouble() < spawnChance) {
                Creature newCreature = world.createCreature();
                if (newCreature != null) {
                    events.add("New creature spawned: " + newCreature.getName());
                }
                // If null, we're at max capacity
            }

            if (random.nextDouble() < foodSpawnChance) {
                world.spawnFood();
                events.add("Food spawned in the world!");
            }

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
}
