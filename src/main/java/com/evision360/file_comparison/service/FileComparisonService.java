package com.evision360.file_comparison.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
@Service
public class FileComparisonService {
    @Value("${basicfile.path}")
    private String basicfilePath;
    @Value("${otherfiles.directory}")
    private String filesDirectory;
    private final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public Map<String, Double> compareFiles() throws IOException {
        LOGGER.log(Level.INFO, "File Comparison in Single Thread");

        LOGGER.log(Level.INFO, "start Reading data from basic file " + Paths.get(basicfilePath).getFileName());
        Set<String> wordsInBasicFile = extractWords(Files.readString(Paths.get(basicfilePath)));
        LOGGER.log(Level.INFO, "finishing Reading data from basic file " + Paths.get(basicfilePath).getFileName());

        LOGGER.log(Level.INFO, "starting Reading data from other files ");
        Map<String, Double> scores = new HashMap<>();
        Files.list(Paths.get(filesDirectory)).forEach(filePath -> {
            try {
                Set<String> wordsInFile = extractWords(Files.readString(filePath));
                double score = calculateSimilarityScore(wordsInBasicFile, wordsInFile);
                scores.put(filePath.toString(), score);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        LOGGER.log(Level.INFO, "finishing Reading data from other files ");

        return scores;
    }

    private Set<String> extractWords(String content) {
        return Arrays.stream(content.split("\\W+"))
                .filter(word -> word.matches("[a-zA-Z]+"))
                .collect(Collectors.toSet());
    }

    private double calculateSimilarityScore(Set<String> wordsInBasicFile, Set<String> wordsInFile) {
        long commonWordsCount = wordsInBasicFile.stream()
                .filter(wordsInFile::contains)
                .count();
        return (double) commonWordsCount / wordsInBasicFile.size() * 100;
    }
}