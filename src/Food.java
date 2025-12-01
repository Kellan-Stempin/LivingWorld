public class Food {
    private String type;
    private int nutritionValue;
    private boolean consumed;
    private int x;
    private int y;

    public Food(String type, int nutritionValue) {
        this.type = type;
        this.nutritionValue = nutritionValue;
        this.consumed = false;
    }

    public int consume() {
        if (!consumed) {
            consumed = true;
            return nutritionValue;
        }
        return 0;
    }

    public String getType() { return type; }

    public int getNutritionValue() { return nutritionValue; }

    public boolean isConsumed() { return consumed; }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return type + "'s\n" + "Nutrition: " + nutritionValue + "\n" + "Consumed: " + consumed;
    }
}
