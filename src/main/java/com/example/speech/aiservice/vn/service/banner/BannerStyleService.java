package com.example.speech.aiservice.vn.service.banner;

import com.example.speech.aiservice.vn.dto.response.BannerChapterStyleResponseDTO;
import com.example.speech.aiservice.vn.dto.response.BannerTitleStyleResponseDTO;
import com.example.speech.aiservice.vn.model.enums.ColorType;
import com.example.speech.aiservice.vn.model.enums.FontSizeType;
import com.example.speech.aiservice.vn.model.enums.FontType;
import com.example.speech.aiservice.vn.model.enums.LineSpacingType;
import com.example.speech.aiservice.vn.service.console.*;
import com.example.speech.aiservice.vn.service.propertie.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class BannerStyleService {

    private final PropertiesService propertiesService;

    private final BannerSettingListener bannerSettingListener;


    @Autowired
    public BannerStyleService(PropertiesService propertiesService, BannerSettingListener bannerSettingListener) {
        this.propertiesService = propertiesService;
        this.bannerSettingListener = bannerSettingListener;
    }


    public BannerTitleStyleResponseDTO bannerTitleStyleResponseDTO() {
        /**
         * setting style for title
         */
        System.out.println("\uD83C\uDF66 Title setting");
        BannerTitleStyleResponseDTO titleStyle = bannerSettingListener.promptTitleStyleFromCode();
        String titleFontPath = titleStyle.getFontPath();
        Color titleFontColor = titleStyle.getFontColor();
        Color titleBorderColor = titleStyle.getBorderColor();
        float titleFontSize = titleStyle.getFontSize();

        return new BannerTitleStyleResponseDTO(titleFontPath, titleFontColor, titleBorderColor, titleFontSize);
    }

    public BannerChapterStyleResponseDTO bannerChapterStyleResponseDTO() {
        /**
         * setting style for chapter
         */
        System.out.println("\n");
        System.out.println("\uD83C\uDF69 Chapter setting");
        BannerChapterStyleResponseDTO chapterStyle = bannerSettingListener.promptChapterStyleFromCode();
        String chapterFontPath = chapterStyle.getFontPath();
        Color chapterFontColor = chapterStyle.getFontColor();
        Color chapterBorderColor = chapterStyle.getBorderColor();
        float chapterFontSize = chapterStyle.getFontSize();
        int space = chapterStyle.getSpace();

        return new BannerChapterStyleResponseDTO(chapterFontPath, chapterFontColor, chapterBorderColor, chapterFontSize, space);
    }
}
