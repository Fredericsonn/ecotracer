package upec.episen.eco.controller;

import java.net.InetAddress;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InstanceController {

    @GetMapping("/api/instance")
    public Map<String, String> instance() throws Exception {
        return Map.of("hostname", InetAddress.getLocalHost().getHostName());
    }
}