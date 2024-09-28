package com.evision360.file_comparison.controller;

import com.evision360.file_comparison.service.FileComparisonMultiThreadsService;
import com.evision360.file_comparison.service.FileComparisonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class FileComparisonController {
    @Autowired
    private FileComparisonService fileComparisonService;
    @Autowired
    private FileComparisonMultiThreadsService fileComparisonMultiThreadsServicee;
    private final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @GetMapping("compare-files")
    public Map<String, Double> compareFiles() throws IOException {
        long start = System.currentTimeMillis();
        Map<String, Double> compareFiles = fileComparisonService.compareFiles();
        long end = System.currentTimeMillis();

        long elapsedTime = (end - start);
        LOGGER.log(Level.INFO, "Compariosn process take " + (double) elapsedTime / 1000.0 + " Seconds ");
        LOGGER.log(Level.INFO, "Compariosn process take " + elapsedTime + " Milliseconds ");

        return compareFiles ;
    }
    @GetMapping("compare-files-threads")
    public Map<String, Double> compareFilesThreads() throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        Map<String, Double> compareFiles = fileComparisonMultiThreadsServicee.compareFiles();
        long end = System.currentTimeMillis();

        long elapsedTime = (end - start);
        LOGGER.log(Level.INFO, "Compariosn process take " + (double) elapsedTime / 1000.0 + " Seconds ");
        LOGGER.log(Level.INFO, "Compariosn process take " + elapsedTime + " Milliseconds ");

        return compareFiles ;
    }
}
