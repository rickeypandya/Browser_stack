package main.java.com.scrapper ;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class WordFrequencyAnalyzer {
    public static void analyze(List<String> titles) {
        Map<String, Integer> freqMap = new HashMap<>();
        Pattern wordPattern = Pattern.compile("\\b\\w+\\b" , Pattern.CASE_INSENSITIVE);  // matches word tokens

        for (String title : titles) {
            Matcher matcher = wordPattern.matcher(title.toLowerCase());
            while (matcher.find()) {
                String word = matcher.group();
                freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
            }
        }

        List<String> reportLines = new ArrayList<>();
        reportLines.add("Repeated Words (occurring more than twice):\n");

        boolean found = false;
        for (Map.Entry<String, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > 2) {
                String line = "• " + entry.getKey() + " -> " + entry.getValue() + " times";
                reportLines.add(line);
                found = true;
            }
        }

        if (!found) {
            reportLines.add("No word appears more than twice.");
        }

        try {
            Files.createDirectories(Paths.get("output"));
            Files.write(Paths.get("output/reports.txt"), reportLines);
            System.out.println("Word frequency report written to: output/reports.txt");
        } catch (IOException e) {
            System.err.println("Failed to write word frequency report: " + e.getMessage());
        }
    }
}
