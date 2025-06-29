package com.example.fund_analyzer;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FundService {

    @Autowired
    private FundRepository fundRepository;

    public void updateFundsFromFolder(String folderPath) {
        clearDatabase();

        try (Stream<Path> paths = Files.list(Paths.get(folderPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".csv"))
                    .forEach(p -> updateFunds(p.toString()));
        } catch (IOException e) {
            System.err.println("Error listing files: " + e.getMessage());
        }
    }

    @Transactional
    public void updateFunds(String csvPath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath))) {
            String[] headers = csvReader.readNext(); // read header line

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                // Assuming columns order: "Tarih","Fon Kodu","Fon Adı","Fiyat","Tedavüldeki Pay Sayısı","Kişi Sayısı","Fon Toplam Değer"
                Fund fund = new Fund();

                fund.setDate(LocalDate.parse(row[0], java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                fund.setCode(row[1]);
                fund.setName(row[2]);
                fund.setPrice(parseDouble(row[3]));
                fund.setShareCount(parseLong(row[4]));
                fund.setPeopleCount(parseInt(row[5]));
                fund.setTotalPrice(parseDouble(row[6]));

                fundRepository.save(fund);
            }
        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    @Transactional
    public void clearDatabase() {
        fundRepository.deleteAll();
    }

    @Transactional
    public List<Fund> getAll() {
        return fundRepository.findAll();
    }

    private long parseLong(String s) {
        String noThousands = s.replace(".", "");
        String integerPart = noThousands.split(",")[0];
        return Long.parseLong(integerPart);
    }

    private double parseDouble(String s) {
        return Double.parseDouble(s.replace(".", "").replace(",", "."));
    }


    private int parseInt(String s) {
        return Integer.parseInt(s.replace(".", "").replace(",", ""));
    }
}
