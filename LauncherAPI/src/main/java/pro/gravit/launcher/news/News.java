package pro.gravit.launcher.news;

import java.util.Arrays;

public final class News {


    public final String title;
    public final String content;
    public final NewsPhoto[] newsPhotos;
    public final Integer createdDate;

    public News(String title, String content, NewsPhoto[] newsPhotos, Integer createdDate) {
        this.title = title;
        this.content = content;
        this.newsPhotos = newsPhotos;
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "News{title=" + title + ", content=" + content + ", imageUrls = " + Arrays.toString(newsPhotos) + ", createdDate = " + createdDate + "}";
    }
}
