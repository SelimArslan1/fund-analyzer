package com.example.fund_analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/funds")
public class FundRestController {

    @Autowired
    private FundService fundService;

    @Autowired
    private FundAnalysisService fundAnalysisService;

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
