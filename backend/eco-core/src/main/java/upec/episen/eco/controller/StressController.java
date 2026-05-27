package upec.episen.eco.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StressController {

    @GetMapping("/api/stress")
    public Map<String, Object> stress(@RequestParam(defaultValue = "500") long ms) {
        ms = Math.max(0, Math.min(ms, 10_000));

        Instant end = Instant.now().plus(Duration.ofMillis(ms));
        long x = 0;
        while (Instant.now().isBefore(end)) {
            x += System.nanoTime(); // keep CPU busy
        }
        return Map.of("burn_ms", ms, "sink", x);
    }
}