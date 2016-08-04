package com.upwork.dsavitski.crawler.controllers;

import com.upwork.dsavitski.crawler.services.CrawlerService;
import com.upwork.dsavitski.crawler.services.LogService;
import com.upwork.dsavitski.crawler.services.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    LogService logService;

    @Autowired
    CrawlerService crawlerService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProgressBar progressBar;

    @RequestMapping("/")
    public String index(Model model) {
        return "index";
    }

    @RequestMapping("/log")
    public String log(Model model) {
        model.addAttribute("log", logService.readLog());
        return "log";
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public String start(@RequestParam(value = "url", required = true) String url,
                        RedirectAttributes redirectAttributes) {
        crawlerService.start(url);

        redirectAttributes.addFlashAttribute("startButton", "disabled");
        redirectAttributes.addFlashAttribute("startProgressBar", true);
        return "redirect:/";
    }

    @RequestMapping(value = "/logClear", method = RequestMethod.POST)
    public String logClear() {
        logService.clearLog();
        return "redirect:/";
    }

    @RequestMapping(value = "/productsClear", method = RequestMethod.POST)
    public String productsClear() {
        jdbcTemplate.update("TRUNCATE products");
        return "redirect:/";
    }

    @RequestMapping("/products")
    public String products(Model model) {
        final List<Map<String, Object>> list = jdbcTemplate.queryForList("SELECT * from products");
        model.addAttribute("products", list);
        return "products";
    }

    @RequestMapping("/getProgress")
    @ResponseBody
    public String getProgress() {
        return String.valueOf(progressBar.getCurrentPercent());
    }
}
