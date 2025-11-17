public class Food {
    private String type;
    private int nutritionValue;
    private boolean consumed;

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

    @Override
    public String toString() {
        return type + "'s\n" + "Nutrition: " + nutritionValue + "\n" + "Consumed: " + consumed;
    }
}
