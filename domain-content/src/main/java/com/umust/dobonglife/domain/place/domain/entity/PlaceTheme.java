package com.umust.dobonglife.domain.place.domain.entity;

import com.umust.dobonglife.domain.place.domain.vo.Theme;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_themes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaceTheme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_theme_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Theme theme;

    public static PlaceTheme of(Theme theme) {
        PlaceTheme placeTheme = new PlaceTheme();
        placeTheme.theme = theme;
        return placeTheme;
    }
}
