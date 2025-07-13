package com.scrapper;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriverRunner {

    private static final String BROWSERSTACK_USERNAME = "hemank_4INeUm";
    private static final String BROWSERSTACK_ACCESS_KEY = "7iTxNwfpubUT6w14QMVV";
    private static final String BROWSERSTACK_URL = "https://" + BROWSERSTACK_USERNAME + ":" + BROWSERSTACK_ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

    public static void main(String[] args) {
        // Define all desired capabilities
        List<Capabilities> capabilitiesList = List.of(
        getWindowsEdgeCaps(),
        getMobileSafariCaps(),
        getFirefoxLinuxCaps(),
        getWindowsChromeCaps(),
        getMobileCaps()
);

        ExecutorService executor = Executors.newFixedThreadPool(capabilitiesList.size());   
        for (Capabilities caps : capabilitiesList) {
            executor.submit(new ScraperTask(caps));
        }

        executor.shutdown();
    }

    public static Capabilities getWindowsEdgeCaps() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "Edge");

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "10");
        bstackOptions.put("browserVersion", "latest");
        bstackOptions.put("sessionName", "Windows Edge Scrape");
        bstackOptions.put("userName", BROWSERSTACK_USERNAME);
        bstackOptions.put("accessKey", BROWSERSTACK_ACCESS_KEY);
        bstackOptions.put("projectName", "Selenium Script");
        bstackOptions.put("buildName", "BrowserStack Project");

        caps.setCapability("bstack:options", bstackOptions);
        return caps;
    }

    public static Capabilities getFirefoxLinuxCaps() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "Firefox");
    
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "OS X");
        bstackOptions.put("osVersion", "Monterey");
        bstackOptions.put("browserVersion", "latest");
        bstackOptions.put("sessionName", "macOS Firefox Scrape");
        bstackOptions.put("userName", BROWSERSTACK_USERNAME);
        bstackOptions.put("accessKey", BROWSERSTACK_ACCESS_KEY);
        bstackOptions.put("projectName", "Selenium Script");
        bstackOptions.put("buildName", "BrowserStack Project");
    
        caps.setCapability("bstack:options", bstackOptions);
        return caps;
    }
    public static Capabilities getMobileSafariCaps() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "safari");
    
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("deviceName", "iPhone 14");
        bstackOptions.put("osVersion", "16");
        bstackOptions.put("realMobile", "true");
        bstackOptions.put("sessionName", "iPhone Safari Scrape");
        bstackOptions.put("userName", BROWSERSTACK_USERNAME);
        bstackOptions.put("accessKey", BROWSERSTACK_ACCESS_KEY);
        bstackOptions.put("projectName", "Selenium Script");
        bstackOptions.put("buildName", "BrowserStack Project");
    
        caps.setCapability("bstack:options", bstackOptions);
        return caps;
    }
    
    public static Capabilities getWindowsChromeCaps() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "Chrome");
    
        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "Windows");
        bstackOptions.put("osVersion", "11");
        bstackOptions.put("browserVersion", "latest");
        bstackOptions.put("sessionName", "Windows Chrome Scrape");
        bstackOptions.put("userName", BROWSERSTACK_USERNAME);
        bstackOptions.put("accessKey", BROWSERSTACK_ACCESS_KEY);
        bstackOptions.put("projectName", "Selenium Script");
        bstackOptions.put("buildName", "BrowserStack Project");
    
        caps.setCapability("bstack:options", bstackOptions);
        return caps;
    }

    public static Capabilities getMobileCaps() {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", "chrome");

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("deviceName", "Samsung Galaxy S22 Ultra");
        bstackOptions.put("osVersion", "12.0");
        bstackOptions.put("realMobile", "true");
        bstackOptions.put("sessionName", "Galaxy S22 Chrome Scrape");
        bstackOptions.put("userName", BROWSERSTACK_USERNAME);
        bstackOptions.put("accessKey", BROWSERSTACK_ACCESS_KEY);
        bstackOptions.put("projectName", "Selenium Script");
        bstackOptions.put("buildName", "BrowserStack Project");


        caps.setCapability("bstack:options", bstackOptions);
        return caps;
    }

    public static class ScraperTask implements Runnable {
        private final Capabilities capabilities;

        public ScraperTask(Capabilities capabilities) {
            this.capabilities = capabilities;
        }

        @Override
        public void run() {
            try {
                WebDriver driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), capabilities);
                System.out.println("🔍 Starting scrape with: " + capabilities);
                driver.get("https://elpais.com/opinion/");
                // Your scraping logic here
                ElPaisScraper.scrape(driver);
                System.out.println("✅ Done scraping: " + driver.getTitle());
                driver.quit();
            } catch (Exception e) {
                System.err.println("❌ Error during scraping: " + e.getMessage());
            }
        }
    }
}
