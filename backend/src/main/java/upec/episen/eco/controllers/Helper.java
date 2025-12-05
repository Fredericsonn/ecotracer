package upec.episen.eco.controllers;

public class Helper {

    public static String usageCategoryRectifier(String category) {
        if (category.contains(" ")) return category.replace(" ", "_");
        return category;
    }
}
