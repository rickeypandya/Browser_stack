package main.java.com.scrapper;

public class Article {
    private String spanishTitle;
    private String englishTitle;
    private String content;
    private String imagePath;

    public Article(String spanishTitle, String content, String imagePath) {
        this.spanishTitle = spanishTitle;
        this.content = content;
        this.imagePath = imagePath;
    }

    public String getSpanishTitle() { return spanishTitle; }
    public String getEnglishTitle() { return englishTitle; }
    public void setEnglishTitle(String englishTitle) { this.englishTitle = englishTitle; }
    public String getContent() { return content; }
    public String getImagePath() { return imagePath; }

    public String getSnippet() {
        if (content == null) return "";
        return content.length() <= 150 ? content : content.substring(0, 150) + "...";
    }
}