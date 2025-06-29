package com.example.fund_analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FundRestController {

    @Autowired
    private FundService fundService;

    @Autowired
    private FundAnalysisService fundAnalysisService;

    @GetMapping("/funds")
    public List<Fund> getFunds() {
        return fundService.getAll();
    }
    @GetMapping("/fund-streaks")
    public List<FundStreak> getFundStreaks() {
        return fundAnalysisService.getAll();
    }
    @GetMapping("/funds/{metric}")
    public List<FundStreak> getFundStreakByMetric(@PathVariable String metric) {
        List<FundStreak> allStreaks = fundAnalysisService.getAll();

        return allStreaks.stream()
                .filter(streak -> {
                    try {
                        JsonNode root = streak.getPattern();
                        JsonNode patternsNode = root.get("pattern");
                        if (patternsNode != null && patternsNode.isArray()) {
                            for (JsonNode node : patternsNode) {
                                if (node.asText().equals(metric)) {
                                    return true;
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Optionally log error
                    }
                    return false;
                })
                .toList();
    }


    @GetMapping("/update")
    public void updateFundInformation() {
        fundService.updateFundsFromFolder("/app/csvfiles");
    }

    @GetMapping("/analyze")
    public void analyzeData() {
        fundAnalysisService.clearDatabase();
        fundAnalysisService.analyzePatterns();
    }

}
