import java.util.List;
import java.util.ArrayList;
import creatures.Creature;

/**
 * WorldVisualizer - Creates detailed ASCII art visualization of the Living World
 * 
 * TIP: For best results, reduce your terminal font size:
 * - Windows Terminal: Settings > Appearance > Font size (try 8-10pt)
 * - PowerShell: Right-click title bar > Properties > Font (try 8-10pt)
 * - VS Code Terminal: Settings > Terminal > Font Size (try 10-12px)
 */
public class WorldVisualizer {
    private static final int GRID_WIDTH = 60;
    private static final int GRID_HEIGHT = 25;
    
    // ANSI Color Codes
    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";
    
    // Unicode Box Drawing Characters
    private static final String TOP_LEFT = "┌";
    private static final String TOP_RIGHT = "┐";
    private static final String BOTTOM_LEFT = "└";
    
    private static final String BOTTOM_RIGHT = "┘";
    private static final String HORIZONTAL = "─";
    private static final String VERTICAL = "│";
    private static final String CROSS = "┼";
    private static final String T_UP = "┴";
    private static final String T_DOWN = "┬";
    private static final String T_LEFT = "┤";
    private static final String T_RIGHT = "├";
    
    private char[][] grid;
    private String[][] creatureNames;
    private int[][] creatureHealth;
    
    // Track previous state to detect changes
    private int previousAliveCount = -1;
    private int previousTotalCreatures = -1;
    private int previousFoodCount = -1;
    private List<String> previousCreatureStates; // Store creature name + health for comparison
    
    public WorldVisualizer() {
        this.grid = new char[GRID_HEIGHT][GRID_WIDTH];
        this.creatureNames = new String[GRID_HEIGHT][GRID_WIDTH];
        this.creatureHealth = new int[GRID_HEIGHT][GRID_WIDTH];
        this.previousCreatureStates = new ArrayList<>();
        clearGrid();
    }
    
    private void clearGrid() {
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = ' ';
                creatureNames[y][x] = null;
                creatureHealth[y][x] = 0;
            }
        }
    }
    
    public boolean visualize(World world, int day, int tickInDay, int totalTick, List<String> events) {
        // Check if anything changed
        int aliveCount = 0;
        int totalCreatures = world.getCreatures().size();
        int foodCount = world.getFood().size();
        
        for (Creature c : world.getCreatures()) {
            if (c.isAlive()) aliveCount++;
        }
        
        // Build current creature states
        List<String> currentCreatureStates = new ArrayList<>();
        for (Creature creature : world.getCreatures()) {
            if (creature.isAlive()) {
                currentCreatureStates.add(creature.getName() + ":" + creature.getHealth());
            }
        }
        
        // Check if anything changed
        boolean hasChanges = 
            previousAliveCount != aliveCount ||
            previousTotalCreatures != totalCreatures ||
            previousFoodCount != foodCount ||
            !currentCreatureStates.equals(previousCreatureStates) ||
            (events != null && !events.isEmpty());
        
        // Only render if something changed or it's the first render
        if (hasChanges || previousAliveCount == -1) {
            clearGrid();
            
            // Place creatures on grid
            List<Creature> creatures = world.getCreatures();
            for (Creature creature : creatures) {
                if (creature.isAlive()) {
                    int x = creature.getX();
                    int y = creature.getY();
                    
                    if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT) {
                        if (creature.getSpecies().equals("Animal")) {
                            grid[y][x] = 'A';  // Animal
                        } else {
                            grid[y][x] = 'M';  // Monster
                        }
                        creatureNames[y][x] = creature.getName();
                        creatureHealth[y][x] = creature.getHealth();
                    }
                }
            }
            
            // Place food on grid
            List<Food> foodList = world.getFood();
            for (Food food : foodList) {
                if (!food.isConsumed()) {
                    int x = food.getX();
                    int y = food.getY();
                    
                    if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT && grid[y][x] == ' ') {
                        grid[y][x] = '•';  // Food
                    }
                }
            }
            
            // Update previous state
            previousAliveCount = aliveCount;
            previousTotalCreatures = totalCreatures;
            previousFoodCount = foodCount;
            previousCreatureStates = new ArrayList<>(currentCreatureStates);
            
            // Render the visualization with day/tick info
            render(day, tickInDay, totalTick, world, events);
            return true;
        }
        
        return false; // No changes, didn't render
    }
    
    private void clearScreen() {
        // Method 1: Try system command (works best in standalone terminals)
        try {
            String os = System.getProperty("os.name").toLowerCase();
            
            if (os.contains("win")) {
                // Windows: Use 'cls' command
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                return; // Success, exit early
            } else {
                // Unix/Linux/Mac: Use 'clear' command
                new ProcessBuilder("clear").inheritIO().start().waitFor();
                return; // Success, exit early
            }
        } catch (Exception e) {
            // Command failed, try next method
        }
        
        // Method 2: Try ANSI escape codes (works in modern terminals)
        try {
            System.out.print("\033[H\033[2J");
            System.out.flush();
            // Small delay to let it process
            Thread.sleep(10);
            return;
        } catch (Exception e) {
            // ANSI failed, use fallback
        }
        
        // Method 3: Reliable fallback - print many newlines
        // This works in ALL terminals and IDEs
        printNewlines(100);
    }
    
    private void printNewlines(int count) {
        // Print many newlines to clear visible area
        // This is the most reliable method across all platforms and IDEs
        for (int i = 0; i < count; i++) {
            System.out.println();
        }
        System.out.flush();
    }
    
    private void render(int day, int tickInDay, int totalTick, World world, List<String> events) {
        // Clear screen - try multiple methods for cross-platform compatibility
        clearScreen();
        
        // Title
        System.out.println();
        System.out.println(CYAN + "╔════════════════════════════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║" + RESET + "              " + BRIGHT_YELLOW + "LIVING WORLD SIMULATION" + RESET + "                " + CYAN + "║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════════════════════════════╝" + RESET);
        System.out.println();
        
        // Stats header with day/tick information
        int aliveCount = 0;
        for (Creature c : world.getCreatures()) {
            if (c.isAlive()) aliveCount++;
        }
        
        System.out.println("  " + GREEN + "Day: " + RESET + BRIGHT_YELLOW + day + RESET + 
                          "  │  " + GREEN + "Tick: " + RESET + BRIGHT_YELLOW + tickInDay + RESET + "/24" +
                          "  │  " + GREEN + "Total Ticks: " + RESET + BRIGHT_YELLOW + totalTick + RESET);
        System.out.println("  " + GREEN + "Alive: " + RESET + BRIGHT_GREEN + aliveCount + RESET + 
                          " / " + world.getCreatures().size() + 
                          "  │  " + GREEN + "Food: " + RESET + BRIGHT_YELLOW + world.getFood().size() + RESET);
        System.out.println();

        System.out.print("  " + CYAN + TOP_LEFT);
        for (int x = 0; x < GRID_WIDTH; x++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(TOP_RIGHT + RESET);

        for (int y = 0; y < GRID_HEIGHT; y++) {
            System.out.print("  " + CYAN + VERTICAL + RESET);
            
            for (int x = 0; x < GRID_WIDTH; x++) {
                char cell = grid[y][x];
                String color = RESET;
                
                if (cell == 'A') {
                    color = BRIGHT_GREEN;
                    System.out.print(color + "A" + RESET);
                } else if (cell == 'M') {
                    color = BRIGHT_RED;
                    System.out.print(color + "M" + RESET);
                } else if (cell == '•') {
                    color = BRIGHT_YELLOW;
                    System.out.print(color + "*" + RESET);
                } else {
                    if ((x + y) % 4 == 0) {
                        System.out.print("·");
                    } else {
                        System.out.print(" ");
                    }
                }
            }
            
            System.out.println(CYAN + VERTICAL + RESET);
        }

        System.out.print("  " + CYAN + BOTTOM_LEFT);
        for (int x = 0; x < GRID_WIDTH; x++) {
            System.out.print(HORIZONTAL);
        }
        System.out.println(BOTTOM_RIGHT + RESET);
        
        System.out.println();

        System.out.println("  " + BRIGHT_GREEN + "A" + RESET + " = Animal  " + 
                          BRIGHT_RED + "M" + RESET + " = Monster  " + 
                          BRIGHT_YELLOW + "*" + RESET + " = Food");
        System.out.println();

        // Creature stats (static - only updates when rendered)
        int shown = 0;
        for (Creature creature : world.getCreatures()) {
            if (creature.isAlive() && shown < 5) {
                String speciesColor = creature.getSpecies().equals("Animal") ? BRIGHT_GREEN : BRIGHT_RED;
                String healthBar = createHealthBar(creature.getHealth());
                String healthColor = creature.getHealth() > 50 ? BRIGHT_GREEN : 
                                    creature.getHealth() > 25 ? YELLOW : BRIGHT_RED;
                
                System.out.print("  " + speciesColor + creature.getName() + RESET + 
                               " [" + creature.getSpecies() + "] " +
                               healthColor + healthBar + " " + creature.getHealth() + "%" + RESET);
                if (shown < 4) System.out.print("  │  ");
                shown++;
            }
        }
        if (shown > 0) System.out.println();
        System.out.println();
        
        // Event log (only show recent events)
        if (events != null && !events.isEmpty()) {
            System.out.println(CYAN + "  Recent Events:" + RESET);
            // Show last 3 events
            int start = Math.max(0, events.size() - 3);
            for (int i = start; i < events.size(); i++) {
                System.out.println("  " + YELLOW + "•" + RESET + " " + events.get(i));
            }
            System.out.println();
        }
    }
    
    private String createHealthBar(int health) {
        int bars = health / 5;  // 20 bars max (100%)
        StringBuilder bar = new StringBuilder();
        String color = health > 50 ? BRIGHT_GREEN : health > 25 ? YELLOW : BRIGHT_RED;
        
        for (int i = 0; i < 20; i++) {
            if (i < bars) {
                bar.append(color).append("█").append(RESET);
            } else {
                bar.append("░");
            }
        }
        return bar.toString();
    }
    
    public void showInstructions() {
        System.out.println();
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println(YELLOW + "  TIP: For better visualization, reduce your terminal font size!" + RESET);
        System.out.println(CYAN + "═══════════════════════════════════════════════════════════════" + RESET);
        System.out.println();
        System.out.println("  " + GREEN + "Windows Terminal:" + RESET + " Settings → Appearance → Font size (try 8-10pt)");
        System.out.println("  " + GREEN + "PowerShell:" + RESET + " Right-click title bar → Properties → Font (try 8-10pt)");
        System.out.println("  " + GREEN + "VS Code Terminal:" + RESET + " Settings → Terminal → Font Size (try 10-12px)");
        System.out.println();
        System.out.println("  Smaller fonts allow more detail in the ASCII visualization!");
        System.out.println();
    }
}

