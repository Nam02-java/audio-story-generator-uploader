package com.example.speech.aiservice.vn.service.executor;

import com.example.speech.aiservice.vn.model.entity.chapter.Chapter;
import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import com.example.speech.aiservice.vn.service.workflow.full.FullWorkFlow;

import java.awt.*;
import java.util.*;
import java.util.List;

public class MyRunnableService implements Runnable {
    private final FullWorkFlow fullWorkFlow;
    private final String port;
    private final String seleniumFileName;
    private final Novel novel;
    private final String chapterLinkToScan;
    private final int totalChapterNumber;
    private final String imagePath;
    private final String titleFontPath;
    private final Color titleFontColor;
    private final Color titleBorderColor;
    private final float titleFontSize;
    private final String chapterFontPath;
    private final Color chapterFontColor;
    private final Color chapterBorderColor;
    private final float chapterFontSize;
    private final int size;
    private final Map<Integer, String> videoPathMap;
    private Map<String, int[]> totalVideoMap = new LinkedHashMap<>();

    public MyRunnableService(
            FullWorkFlow fullWorkFlow,
            String port,
            String seleniumFileName,
            Novel novel,
            String chapterLinkToScan,
            int totalChapterNumber,
            String imagePath,
            String titleFontPath,
            Color titleFontColor,
            Color titleBorderColor,
            float titleFontSize,
            String chapterFontPath,
            Color chapterFontColor,
            Color chapterBorderColor,
            float chapterFontSize,
            int size,
            Map<Integer, String> videoPathMap,
            Map<String, int[]> totalVideoMap


    ) {
        this.fullWorkFlow = fullWorkFlow;
        this.port = port;
        this.seleniumFileName = seleniumFileName;
        this.novel = novel;
        this.chapterLinkToScan = chapterLinkToScan;
        this.totalChapterNumber = totalChapterNumber;
        this.imagePath = imagePath;
        this.titleFontPath = titleFontPath;
        this.titleFontColor = titleFontColor;
        this.titleBorderColor = titleBorderColor;
        this.titleFontSize = titleFontSize;
        this.chapterFontPath = chapterFontPath;
        this.chapterFontColor = chapterFontColor;
        this.chapterBorderColor = chapterBorderColor;
        this.chapterFontSize = chapterFontSize;
        this.size = size;
        this.videoPathMap = videoPathMap;
        this.totalVideoMap = totalVideoMap;
    }

    @Override
    public void run() {
        System.out.println("▶️ Running on thread: " + Thread.currentThread().getId());

        fullWorkFlow.runProcess(
                port,
                seleniumFileName,
                novel,
                chapterLinkToScan,
                totalChapterNumber,
                imagePath,
                titleFontPath,
                titleFontColor,
                titleBorderColor,
                titleFontSize,
                chapterFontPath,
                chapterFontColor,
                chapterBorderColor,
                chapterFontSize,
                size,
                videoPathMap,
                totalVideoMap
        );
    }
}

