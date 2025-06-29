package com.example.fund_analyzer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/funds")
public class FundRestController {

    @Autowired
    private FundService fundService;


    @GetMapping("/update")
    public void updateFundInformation() {
        fundService.updateFundsFromFolder("/app/csvfiles");
    }

}
