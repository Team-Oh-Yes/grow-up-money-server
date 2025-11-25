package com.ohyes.GrowUpMoney.domain.ranking.controller;

import com.ohyes.GrowUpMoney.domain.ranking.domain.RankingSnapshot;
import com.ohyes.GrowUpMoney.domain.ranking.entity.RankUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RankingController {
@GetMapping("/rank")
    public String rankList(@RequestBody RankingSnapshot request){

    }
}
