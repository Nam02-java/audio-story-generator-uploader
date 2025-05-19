package com.example.speech.aiservice.vn.service.workflow.full;

import com.example.speech.aiservice.vn.dto.response.*;
import com.example.speech.aiservice.vn.model.entity.chapter.Chapter;
import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import com.example.speech.aiservice.vn.service.account.LoginCheckerService;
import com.example.speech.aiservice.vn.service.account.LoginService;
import com.example.speech.aiservice.vn.service.image.ImageDesignService;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import com.example.speech.aiservice.vn.service.repositoryService.chapter.ChapterService;
import com.example.speech.aiservice.vn.service.crawl.WebCrawlerService;
import com.example.speech.aiservice.vn.service.google.GoogleChromeLauncherService;
import com.example.speech.aiservice.vn.service.repositoryService.track.TrackUploadService;
import com.example.speech.aiservice.vn.service.selenium.WebDriverLauncherService;
import com.example.speech.aiservice.vn.service.speech.SpeechService;
import com.example.speech.aiservice.vn.service.video.VideoCreationService;
import com.example.speech.aiservice.vn.service.youtube.YoutubeUploadService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype") // Create a new instance every time you call
public class FullWorkFlow {
    private final GoogleChromeLauncherService googleChromeLauncherService;
    private final WebDriverLauncherService webDriverLauncherService;
    private final LoginCheckerService loginCheckerService;
    private final LoginService loginService;
    private final WebCrawlerService webCrawlerService;
    private final SpeechService speechService;
    private final VideoCreationService videoCreationService;
    private final YoutubeUploadService youtubeUploadService;
    private final ChapterService chapterService;
    private final TrackUploadService trackUploadService;
    private final PropertiesService propertiesService;
    private final ImageDesignService imageDesignService;

    // Constructor Injection
    @Autowired
    public FullWorkFlow(GoogleChromeLauncherService googleChromeLauncherService, WebDriverLauncherService webDriverLauncherService, LoginCheckerService loginCheckerService, LoginService loginService, WebCrawlerService webCrawlerService, SpeechService speechService, VideoCreationService videoCreationService, YoutubeUploadService youtubeUploadService, ChapterService chapterService, TrackUploadService trackUploadService, PropertiesService propertiesService, ImageDesignService imageDesignService) {
        this.googleChromeLauncherService = googleChromeLauncherService;
        this.webDriverLauncherService = webDriverLauncherService;
        this.loginCheckerService = loginCheckerService;
        this.loginService = loginService;
        this.webCrawlerService = webCrawlerService;
        this.speechService = speechService;
        this.videoCreationService = videoCreationService;
        this.youtubeUploadService = youtubeUploadService;
        this.chapterService = chapterService;
        this.trackUploadService = trackUploadService;
        this.propertiesService = propertiesService;
        this.imageDesignService = imageDesignService;
    }

    public void runProcess(String port, String seleniumFileName, Novel novel, String chapterLinkToScan, int totalChapterNumber, String imagePath,
                           String titleFontPath, Color titleFontColor, Color titleBorderColor, float titleFontSize,
                           String chapterFontPath, Color chapterFontColor, Color chapterBorderColor, float chapterFontSize, int size,
                           Map<Integer, String> videoPathMap,Map<String, int[]> totalVideoMap
    ) {
        FullProcessResponseDTO fullProcessResponseDTO = fullProcessResponseDTO(
                port, seleniumFileName, novel, chapterLinkToScan, totalChapterNumber, imagePath,
                titleFontPath, titleFontColor, titleBorderColor, titleFontSize,
                chapterFontPath, chapterFontColor, chapterBorderColor, chapterFontSize, size,
                videoPathMap,totalVideoMap
        );
    }

    private FullProcessResponseDTO fullProcessResponseDTO(String port, String seleniumFileName, Novel novel, String chapterLinkToScan, int totalChapterNumber, String imagePath,
                                                          String titleFontPath, Color titleFontColor, Color titleBorderColor, float titleFontSize,
                                                          String chapterFontPath, Color chapterFontColor, Color chapterBorderColor, float chapterFontSize, int size,
                                                          Map<Integer, String> videoPathMap,Map<String, int[]> totalVideoMap) {
        WebDriver chromeDriver = null;
        Process chromeProcess = null;
        try {

            chromeProcess = googleChromeLauncherService.openGoogleChrome(port, seleniumFileName);
            chromeDriver = webDriverLauncherService.initWebDriver(port);

            // Crawl data on Chivi.App website
            WebCrawlResponseDTO webCrawlResponseDTO = webCrawlerService.webCrawlResponseDTO(chromeDriver, novel, chapterLinkToScan);

            if (!chapterService.isExistsByNovelAndChapterNumber(novel, webCrawlResponseDTO.getChapterNumber())) {
                chapterService.addChapter(novel, webCrawlResponseDTO.getChapterNumber(), webCrawlResponseDTO.getTitle(), chapterLinkToScan);
            } else {
                System.out.println(String.format("😢 %s (Chapter: %s) - URL: %s already exists in the database.",
                        webCrawlResponseDTO.getTitle(),
                        webCrawlResponseDTO.getChapterNumber(),
                        webCrawlResponseDTO.getUrlWebsite()));

            }

            Chapter chapter = chapterService.getChapterByLink(chapterLinkToScan);

            // Convert text to speech with ADMICRO | Vietnamese Speech Synthesis
            TextToSpeechResponseDTO textToSpeechResponseDTO = speechService.textToSpeechResponseDTO(chromeDriver, webCrawlResponseDTO.getContentFilePath(), novel, chapter);
            /**
             * demo
             */
            webDriverLauncherService.shutDown(chromeDriver);
            googleChromeLauncherService.shutdown(chromeProcess);

            /**
             * create chapter image path
             */

            String chapterImagePath = imageDesignService.execute(imagePath, novel, chapter,
                    titleFontPath, titleFontColor, titleBorderColor, titleFontSize,
                    chapterFontPath, chapterFontColor, chapterBorderColor, chapterFontSize, size,
                    totalVideoMap);

//            String chapterImagePath = imageDesignService.execute(imagePath, novel, chapter,
//                    titleFontPath, titleFontColor, titleBorderColor, titleFontSize,
//                    chapterFontPath, chapterFontColor, chapterBorderColor, chapterFontSize, size);
            // Create videos using mp4 files combined with photos
            CreateVideoResponseDTO createVideoResponseDTO = videoCreationService.createVideoResponseDTO(textToSpeechResponseDTO.getFilePath(), chapterImagePath, novel, chapter);

            int chapterNumber = chapter.getChapterNumber();
            String videoFilePath = createVideoResponseDTO.getCreatedVideoFilePath();
            videoPathMap.put(chapterNumber, videoFilePath);

            //Upload video to youtube with youtube data API
            //YoutubeUploadResponseDTO youtubeUploadResponseDTO = youtubeUploadService.upload(createVideoResponseDTO.getCreatedVideoFilePath(), novel, chapter,totalChapterNumber);

            //Aggregated DTO response
            FullProcessResponseDTO fullProcessResponse = new FullProcessResponseDTO(null, webCrawlResponseDTO, textToSpeechResponseDTO, createVideoResponseDTO, null);

            chapterService.markChapterAsScanned(chapter);

            return fullProcessResponse;

        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            webDriverLauncherService.shutDown(chromeDriver);
            googleChromeLauncherService.shutdown(chromeProcess);
        }
    }

    private void safeNovelToDatabase(NovelInfoResponseDTO novelInfo) {

//        Novel novel = new Novel(novelInfo.getTitle(), novelInfo.getLink());
//        if (!novelService.isNovelExists(novel.getTitle())) {
//            novelService.saveNovel(novel);
//        } else {
//            System.out.println("\uD83D\uDC80 " + novelInfo.getTitle() + " already exists in the database");
//        }
    }
}

