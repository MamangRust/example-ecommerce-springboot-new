package com.sanedge.ecommerce.domain.responses.merchantsociallink;

import com.sanedge.ecommerce.models.merchant.MerchantSocialMediaLink;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantSocialMediaLinkResponse {
    private Long id;
    private String platform;
    private String url;
    private String createdAt;
    private String updatedAt;

    public static MerchantSocialMediaLinkResponse from(MerchantSocialMediaLink entity) {
        return MerchantSocialMediaLinkResponse.builder()
                .id(entity.getMerchantSocialId())
                .platform(entity.getPlatform())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .build();
    }
}
