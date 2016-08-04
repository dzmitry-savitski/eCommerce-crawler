package com.upwork.dsavitski.crawler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@PropertySource(value = {"classpath:log4j.properties"})
public class LogService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);
    @Autowired
    private Environment environment;

    public String readLog() {
        String logFile = environment.getRequiredProperty("log4j.appender.file.File");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile), "utf-8"))) {
            String log = "";
            String line;
            while ((line = reader.readLine()) != null) {
                log += line + "<br/>\n";
            }
            return log;
        } catch (IOException e) {
            LOGGER.error("Log reading error: ", e);
            return "Log reading error! " + e.getMessage() +
                    " More info in log: " + logFile;
        }

    }

    public void clearLog() {
        String logFile = environment.getRequiredProperty("log4j.appender.file.File");
        try (PrintWriter writer = new PrintWriter(logFile)) {
        } catch (FileNotFoundException e) {
            LOGGER.error("Error clearing log file.", e);
        }
    }
}
