package com.upwork.dsavitski.crawler.threads;

import com.upwork.dsavitski.crawler.services.ProgressBar;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FetchProduct implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchProduct.class);

    private final URL url;
    private final JdbcTemplate jdbcTemplate;
    private ProgressBar progressBar;

    public FetchProduct(URL url, JdbcTemplate jdbcTemplate, ProgressBar progressBar) {
        this.url = url;
        this.jdbcTemplate = jdbcTemplate;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()))) {
            String htmlSource = "";
            String line;
            while ((line = in.readLine()) != null)
                htmlSource += line + "\n";
            in.close();

            Pattern p = Pattern.compile("<h1>(?<name>.*?)</h1>.*SKU</span>(?<upc>.*?)</div>.*regular-price\">.*?\\$(?<price>.*?)</div>", Pattern.DOTALL);
            Matcher matcher = p.matcher(htmlSource);

            if (matcher.find()) {
                String name = unescape(matcher.group("name"));
                String upc = unescape(matcher.group("upc"));
                String price = unescape(matcher.group("price"));
                saveProduct(upc, name, price);
            } else {
                LOGGER.error("Not match product regex for page " + url);
            }
        } catch (IOException e) {
            LOGGER.error("Error fetching page " + url, e);
        }
        progressBar.decrement();
    }

    private void saveProduct(String upc, String name, String price) {
        String sql = "INSERT INTO products (upc, name, price) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, upc, name, price);
        LOGGER.info("Saved to db: " + upc + " : " + name + " : " + price);
    }

    private String unescape(String inputString) {
        return StringEscapeUtils.unescapeHtml4(inputString.trim());
    }
}
