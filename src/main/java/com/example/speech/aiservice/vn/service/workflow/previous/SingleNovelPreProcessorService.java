package com.example.speech.aiservice.vn.service.workflow.previous;

import com.example.speech.aiservice.vn.dto.response.BannerChapterStyleResponseDTO;
import com.example.speech.aiservice.vn.dto.response.BannerTitleStyleResponseDTO;
import com.example.speech.aiservice.vn.dto.response.NovelInfoResponseDTO;
import com.example.speech.aiservice.vn.model.entity.chapter.Chapter;
import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import com.example.speech.aiservice.vn.model.entity.selenium.SeleniumConfigSingle;
import com.example.speech.aiservice.vn.service.banner.BannerStyleService;
import com.example.speech.aiservice.vn.service.console.BannerSettingListener;
import com.example.speech.aiservice.vn.service.filehandler.FileNameService;
import com.example.speech.aiservice.vn.service.image.ImageDesignService;
import com.example.speech.aiservice.vn.service.image.ImageService;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import com.example.speech.aiservice.vn.service.queue.ScanQueue;
import com.example.speech.aiservice.vn.service.executor.MyRunnableService;
import com.example.speech.aiservice.vn.service.google.GoogleChromeLauncherService;
import com.example.speech.aiservice.vn.service.repositoryService.chapter.ChapterService;
import com.example.speech.aiservice.vn.service.repositoryService.novel.NovelService;
import com.example.speech.aiservice.vn.service.repositoryService.selenium.SeleniumConfigSingleService;
import com.example.speech.aiservice.vn.service.repositoryService.track.TrackedNovelSingleService;
import com.example.speech.aiservice.vn.service.schedule.TimeDelay;
import com.example.speech.aiservice.vn.service.selenium.WebDriverLauncherService;
import com.example.speech.aiservice.vn.service.string.ChapterLinkBuilderService;
import com.example.speech.aiservice.vn.service.string.TotalChapterParse;
import com.example.speech.aiservice.vn.service.video.VideoMergerService;
import com.example.speech.aiservice.vn.service.wait.WaitService;
import com.example.speech.aiservice.vn.service.workflow.full.FullWorkFlow;
import com.example.speech.aiservice.vn.service.youtube.YoutubeUploadService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

@Service
public class SingleNovelPreProcessorService {

    private final GoogleChromeLauncherService googleChromeLauncherService;
    private final WebDriverLauncherService webDriverLauncherService;
    private final WaitService waitService;
    private final NovelService novelService;
    private final ChapterService chapterService;
    private final TrackedNovelSingleService trackedNovelService;
    private final ExecutorService executorService;
    private final ApplicationContext applicationContext;
    private final SeleniumConfigSingleService seleniumConfigSingleService;
    private final FileNameService fileNameService;
    private volatile boolean stop = false; // Volatile variable to track STOP command - true = stopping
    private volatile String imagePath = null;
    private final TaskScheduler taskScheduler;
    private volatile ScheduledFuture<?> scheduledTask;
    private final TimeDelay timeDelay;
    private final PropertiesService propertiesService;
    private final ScanQueue scanQueue;
    private final ImageService imageService;
    private final ImageDesignService imageDesignService;
    private final ChapterLinkBuilderService chapterLinkBuilderService;
    private final BannerStyleService bannerStyleService;
    private final BannerSettingListener bannerSettingListener;
    private final TotalChapterParse totalChapterParse;
    private Map<String, int[]> totalVideoMap = new LinkedHashMap<>();
    private final NavigableMap<Integer, String> videoPathMap = new ConcurrentSkipListMap<>();

    private final VideoMergerService videoMergerService;
    private final YoutubeUploadService youtubeUploadService;


    @Autowired
    public SingleNovelPreProcessorService(GoogleChromeLauncherService googleChromeLauncherService, WebDriverLauncherService webDriverLauncherService, WaitService waitService, NovelService novelService, ChapterService chapterService, TrackedNovelSingleService trackedNovelService, ApplicationContext applicationContext, SeleniumConfigSingleService seleniumConfigSingleService, FileNameService fileNameService, TaskScheduler taskScheduler, TimeDelay timeDelay, PropertiesService propertiesService, ScanQueue scanQueue, ImageService imageService, ImageDesignService imageDesignService, ChapterLinkBuilderService chapterLinkBuilderService, BannerStyleService bannerStyleService, BannerSettingListener bannerSettingListener, TotalChapterParse totalChapterParse, VideoMergerService videoMergerService, YoutubeUploadService youtubeUploadService) {
        this.googleChromeLauncherService = googleChromeLauncherService;
        this.webDriverLauncherService = webDriverLauncherService;
        this.waitService = waitService;
        this.novelService = novelService;
        this.chapterService = chapterService;
        this.trackedNovelService = trackedNovelService;
        this.applicationContext = applicationContext;
        this.seleniumConfigSingleService = seleniumConfigSingleService;
        this.fileNameService = fileNameService;
        this.taskScheduler = taskScheduler;
        this.timeDelay = timeDelay;
        this.propertiesService = propertiesService;
        this.scanQueue = scanQueue;
        this.imageService = imageService;
        this.imageDesignService = imageDesignService;
        this.chapterLinkBuilderService = chapterLinkBuilderService;
        this.bannerStyleService = bannerStyleService;
        this.bannerSettingListener = bannerSettingListener;
        this.totalChapterParse = totalChapterParse;
        this.videoMergerService = videoMergerService;
        this.youtubeUploadService = youtubeUploadService;
        this.executorService = Executors.newFixedThreadPool(3);
    }


    public void startWorkflow(long delay) {
        System.out.println("delay - " + timeDelay.getSecond() + "ms");
        timeDelay.setSecond(0);
        if (scheduledTask != null && !scheduledTask.isDone()) {
            return;
        }
        scheduledTask = taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                executeWorkflow();
                stop = false;
            }
        }, new Date(System.currentTimeMillis() + delay));
    }


    public void executeWorkflow() {

        List<SeleniumConfigSingle> threadConfigs = seleniumConfigSingleService.getAllConfigs();

        SeleniumConfigSingle defaultSeleniumConfigSingle = threadConfigs.get(0);

        WebDriver driver = null;
        Process chromeProcess = null;
        NovelInfoResponseDTO novelInfo = null;

        Scanner scanner = new Scanner(System.in);

        int totalChapterNumber = 0;
        try {

            /**
             * input link from commandlistenerapp
             */

            String inputLink = scanQueue.takeFromQueue();

            chromeProcess = googleChromeLauncherService.openGoogleChrome(defaultSeleniumConfigSingle.getPort(), defaultSeleniumConfigSingle.getSeleniumFileName());
            driver = webDriverLauncherService.initWebDriver(defaultSeleniumConfigSingle.getPort());


            novelInfo = scanNovelTitle(driver, inputLink);



            safeNovelToDatabase(novelInfo);

            totalChapterNumber = scanTotalChapterNumber(driver);

            int totalChapterPerVideo = Integer.parseInt(propertiesService.getTotalChapterPerVideo());

            totalVideoMap = getVideoChapterMap(novelInfo, totalChapterNumber, totalChapterPerVideo);

            imagePath = imageService.getValidImagePath(driver, chromeProcess, inputLink, googleChromeLauncherService, webDriverLauncherService, defaultSeleniumConfigSingle.getPort(), defaultSeleniumConfigSingle.getSeleniumFileName(), novelInfo.getTitle());

            Novel novel = novelService.findByTitle(novelInfo.getTitle());
            String title = novel.getTitle();
            imagePath = editPictureNow(totalVideoMap, title);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webDriverLauncherService.shutDown(driver);
            googleChromeLauncherService.shutdown(chromeProcess);
        }


        if (!stop) {

            System.out.println("stop is false");

            BannerTitleStyleResponseDTO bannerTitleStyleResponseDTO = bannerStyleService.bannerTitleStyleResponseDTO();
            BannerChapterStyleResponseDTO bannerChapterStyleResponseDTO = bannerStyleService.bannerChapterStyleResponseDTO();

            Novel novel = novelService.findByTitle(novelInfo.getTitle());
            Long novelId = novel.getId();

            int maxChapterNumber;
            Optional<Chapter> lastChapter = chapterService.findLastScannedChapter(novelId);
            if (lastChapter.isPresent()) {
                maxChapterNumber = lastChapter.get().getChapterNumber();
                System.out.println("\n"+"\uD83D\uDD0A Last scanned chapter: " + maxChapterNumber);
            } else {
                maxChapterNumber = 0;
                System.out.println("No chapters scanned yet");
            }


            while (true) {

                if (stop) {
                    System.out.println("STOP command received! No new tasks will be started.");
                    timeDelay.setSecond(5000);
                    return;
                }

                if (maxChapterNumber == totalChapterNumber) {
                    System.out.println("The entire story has been scanned, please enter another story link!");
                    timeDelay.setSecond(5000);
                    return;
                }

                int maxThreads = 3;
                CountDownLatch latch = new CountDownLatch(maxThreads);

                for (int i = 0; i < maxThreads; i++) {
                    SeleniumConfigSingle config = threadConfigs.get(i);

                    FullWorkFlow fullWorkFlow = applicationContext.getBean(FullWorkFlow.class);

                    /**
                     * For example: no chapter has been scanned yet, after each for loop +=1, the total scan is 3 -> continue
                     */
                    maxChapterNumber += 1;

                    if (maxChapterNumber > totalChapterNumber) {
                        latch.countDown();
                        break;
                    }

                    boolean shouldBreakOuterLoop = false;

                    if (!totalVideoMap.isEmpty()) {
                        // Compare with videoMap entries
                        for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
                            int[] range = entryTotalVideoMap.getValue();
                            int end = range[1];
                            if (maxChapterNumber > end) {
                                maxChapterNumber = maxChapterNumber - 1;
                                shouldBreakOuterLoop = true;
                                break;
                            }
                        }
                    }

                    if (shouldBreakOuterLoop) {
                        latch.countDown();
                        continue;
                    }


                    String chapterLinkToScan = chapterLinkBuilderService.buildChapterLink(novelInfo.getLink(), maxChapterNumber);
                    System.out.println("\uD83D\uDCBB Scan URL : " + chapterLinkToScan);

                    MyRunnableService myRunnableService = new MyRunnableService(fullWorkFlow, config.getPort(), config.getSeleniumFileName(), novel, chapterLinkToScan, totalChapterNumber, imagePath, bannerTitleStyleResponseDTO.getFontPath(), bannerTitleStyleResponseDTO.getFontColor(), bannerTitleStyleResponseDTO.getBorderColor(), bannerTitleStyleResponseDTO.getFontSize(), bannerChapterStyleResponseDTO.getFontPath(), bannerChapterStyleResponseDTO.getFontColor(), bannerChapterStyleResponseDTO.getBorderColor(), bannerChapterStyleResponseDTO.getFontSize(), bannerChapterStyleResponseDTO.getSpace(), videoPathMap, totalVideoMap);

                    executorService.execute(() -> {
                        try {
                            myRunnableService.run();
                        } finally {
                            latch.countDown();
                        }
                    });
                }

                try {
                    latch.await();
                } catch (InterruptedException e) {
                    System.err.println("Error completing task : " + e.getMessage());
                }

                // Compare with first video
                String videoName = null;
                String uploadVideoDirectoryPath = null;
                String novelTitle = novel.getTitle();

                if (!totalVideoMap.isEmpty()) {

                    // Compare with videoMap entries
                    for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
                        int[] range = entryTotalVideoMap.getValue();
                        int start = range[0];
                        int end = range[1];

                        if (maxChapterNumber >= end) { // ==
                            if (videoPathMap.size() != end) {
                                if (!videoPathMap.isEmpty()) {
                                    Map.Entry<Integer, String> firstEntry = videoPathMap.firstEntry();
                                    if (firstEntry != null) {
                                        String firstVideoPath = firstEntry.getValue();
                                        uploadVideoDirectoryPath = new File(firstVideoPath).getParent();

                                        File folder = new File(uploadVideoDirectoryPath);
                                        File[] allFiles = folder.listFiles((dir, name) -> name.endsWith(".mp4"));

                                        if (allFiles != null) {
                                            for (int chapterNumber = start; chapterNumber <= end; chapterNumber++) {
                                                if (videoPathMap.containsKey(chapterNumber)) {
                                                    continue; // already have then ignore
                                                }

                                                // Find files starting with chapter number
                                                for (File file : allFiles) {
                                                    String fileName = file.getName();
                                                    if (fileName.startsWith(chapterNumber + ".")) {
                                                        videoPathMap.put(chapterNumber, file.getAbsolutePath());
                                                        break; // only get the first matching file
                                                    }
                                                }

                                                // If still not found
                                                if (!videoPathMap.containsKey(chapterNumber)) {
                                                    System.err.println("❌ File not found for chapter " + chapterNumber);
                                                }
                                            }
                                        } else {
                                            System.err.println("❌ Cannot read video folder : " + uploadVideoDirectoryPath);
                                        }
                                    }
                                } else {
                                    System.err.println("❌ videoPathMap is empty, cannot deduce directory containing video");
                                }
                            }

                            System.out.println("📄 List of collected chapter videos:");
                            System.out.println("────────────────────────────────────────────");
                            for (Map.Entry<Integer, String> entry : videoPathMap.entrySet()) {
                                int chapter = entry.getKey();
                                String path = entry.getValue();
                                System.out.printf("Chapter %d → %s%n", chapter, path);
                            }
                            System.out.println("────────────────────────────────────────────");
                            System.out.println("✅ Total chapters collected: " + videoPathMap.size());


                            // Get only videos within chapter range [start, end]
                            List<String> sortedVideoPaths = new ArrayList<>();
                            for (Map.Entry<Integer, String> entryVideoPathMap : videoPathMap.entrySet()) {
                                int chapter = entryVideoPathMap.getKey();
                                if (chapter >= start && chapter <= end) {
                                    sortedVideoPaths.add(entryVideoPathMap.getValue());
                                }
                            }

                            videoName = entryTotalVideoMap.getKey();
                            try {
                                uploadVideoDirectoryPath = videoMergerService.mergeVideos(sortedVideoPaths, novel, videoName);
                            } catch (IOException | InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            // Delete merged path from map
                            for (int i = start; i <= end; i++) {
                                videoPathMap.remove(i);
                            }

                            totalVideoMap.remove(videoName);
                            System.out.println("🎯 Completed " + videoName);

                            String title = videoName;
                            String description = "\uFE0F\uD83C\uDFA7 Truyện Audio - " + novelTitle + " - " + novelInfo.getStoryTitle() + " \uD83D\uDCFC";
                            String playListName = novelTitle;

                            waitService.waitForSeconds(5);
                            youtubeUploadService.upload(novel, uploadVideoDirectoryPath, title, description, playListName, imagePath);
                            waitService.waitForSeconds(5);

                            /**
                             * delete current image path
                             */
                            deleteOldImagePath(imagePath);

                            /**
                             * change image path of range
                             */
                            imagePath = nextImagePath(totalVideoMap, novelTitle);



                            break;
                        }
                    }


                } else {
                    System.out.println("video map is null");
                }
                System.out.println("\uD83C\uDF04 Complete threads, continue scanning...");
            }
        } else {
            System.out.println("stop is true");
            stopConditions();
            timeDelay.setSecond(5000);
        }
    }

    private String editPictureNow(Map<String, int[]> totalVideoMap, String safeTitle) {

        for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
            int[] range = entryTotalVideoMap.getValue();
            int start = range[0];
            int end = range[1];
            System.out.println("\uD83D\uDE93 All range : " + start + " - " + end);
        }

        System.out.println();
        for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
            int[] range = entryTotalVideoMap.getValue();
            int start = range[0];
            int end = range[1];
            System.out.println("\uD83D\uDE93 The range at this point is : " + start + " - " + end);
            break;
        }

        String input = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter ok when you edit done yet : ");
        while (true) {
            input = scanner.nextLine();
            if (input.equals("ok")) {
                String imageDirectoryPath = propertiesService.getImageDirectory();
                String safeNovelTitle = fileNameService.sanitizeFileName(safeTitle);
                String imageDirectory = imageDirectoryPath + File.separator + safeNovelTitle;
                fileNameService.ensureDirectoryExists(imageDirectory);

                File dir = new File(imageDirectory);
                String firstPngPath = null;

                if (dir.exists() && dir.isDirectory()) {
                    File[] pngFiles = dir.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            return name.toLowerCase().endsWith(".png");
                        }
                    });

                    if (pngFiles != null && pngFiles.length > 0) {
                        firstPngPath = pngFiles[0].getAbsolutePath();
                        System.out.println("✅ First image in file : " + firstPngPath);
                        return firstPngPath.toString();
                    }
                }
                break;
            }
        }
        return null;
    }

    private String nextImagePath(Map<String, int[]> totalVideoMap, String safeTitle) {

        String imageDirectoryPath = propertiesService.getImageDirectory();
        String safeNovelTitle = fileNameService.sanitizeFileName(safeTitle);
        String imageDirectory = imageDirectoryPath + File.separator + safeNovelTitle;
        fileNameService.ensureDirectoryExists(imageDirectory);

        File dir = new File(imageDirectory);
        String firstPngPath = null;

        if (dir.exists() && dir.isDirectory()) {
            File[] pngFiles = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".png");
                }
            });

            if (pngFiles != null && pngFiles.length > 0) {
                firstPngPath = pngFiles[0].getAbsolutePath();
                System.out.println("✅ First image in file : " + firstPngPath);
                return firstPngPath.toString();
            }
        }


        for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
            int[] range = entryTotalVideoMap.getValue();
            int start = range[0];
            int end = range[1];
            System.out.println("\uD83D\uDE93 All range : " + start + " - " + end);
        }

        System.out.println();
        for (Map.Entry<String, int[]> entryTotalVideoMap : totalVideoMap.entrySet()) {
            int[] range = entryTotalVideoMap.getValue();
            int start = range[0];
            int end = range[1];
            System.out.println("\uD83D\uDE93 The range at this point is : " + start + " - " + end);
            break;
        }
        String input = null;
        Scanner scanner = new Scanner(System.in);
        System.out.println("\uD83E\uDD20 Enter ok when you have new picture because you don't have a picture now ! : ");
        while (true) {
            input = scanner.nextLine();
            if (input.equals("ok")) {
                break;
            }
        }
        File[] updatedPngFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".png");
            }
        });

        if (updatedPngFiles != null && updatedPngFiles.length > 0) {
            String newFirstImage = updatedPngFiles[0].getAbsolutePath();
            System.out.println("✅ New image selected after ok: " + newFirstImage);
            return newFirstImage;
        } else {
            System.out.println("❌ No PNG file found after ok.");
            return null;
        }
    }

    private void deleteOldImagePath(String imagePath) {
        System.out.println("\uD83D\uDC4A Delete this shit : " + imagePath);
        new File(imagePath).delete();
    }

    private void safeNovelToDatabase(NovelInfoResponseDTO novelInfo) {
        Novel novel = new Novel(novelInfo.getTitle(), novelInfo.getLink());
        if (!novelService.isNovelExists(novel.getTitle())) {
            novelService.saveNovel(novel);
        } else {
            System.out.println("\uD83D\uDC80 " + novelInfo.getTitle() + " already exists in the database");
        }
    }

    private Map<String, int[]> getVideoChapterMap(NovelInfoResponseDTO novelInfoResponseDTO, int totalChapters,
                                                  int chaptersPerVideo) {
        Map<String, int[]> map = new LinkedHashMap<>(); // new local map

        int totalVideos = (int) Math.ceil((double) totalChapters / chaptersPerVideo);

        String title = novelInfoResponseDTO.getTitle();
        for (int i = 0; i < totalVideos; i++) {
            int start = i * chaptersPerVideo + 1;
            int end = Math.min((i + 1) * chaptersPerVideo, totalChapters);

            Novel novel = novelService.findByTitle(novelInfoResponseDTO.getTitle());
            boolean chapter = chapterService.isExistsByNovelAndChapterNumber(novel, end);
            if (chapter) {
                continue;
            }

            String videoName = title + " || Chương " + start + " - " + end;
            map.put(videoName, new int[]{start, end});
        }

        return map;
    }


    private NovelInfoResponseDTO scanNovelTitle(WebDriver driver, String inputLink) {
        scanQueue.printQueue();
        try {
            driver.get(inputLink);

            /**
             * wait for translate
             */
            waitService.waitForSeconds(3);

            WebElement titleElement = driver.findElement(By.cssSelector("body > main > div:nth-child(1) > div.novel > div.n-text > h1 > font > font"));
            String title = titleElement.getText().trim();
            String safeTitle = title.split(" - ", 2)[0].trim();
            System.out.println("Title :" + safeTitle);


            System.out.println("Enter your YouTube description (type 'ok' on a new line to finish):");
            Scanner scanner = new Scanner(System.in);
            StringBuilder descriptionBuilder = new StringBuilder();

            while (true) {
                String line = scanner.nextLine();
                if (line.equalsIgnoreCase("ok")) break;
                descriptionBuilder.append(line).append(System.lineSeparator());
            }

            String storyTitle = descriptionBuilder.toString().trim();

            System.out.println("\n📄 YouTube Description:");
            System.out.println(storyTitle);

            if (storyTitle.isEmpty()) {
                return new NovelInfoResponseDTO(safeTitle, null, inputLink);
            }

            return new NovelInfoResponseDTO(safeTitle, storyTitle, inputLink);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int scanTotalChapterNumber(WebDriver driver) {
        return totalChapterParse.parse(driver);
    }

    public void stopConditions() {
        trackedNovelService.clearTracking();
        imagePath = null;
        stop = true;
        videoPathMap.clear();
        totalVideoMap.clear();
    }
}
