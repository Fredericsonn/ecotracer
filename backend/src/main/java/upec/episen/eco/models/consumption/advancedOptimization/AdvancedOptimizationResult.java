package upec.episen.eco.models.consumption.advancedOptimization;

import java.util.List;

public class AdvancedOptimizationResult {
    private boolean solutionFound;
    private String message;
    private List<OptimizedItemResult> optimizedItems;
    private double originalTotalCarbon;
    private double targetTotalCarbon;
    private double achievedTotalCarbon;
    private double originalTotalCost;
    private double targetMaxCost;
    private double achievedTotalCost;

    public AdvancedOptimizationResult(boolean solutionFound, String message) {
        this.solutionFound = solutionFound;
        this.message = message;
    }

    public boolean isSolutionFound() { return solutionFound; }
    public void setSolutionFound(boolean solutionFound) { this.solutionFound = solutionFound; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public List<OptimizedItemResult> getOptimizedItems() { return optimizedItems; }
    public void setOptimizedItems(List<OptimizedItemResult> optimizedItems) { this.optimizedItems = optimizedItems; }
    public double getOriginalTotalCarbon() { return originalTotalCarbon; }
    public void setOriginalTotalCarbon(double originalTotalCarbon) { this.originalTotalCarbon = originalTotalCarbon; }
    public double getTargetTotalCarbon() { return targetTotalCarbon; }
    public void setTargetTotalCarbon(double targetTotalCarbon) { this.targetTotalCarbon = targetTotalCarbon; }
    public double getAchievedTotalCarbon() { return achievedTotalCarbon; }
    public void setAchievedTotalCarbon(double achievedTotalCarbon) { this.achievedTotalCarbon = achievedTotalCarbon; }
    public double getOriginalTotalCost() { return originalTotalCost; }
    public void setOriginalTotalCost(double originalTotalCost) { this.originalTotalCost = originalTotalCost; }
    public double getTargetMaxCost() { return targetMaxCost; }
    public void setTargetMaxCost(double targetMaxCost) { this.targetMaxCost = targetMaxCost; }
    public double getAchievedTotalCost() { return achievedTotalCost; }
    public void setAchievedTotalCost(double achievedTotalCost) { this.achievedTotalCost = achievedTotalCost; }
}