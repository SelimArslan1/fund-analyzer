package com.example.fund_analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FundRestController {

    @Autowired
    private FundService fundService;

    @Autowired
    private FundAnalysisService fundAnalysisService;

    @GetMapping("/funds")
    public List<Fund> getFunds(@RequestParam(required = false) String code) {
        if (code != null) {
            return fundService.getByCode(code);
        } else {
            return fundService.getAll();
        }
    }

    @GetMapping("/fund-streaks")
    public List<FundStreak> getFundStreaks() {
        return fundAnalysisService.getAll();
    }
    @GetMapping("/funds/metric")
    public List<FundStreak> getFundStreakByMetrics(
            @RequestParam(defaultValue = "false") boolean price,
            @RequestParam(defaultValue = "false") boolean shareCount,
            @RequestParam(defaultValue = "false") boolean peopleCount,
            @RequestParam(defaultValue = "false") boolean totalPrice) {

        List<String> requiredPatterns = new ArrayList<>();
        if (price) requiredPatterns.add("price");
        if (shareCount) requiredPatterns.add("shareCount");
        if (peopleCount) requiredPatterns.add("peopleCount");
        if (totalPrice) requiredPatterns.add("totalPrice");

        if (requiredPatterns.isEmpty()) {
            return fundAnalysisService.getAll();
        }

        return fundAnalysisService.getAll().stream()
                .filter(streak -> {
                    JsonNode patternsNode = streak.getPattern().get("pattern");
                    if (patternsNode == null || !patternsNode.isArray()) return false;

                    List<String> actualPatterns = new ArrayList<>();
                    patternsNode.forEach(node -> actualPatterns.add(node.asText()));

                    // Return true only if ALL required patterns are present
                    return actualPatterns.containsAll(requiredPatterns);
                })
                .toList();
    }

    @PostMapping("/update")
    public void updateFundInformation() {
        fundService.updateFundsFromFolder("/app/csvfiles");
    }

    @PostMapping("/analyze")
    public void analyzeData() {
        fundAnalysisService.clearDatabase();
        fundAnalysisService.analyzePatterns();
    }

}
