package com.culturespot.culturespotdomain.core.community.repository;

import com.culturespot.culturespotdomain.core.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
