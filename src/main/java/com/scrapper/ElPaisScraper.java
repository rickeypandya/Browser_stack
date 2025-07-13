// package main.java.com.scrapper;

// import org.openqa.selenium.*;
// import org.openqa.selenium.remote.*;
// import org.openqa.selenium.support.ui.*;

// import java.io.*;
// import java.net.URL;
// import java.nio.file.*;
// import java.time.Duration;
// import java.util.*;

// public class ElPaisScraper {
//     public static final String USERNAME = "hemank_4INeUm";
//     public static final String ACCESS_KEY = "7iTxNwfpubUT6w14QMVV";
//     public static final String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

//     public static void scrape(Web) throws Exception {
//         Map<String, Object> bstackOptions = new HashMap<>();
//         bstackOptions.put("os", "Windows");
//         bstackOptions.put("osVersion", "10");
//         bstackOptions.put("projectName", "BrowserStack Sample");
//         bstackOptions.put("buildName", "bstack-demo");
//         bstackOptions.put("sessionName", "ElPaisScraper");
//         bstackOptions.put("debug", true);
//         bstackOptions.put("networkLogs", true);
//         bstackOptions.put("consoleLogs", "info");

//         MutableCapabilities caps = new MutableCapabilities();
//         caps.setCapability("browserName", "Chrome");
//         caps.setCapability("browserVersion", "120.0");
//         caps.setCapability("bstack:options", bstackOptions);

//         WebDriver driver = new RemoteWebDriver(new URL(URL), caps);
//         driver.get("https://elpais.com");

//         Path imagesDir = Paths.get("output/images");
//         if (Files.exists(imagesDir)) {
//             try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDir)) {
//                 for (Path file : stream) {
//                     Files.delete(file);
//                 }
//             }
//         } else {
//             Files.createDirectories(imagesDir);
//         }

//         try {
//             WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

//             // Dismiss overlay if present
//             try {
//                 WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("blockNavigation")));
//                 ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
//                 System.out.println("Overlay blocked navigation. Hiding it...");
//             } catch (TimeoutException e) {
//                 System.out.println("No blocking overlay found.");
//             }

//             // ✅ Accept cookie modal if present
//             try {
//                 WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("didomi-notice-agree-button")));
//                 acceptButton.click();
//                 System.out.println("✅ Accepted cookie consent.");
//             } catch (TimeoutException e) {
//                 System.out.println("ℹ️ No cookie consent popup found.");
//             }

//             // Now click the Opinión tab
//             WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[4]/header/div[2]/div[1]/nav/div/a[2]")));
//             opinionLink.click();
//             wait.until(ExpectedConditions.urlContains("opinion"));

//             List<WebElement> articles = driver.findElements(By.cssSelector("article"));
//             List<Article> scraped = new ArrayList<>();
//             int count = 0;

//             for (int i = 0; i < Math.min(5, articles.size()); i++) {
//                 WebElement article = articles.get(i);
//                 try {
//                     WebElement headerLink = article.findElement(By.cssSelector("h2.c_t a"));
//                     String title = headerLink.getText();

//                     String snippet = "";
//                     try {
//                         snippet = article.findElement(By.cssSelector("p.c_d")).getText();
//                     } catch (Exception ignore) {}

//                     String imagePath = null;
//                     try {
//                         WebElement img = article.findElement(By.cssSelector("img"));
//                         String imgUrl = img.getAttribute("src");
//                         imagePath = downloadImage(imgUrl, "output/images/article" + (++count) + ".jpg");
//                     } catch (Exception ignore) {}

//                     Article art = new Article(title, snippet, imagePath);
//                     art.setEnglishTitle(Translator.translate(title, "en"));
//                     scraped.add(art);
//                 } catch (Exception e) {
//                     System.out.println("⚠️ Error occurred parsing an article.");
//                 }
//             }

//             for (Article a : scraped) {
//                 System.out.println("\uD83D\uDD39 Spanish Title: " + a.getSpanishTitle());
//                 System.out.println("\uD83D\uDD38 English Title: " + a.getEnglishTitle());
//                 System.out.println("\uD83D\uDCC4 Snippet: " + a.getSnippet());
//                 if (a.getImagePath() != null)
//                     System.out.println("\uD83D\uDDBC️ Image Saved: " + a.getImagePath());
//                 System.out.println();
//             }

//             List<String> englishTitles = new ArrayList<>();
//             for (Article a : scraped) englishTitles.add(a.getEnglishTitle());

//             WordFrequencyAnalyzer.analyze(englishTitles);
//             writeTranslatedTitlesToFile(scraped, "output/translated_titles.txt");
//             writeSpanishScrappings(scraped, "output/spanish_scrappings.txt");

//             SessionId sessionId = ((RemoteWebDriver) driver).getSessionId();
//             System.out.println("🔗 View session at: https://automate.browserstack.com/sessions/" + sessionId);

//         } finally {
//             driver.quit();
//         }
//     }

//     private static String downloadImage(String imgUrl, String path) throws IOException {
//         Files.createDirectories(Paths.get("output/images"));
//         InputStream in = new URL(imgUrl).openStream();
//         Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
//         return path;
//     }

//     private static void writeTranslatedTitlesToFile(List<Article> articles, String filePath) throws IOException {
//         Files.createDirectories(Paths.get("output"));
//         try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
//             for (Article a : articles) {
//                 writer.write(a.getEnglishTitle());
//                 writer.newLine();
//             }
//         }
//         System.out.println("Translated titles written to: " + filePath);
//     }

//     private static void writeSpanishScrappings(List<Article> articles, String filePath) throws IOException {
//         Files.createDirectories(Paths.get("output"));
//         try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
//             for (Article a : articles) {
//                 writer.write("\uD83D\uDD39 Title: " + a.getSpanishTitle());
//                 writer.newLine();
//                 writer.write("\uD83D\uDCC4 Snippet: " + a.getSnippet());
//                 writer.newLine();
//                 writer.newLine();
//             }
//         }
//         System.out.println("Spanish scrappings written to: " + filePath);
//     }
// }

package com.scrapper;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.net.URL;

public class ElPaisScraper {
    public static void scrape(WebDriver driver) throws Exception {
        driver.get("https://elpais.com");

        Path imagesDir = Paths.get("output/images");
        if (Files.exists(imagesDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(imagesDir)) {
                for (Path file : stream) {
                    Files.delete(file);
                }
            }
        } else {
            Files.createDirectories(imagesDir);
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Remove blocking overlay
        try {
            WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("blockNavigation")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
            System.out.println("Overlay blocked navigation. Hiding it...");
        } catch (TimeoutException e) {
            System.out.println("No blocking overlay found.");
        }

        // Accept cookies
        try {
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("didomi-notice-agree-button")));
            acceptButton.click();
            System.out.println("✅ Accepted cookie consent.");
        } catch (TimeoutException e) {
            System.out.println("ℹ️ No cookie consent popup found.");
        }

        // Click Opinión
        WebElement opinionLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[4]/header/div[2]/div[1]/nav/div/a[2]")));
        opinionLink.click();
        wait.until(ExpectedConditions.urlContains("opinion"));

        List<WebElement> articles = driver.findElements(By.cssSelector("article"));
        List<Article> scraped = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < Math.min(5, articles.size()); i++) {
            WebElement article = articles.get(i);
            try {
                WebElement headerLink = article.findElement(By.cssSelector("h2.c_t a"));
                String title = headerLink.getText();

                String snippet = "";
                try {
                    snippet = article.findElement(By.cssSelector("p.c_d")).getText();
                } catch (Exception ignore) {}

                String imagePath = null;
                try {
                    WebElement img = article.findElement(By.cssSelector("img"));
                    String imgUrl = img.getAttribute("src");
                    imagePath = downloadImage(imgUrl, "output/images/article" + (++count) + ".jpg");
                } catch (Exception ignore) {}

                Article art = new Article(title, snippet, imagePath);
                art.setEnglishTitle(Translator.translate(title, "en"));
                scraped.add(art);
            } catch (Exception e) {
                System.out.println("⚠️ Error occurred parsing an article.");
            }
        }

        for (Article a : scraped) {
            System.out.println("🔹 Spanish Title: " + a.getSpanishTitle());
            System.out.println("🔸 English Title: " + a.getEnglishTitle());
            System.out.println("📄 Snippet: " + a.getSnippet());
            if (a.getImagePath() != null)
                System.out.println("🖼️ Image Saved: " + a.getImagePath());
            System.out.println();
        }

        List<String> englishTitles = new ArrayList<>();
        for (Article a : scraped) englishTitles.add(a.getEnglishTitle());

        WordFrequencyAnalyzer.analyze(englishTitles);
        writeTranslatedTitlesToFile(scraped, "output/translated_titles.txt");
        writeSpanishScrappings(scraped, "output/spanish_scrappings.txt");
    }

    private static String downloadImage(String imgUrl, String path) throws IOException {
        Files.createDirectories(Paths.get("output/images"));
        InputStream in = new URL(imgUrl).openStream();
        Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    private static void writeTranslatedTitlesToFile(List<Article> articles, String filePath) throws IOException {
        Files.createDirectories(Paths.get("output"));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Article a : articles) {
                writer.write(a.getEnglishTitle());
                writer.newLine();
            }
        }
        System.out.println("Translated titles written to: " + filePath);
    }

    private static void writeSpanishScrappings(List<Article> articles, String filePath) throws IOException {
        Files.createDirectories(Paths.get("output"));
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            for (Article a : articles) {
                writer.write("🔹 Title: " + a.getSpanishTitle());
                writer.newLine();
                writer.write("📄 Snippet: " + a.getSnippet());
                writer.newLine();
                writer.newLine();
            }
        }
        System.out.println("Spanish scrappings written to: " + filePath);
    }
}

