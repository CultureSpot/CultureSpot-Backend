package com.culturespot.culturespotdomain.core.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String uploadFileName;

    @Column(nullable = false)
    private String storedFileName;

    @Builder
    public Image(Post post, String uploadFileName, String storedFileName) {
        this.post = post;
        this.uploadFileName = uploadFileName;
        this.storedFileName = storedFileName;
    }
}

