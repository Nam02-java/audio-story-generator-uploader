package com.example.speech.aiservice.vn.service.youtube;

import com.example.speech.aiservice.vn.service.google.GoogleChromeLauncherService;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import com.example.speech.aiservice.vn.service.selenium.WebDriverLauncherService;
import com.example.speech.aiservice.vn.service.wait.WaitService;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * demo - reschearing
 */
@Service
public class YoutubeViewService {

    private final GoogleChromeLauncherService chromeLauncher;

    private final WebDriverLauncherService webDriverLauncher;

    private final PropertiesService propertiesService;

    private final WaitService waitService;

    @Autowired
    public YoutubeViewService(GoogleChromeLauncherService chromeLauncher, WebDriverLauncherService webDriverLauncher, PropertiesService propertiesService, WaitService waitService) {
        this.chromeLauncher = chromeLauncher;
        this.webDriverLauncher = webDriverLauncher;
        this.propertiesService = propertiesService;
        this.waitService = waitService;
    }


//    public void watchYoutube(String port, String seleniumFileName, String videoUrl) {
//        WebDriver driver = null;
//
//        try {
//            chromeLauncher.openGoogleChrome(port, seleniumFileName);
//            driver = webDriverLauncher.initWebDriver(port);
//
//            driver.get(videoUrl);
//
//            waitService.waitForSeconds(45);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            webDriverLauncher.shutDown(driver);
//            chromeLauncher.shutdown();
//        }
//    }
}
