package com.evision360.file_comparison.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
@Service
public class FileComparisonMultiThreadsService {
    @Value("${basicfile.path}")
    private String basicfilePath;
    @Value("${otherfiles.directory}")
    private String filesDirectory;
    private final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public Map<String, Double> compareFiles() throws IOException, InterruptedException {
        LOGGER.log(Level.INFO, "File Comparison in MultiThread");

        ExecutorService executor = Executors.newFixedThreadPool(5);
        LOGGER.log(Level.INFO, "start Reading data from basic file " + Paths.get(basicfilePath).getFileName());
        Set<String> wordsInBasicFile = extractWords(Files.readString(Paths.get(basicfilePath)));
        LOGGER.log(Level.INFO, "finishing Reading data from basic file " + Paths.get(basicfilePath).getFileName());

        LOGGER.log(Level.INFO, "starting Reading data from other files ");
        ConcurrentHashMap<String, Double> scores = new ConcurrentHashMap<>();
        Files.list(Paths.get(filesDirectory)).forEach(filePath -> {
            executor.execute(()->{
                try {
                    Set<String> wordsInFile = extractWords(Files.readString(filePath));
                    double score = calculateSimilarityScore(wordsInBasicFile, wordsInFile);
                    scores.put(filePath.getFileName().toString() + " " + Thread.currentThread().getName(), score);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            );

        });
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
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
