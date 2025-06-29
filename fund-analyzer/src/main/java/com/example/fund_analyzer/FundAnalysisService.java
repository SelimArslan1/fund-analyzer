package com.example.fund_analyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


@Service
public class FundAnalysisService {

    @Autowired
    private FundRepository fundRepository;

    @Autowired
    private FundStreakRepository fundStreakRepository;

    public void analyzePatterns() {
        // Fetch all funds ordered by fundCode and date ascending
        List<Fund> funds = fundRepository.findAllByOrderByCodeAscDateAsc();

        String currentCode = null;
        List<Fund> currentFundSeries = new ArrayList<>();

        for (Fund fund : funds) {
            if (!fund.getCode().equals(currentCode)) {

                if (!currentFundSeries.isEmpty()) {
                    analyzeAndStoreStreaks(currentFundSeries);
                }
                currentFundSeries = new ArrayList<>();
                currentCode = fund.getCode();
            }
            currentFundSeries.add(fund);
        }

        if (!currentFundSeries.isEmpty()) {
            analyzeAndStoreStreaks(currentFundSeries);
        }
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private void analyzeAndStoreStreaks(List<Fund> fundSeries) {
        Map<String, Function<Fund, Double>> numericFields = Map.of(
                "peopleCount", fund -> (double) fund.getPeopleCount(),
                "shareCount", fund -> (double) fund.getShareCount(),
                "price", Fund::getPrice,
                "totalPrice", Fund::getTotalPrice
        );

        List<String> matchedPatterns = new ArrayList<>();

        for (String field : numericFields.keySet()) {
            int streakLength = 0;
            LocalDate streakStartDate = null;
            Function<Fund, Double> extractor = numericFields.get(field);

            for (int i = 1; i < fundSeries.size(); i++) {
                Fund prev = fundSeries.get(i - 1);
                Fund curr = fundSeries.get(i);

                if (extractor.apply(curr) > extractor.apply(prev)) {
                    if (streakLength == 0) {
                        streakStartDate = prev.getDate();
                    }
                    streakLength++;
                    if (streakLength >= 2) {
                        matchedPatterns.add(field);
                        break; // Only log once per field
                    }
                } else {
                    streakLength = 0;
                    streakStartDate = null;
                }
            }
        }

        if (!matchedPatterns.isEmpty()) {
            FundStreak streak = new FundStreak();
            streak.setFundCode(fundSeries.get(0).getCode());
            streak.setStartDate(fundSeries.get(0).getDate());

            JsonNode jsonArrayPattern = objectMapper.valueToTree(Map.of("pattern", matchedPatterns));
            streak.setPattern(jsonArrayPattern);
            System.out.println("Saving streak for " + streak.getFundCode());
            fundStreakRepository.save(streak);
        }
    }

    @Transactional
    public void clearDatabase() {
        fundStreakRepository.deleteAll();
    }

    @Transactional
    public List<FundStreak> getAll() {
        return fundStreakRepository.findAll();
    }
}

