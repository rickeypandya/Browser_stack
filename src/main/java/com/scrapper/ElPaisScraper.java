package main.java.com.scrapper;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;

public class ElPaisScraper {
    public static void main(String[] args) throws Exception {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
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

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Step 1: Try closing the overlay if it's present
            try {
                WebElement overlay = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("blockNavigation")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='none';", overlay);
                System.out.println("Overlay blocked navigation. Hiding it...");
            } catch (org.openqa.selenium.TimeoutException e) {
                System.out.println("No blocking overlay found.");
            }

            // Step 2: Wait until the Opinion link is clickable, then click
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
                    } catch (org.openqa.selenium.NoSuchElementException ignore) {}

                    String imagePath = null;
                    try {
                        WebElement img = article.findElement(By.cssSelector("img"));
                        String imgUrl = img.getAttribute("src");
                        imagePath = downloadImage(imgUrl, "output/images/article" + (++count) + ".jpg");
                    } catch (org.openqa.selenium.NoSuchElementException ignore) {}

                    Article art = new Article(title, snippet, imagePath);
                    art.setEnglishTitle(Translator.translate(title, "en"));
                    scraped.add(art);
                } catch (Exception e) { 
                    // Skip problematic article
                    System.out.println("Error Occured parsing the article !");
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

            // Save translated titles to output file
            writeTranslatedTitlesToFile(scraped, "output/translated_titles.txt");
            writeSpanishScrappings(scraped, "output/spanish_scrappings.txt");
            WordFrequencyAnalyzer.analyze(englishTitles);
        } finally {
            driver.quit();
        }
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
