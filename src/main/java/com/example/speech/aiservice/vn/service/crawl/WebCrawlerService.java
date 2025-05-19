package com.example.speech.aiservice.vn.service.crawl;

import com.example.speech.aiservice.vn.dto.response.WebCrawlResponseDTO;
import com.example.speech.aiservice.vn.model.entity.chapter.Chapter;
import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import com.example.speech.aiservice.vn.service.filehandler.FileNameService;
import com.example.speech.aiservice.vn.service.filehandler.FileWriterService;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import com.example.speech.aiservice.vn.service.repositoryService.chapter.ChapterService;
import com.example.speech.aiservice.vn.service.string.ChapterLinkBuilderService;
import com.example.speech.aiservice.vn.service.wait.WaitService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class WebCrawlerService {
    private final FileNameService fileNameService;
    private final FileWriterService fileWriterService;
    private final WaitService waitService;
    private final PropertiesService propertiesService;

    private final ChapterService chapterService;

    private final ChapterLinkBuilderService chapterLinkBuilderService;

    @Autowired
    public WebCrawlerService(FileNameService fileNameService, FileWriterService fileWriterService, WaitService waitService, PropertiesService propertiesService, ChapterService chapterService, ChapterLinkBuilderService chapterLinkBuilderService) {
        this.fileNameService = fileNameService;
        this.fileWriterService = fileWriterService;
        this.waitService = waitService;
        this.propertiesService = propertiesService;
        this.chapterService = chapterService;
        this.chapterLinkBuilderService = chapterLinkBuilderService;
    }

    public WebCrawlResponseDTO webCrawlResponseDTO(WebDriver driver, Novel novel, String chapterLinkToScan) throws InterruptedException {

        String contentDirectoryPath = propertiesService.getContentDirectory();
        String contentFileExtension = propertiesService.getContentFileExtension();

        driver.get(chapterLinkToScan);

        waitService.waitForSeconds(5); // Wait for the translation to be complete

        String pageSource = driver.getPageSource();

        Document doc = Jsoup.parse(pageSource);

        final int MAX_RETRIES = 10;
        int attempt = 0;
        String title = null;

        while (attempt < MAX_RETRIES) {
            attempt++;
            System.out.println("🌀 Attempt " + attempt + " / " + MAX_RETRIES);
            title = doc.select("#page > article > h3").text();

            if (title != null && !title.isBlank()) {
                System.out.println("✅ Found title: " + title);
                break;
            }

            System.err.println("⚠️ Title empty, retrying..." + " " + chapterLinkToScan);
            driver.navigate().refresh();
            waitService.waitForSeconds(5);
            pageSource = driver.getPageSource();
            doc = Jsoup.parse(pageSource);
        }

        int chapterNumber = chapterLinkBuilderService.extractChapterNumberFromUrl(driver.getCurrentUrl());
        String content = doc.select("#page > article").text(); //#svelte > div.tm-light.rd-ff-0.rd-fs-3.svelte-19vhflx > main > article:nth-child(6)

        String noChinese = content.replaceAll("\\p{IsHan}", "");
        String cleanedContent = noChinese.replaceAll("[^a-zA-ZÀ-ỹ0-9,. ]+", "");
        cleanedContent = cleanedContent.replaceAll("\\s+", " ").trim();

        System.out.println("Title : " + title);
        System.out.println("Chapter number : " + chapterNumber);
        System.out.println("Content : " + cleanedContent);



        // Create a folder for the collection if it does not exist.
        String safeNovelTitle = fileNameService.sanitizeFileName(novel.getTitle());
        String novelDirectory = contentDirectoryPath + File.separator + safeNovelTitle;
        fileNameService.ensureDirectoryExists(novelDirectory);

        // Handling valid chapter file names
        String safeChapterTitle = fileNameService.sanitizeFileName(title) + contentFileExtension;
        String contentFilePath = fileNameService.getAvailableFileName(novelDirectory, safeChapterTitle, contentFileExtension);

        // Write content to file
        fileWriterService.writeToFile(contentFilePath, cleanedContent);


        return new WebCrawlResponseDTO("Crawling completed", title, chapterNumber, chapterLinkToScan, contentFilePath);
    }
}


/**
 * researching
 */
//WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
//
//        wait.until(new Function<WebDriver, Boolean>() {
//            @Override
//            public Boolean apply(WebDriver d) {
//                return ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete");
//            }
//        });

class demo{
    public static void main(String[] args) {

    }
}