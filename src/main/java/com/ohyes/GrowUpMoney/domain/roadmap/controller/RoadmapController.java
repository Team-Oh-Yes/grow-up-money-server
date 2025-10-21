package com.ohyes.GrowUpMoney.domain.roadmap.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roadmap")
public class RoadmapController {

    @GetMapping("/theme/{themeId}")
    public String getTheme(@PathVariable Long themeId){
        return "themeId: " + themeId;
    }

}