package org.foxesworld.launchserver.news;

import com.vk.api.sdk.callback.longpoll.CallbackApiLongPoll;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.photos.Photo;
import com.vk.api.sdk.objects.photos.PhotoSizes;
import com.vk.api.sdk.objects.wall.Wallpost;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.foxesworld.launcher.news.News;
import org.foxesworld.launcher.news.NewsPhoto;
import org.foxesworld.launchserver.LaunchServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class VkNewsProvider extends NewsProvider {

    private transient final Logger logger = LogManager.getLogger();
    private transient final List<News> newsList = new ArrayList<>();
    private transient final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private transient VkApiClient vkApiClient;
    private transient GroupActor groupActor;
    private transient ServiceActor serviceActor;
    public Integer appId;
    public String serviceToken;
    public Integer groupId;
    public String accessToken;
    public Integer maxNews;
    public String domain;

    public VkNewsProvider() {
    }

    @Override
    public void init(LaunchServer launchServer) {
        verify();
        TransportClient transportClient = new HttpTransportClient();
        vkApiClient = new VkApiClient(transportClient);
        groupActor = new GroupActor(groupId, accessToken);
        serviceActor = new ServiceActor(appId, serviceToken);
        executorService.execute(() -> {
            try {
                new CallbackApiLongPoll(vkApiClient, groupActor) {

                    @Override
                    public void wallPostNew(Integer groupId, Wallpost wallpost) {
                        processWallpost(wallpost);
                        logger.info("Posted news fetched");
                    }
                }.run();
            } catch (ClientException | ApiException e) {
                e.printStackTrace();
            }
        });
        executorService.execute(this::fetchNews);
    }

    public void fetchNews() {
        long startTimeMillis = System.currentTimeMillis();
        logger.info("Fetching news..");
        try {
            vkApiClient.wall().get(serviceActor).domain(domain).count(maxNews).execute().getItems().forEach(this::processWallpost);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        long fetchingTimeInMillis = System.currentTimeMillis() - startTimeMillis;
        logger.info("News fetched in " + fetchingTimeInMillis + " millis");
    }

    private void verify() {
        verifyNonNull(domain, "domain");
        verifyNonNull(serviceToken, "serviceToken");
        verifyNonNull(appId, "appId");
        verifyNonNull(groupId, "groupId");
        verifyNonNull(accessToken, "accessToken");
        verifyNonNull(maxNews, "maxNews");
    }

    private void verifyNonNull(Object object, String objectName) {
        if (object == null) {
            throw new RuntimeException("[Verify][NewsProvider] " + objectName + " can't be null");
        }
    }

    @Override
    public List<News> getNews() {
        return new ArrayList<>(newsList);
    }

    private void processNews(News news) {
        while (newsList.size() + 1 > maxNews) {
            newsList.remove(0);
        }

        newsList.add(news);
    }

    private void processWallpost(Wallpost wallpost) {
        String content = wallpost.getText();
        String title;
        int newLineIndex = content.indexOf("\n");
        if (newLineIndex > 0) {
            title = content.substring(0, newLineIndex);
            content = content.substring(newLineIndex + 2);
        } else {
            title = "";
        }
        List<NewsPhoto> newsPhotos = new ArrayList<>();
        List<WallpostAttachment> attachments = wallpost.getAttachments();
        if (attachments != null) {
            for (WallpostAttachment attachment : attachments) {
                Photo photo = attachment.getPhoto();
                if (photo == null) {
                    continue;
                }
                List<PhotoSizes> photoSizes = photo.getSizes();
                if (photoSizes == null) {
                    continue;
                }
                NewsPhoto newsPhoto = new NewsPhoto();
                for (PhotoSizes photoSize : photoSizes) {
                    try {
                        newsPhoto.put(NewsPhoto.Size.valueOf(photoSize.getType().name()), photoSize.getUrl().getPath());
                    } catch (IllegalArgumentException e) {
                        // It's okay
                    }
                }
                newsPhotos.add(newsPhoto);
            }
        }
        News news = new News(title, content, newsPhotos.toArray(new NewsPhoto[0]), wallpost.getDate());
        processNews(news);
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }

    @Override
    public void sync() {
        newsList.clear();
        executorService.execute(this::fetchNews);
    }
}
