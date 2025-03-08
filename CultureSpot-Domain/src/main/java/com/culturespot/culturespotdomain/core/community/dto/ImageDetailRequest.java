package com.culturespot.culturespotdomain.core.community.dto;

import lombok.Builder;

@Builder
public record ImageDetailRequest(
    Long imageId,
    String imageUrl
){
}
