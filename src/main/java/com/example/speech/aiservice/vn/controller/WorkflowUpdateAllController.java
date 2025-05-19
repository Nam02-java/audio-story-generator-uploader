package com.example.speech.aiservice.vn.controller;

import com.example.speech.aiservice.vn.model.entity.novel.Novel;
import com.example.speech.aiservice.vn.service.queue.ScanQueue;
import com.example.speech.aiservice.vn.service.repositoryService.novel.NovelService;
import com.example.speech.aiservice.vn.service.workflow.previous.SingleNovelPreProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workflow")
public class WorkflowUpdateAllController {

    private final SingleNovelPreProcessorService singleNovelPreProcessorService;
    private final ScanQueue scanQueue;
    private final NovelService novelService;

    @Autowired
    public WorkflowUpdateAllController(SingleNovelPreProcessorService singleNovelPreProcessorService, ScanQueue scanQueue, NovelService novelService) {
        this.singleNovelPreProcessorService = singleNovelPreProcessorService;
        this.scanQueue = scanQueue;
        this.novelService = novelService;
    }


    @PostMapping("/update-all")
    public ResponseEntity<String> updateAllWorkflow() {
        System.out.println("Received UPDATE ALL request!");

        List<Novel> novelList = novelService.findAllNovels();

        if (!novelList.isEmpty()) {
            for (Novel novel : novelList) {
                scanQueue.addToQueue(novel.getLink());
            }
            return ResponseEntity.ok("Added " + novelList.size() + " novels to the scan queue!");
        } else {
            return ResponseEntity.ok("No novels found to update!");
        }
    }
}
