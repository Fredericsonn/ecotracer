package upec.episen.eco.models.machine.Algo;

public class RecyclabilityResult {
    private double recyclabilityPercentage;
    private String recyclabilityCategory;

    public RecyclabilityResult(double recyclabilityPercentage) {
        this.recyclabilityPercentage = recyclabilityPercentage;
        this.recyclabilityCategory = determineCategory(recyclabilityPercentage);
    }

    private String determineCategory(double percentage) {
        if (percentage >= 95.0) {
            return "Entièrement recyclable";
        } else if (percentage >= 50.0) {
            return "Majoritairement recyclable";
        } else {
            return "Non recyclable";
        }
    }

    public double getRecyclabilityPercentage() {
        return recyclabilityPercentage;
    }

    public String getRecyclabilityCategory() {
        return recyclabilityCategory;
    }

    @Override
    public String toString() {
        return "Recyclabilité: " + String.format("%.2f%%", recyclabilityPercentage) +
                " - " + recyclabilityCategory;
    }
}