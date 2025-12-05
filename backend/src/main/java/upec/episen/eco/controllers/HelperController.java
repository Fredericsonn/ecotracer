package upec.episen.eco.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import upec.episen.eco.service.ServiceHelper;

@RestController
public class HelperController {

    @Autowired
    private ServiceHelper helper;

    @GetMapping("/usageCategories")
    public List<String> getUsageCategories() {
        return helper.getCategories();
    }
}
