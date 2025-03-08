package com.culturespot.culturespotdomain.core.community.entity;

import com.culturespot.culturespotdomain.core.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)  // Auditing 활성화
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @Setter
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    @Setter
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long viewCount = 0L;

    @CreatedDate
    @Column(updatable = false, nullable = false)  // 생성일 수정 불가
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    @Builder
    public Post(User user, List<Image> images, String title, String content) {
        this.user = user;
        this.images = (images != null) ? images : new ArrayList<>(); // 🚀 null 방지
        this.title = title;
        this.content = content;
    }
}

