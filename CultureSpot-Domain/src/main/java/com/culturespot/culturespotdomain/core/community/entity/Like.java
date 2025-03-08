package com.culturespot.culturespotdomain.core.community.entity;

import com.culturespot.culturespotdomain.core.user.entity.User;
import jakarta.persistence.*;

@Entity
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long likeId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;
}
