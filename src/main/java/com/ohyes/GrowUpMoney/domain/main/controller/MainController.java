package com.ohyes.GrowUpMoney.domain.main.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {

    @GetMapping("/banners")
    public String getBanners(){
        return "banners image";
    }

    @GetMapping("intro")
    public String getIntro(){
        return "Grow Money introduction";
    }

}
