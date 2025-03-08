package com.culturespot.culturespotdomain.core.community.dto;

import lombok.Builder;

@Builder
public record PostDetails(
        String title,
        String content
){
}
