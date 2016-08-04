package com.upwork.dsavitski.crawler.services;

import com.upwork.dsavitski.crawler.exceptions.CrawlerException;
import com.upwork.dsavitski.crawler.threads.FetchProduct;
import crawlercommons.fetcher.http.SimpleHttpFetcher;
import crawlercommons.fetcher.http.UserAgent;
import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.RobotUtils;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Main crawler service.
 */
@Service
public class CrawlerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlerService.class);
    private static final int MAX_THREADS = 10;
    private static final String URL_CRITERIA = "/product/";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProgressBar progressBar;

    /**
     * Starts parsing given url.
     */
    public void start(String siteUrl) throws CrawlerException {
        try {
            LOGGER.info("<div style=\"color: green\"> Starting " + siteUrl + "</div>");
            URL url = new URL(siteUrl + "robots.txt");

            LOGGER.info("Fetching sitemaps from " + url);
            List<String> sitemaps = fetchSitemaps(url);
            LOGGER.info("Received " + sitemaps.size() + " sitemap links");

            LOGGER.info("Fetching urls...");

            Set<URL> products = fetchUrls(sitemaps);
            parseProducts(products);
        } catch (MalformedURLException e) {
            LOGGER.error("Bad url: " + siteUrl, e);
            throw new CrawlerException("Bad url: " + siteUrl, e);
        }
    }

    private List<String> fetchSitemaps(URL url) {
        final UserAgent userAgent = new UserAgent("Googlebot", "http://www.google.com/bot.html", "");
        final SimpleHttpFetcher httpFetcher = new SimpleHttpFetcher(userAgent);

        final BaseRobotRules rules = RobotUtils.getRobotRules(httpFetcher,
                new SimpleRobotRulesParser(),
                url);

        final List<String> sitemaps = rules.getSitemaps();
        return sitemaps;
    }

    /**
     * Recursively gets all URL's from site map.
     */
    private Collection<SiteMapURL> getUrls(URL sitemapUrl) throws IOException, UnknownFormatException {
        AbstractSiteMap siteMap = new SiteMapParser().parseSiteMap(sitemapUrl);
        if (siteMap.isIndex()) {
            Collection<SiteMapURL> urls = new HashSet<>();

            Collection<AbstractSiteMap> sitemaps = ((SiteMapIndex) siteMap).getSitemaps();
            for (AbstractSiteMap abstractSiteMap : sitemaps) {
                urls.addAll(getUrls(abstractSiteMap.getUrl()));
            }
            return urls;
        } else {
            Collection<SiteMapURL> urls = ((SiteMap) siteMap).getSiteMapUrls();
            System.out.println("parsed " + urls.size() + " urls");
            return urls;
        }
    }

    private Set<URL> fetchUrls(List<String> sitemaps) {
        Set<SiteMapURL> allUrls = new HashSet<>();
        for (String sitemapUrl : sitemaps) {
            try {
                allUrls.addAll(getUrls(new URL(sitemapUrl)));
            } catch (IOException | UnknownFormatException e) {
                LOGGER.error("Error parsing sitemap: " + sitemapUrl, e);
            }
        }
        LOGGER.info("Finished! Received " + allUrls.size() + " urls");
        return filterUrls(allUrls);
    }

    /**
     * Filters URL's by given regexp.
     */
    private Set<URL> filterUrls(Set<SiteMapURL> allUrls) {
        Set<URL> products = new HashSet<>();
        for (SiteMapURL url : allUrls) {
            if (url.getUrl().toString().contains(URL_CRITERIA)) {
                products.add(url.getUrl());
            }
        }
        LOGGER.info("Product urls after filtering: " + products.size());
        progressBar.setInitCount(products.size());
        return products;
    }

    /**
     * Creates thread pool and new parsing threads.
     */
    private void parseProducts(final Set<URL> products) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREADS);
                for (URL url : products) {
                    executor.submit(new FetchProduct(url, jdbcTemplate, progressBar));
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOGGER.info("<div style=\"color: green\">Finished</div>");
            }
        }).start();
    }
}
