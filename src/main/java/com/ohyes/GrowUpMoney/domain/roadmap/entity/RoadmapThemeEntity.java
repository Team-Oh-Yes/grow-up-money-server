package com.ohyes.GrowUpMoney.domain.roadmap.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoadmapThemeEntity {
    @Id
    private Long theme_id;
}
