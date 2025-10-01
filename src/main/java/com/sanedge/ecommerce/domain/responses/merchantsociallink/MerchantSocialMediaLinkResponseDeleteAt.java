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
public class MerchantSocialMediaLinkResponseDeleteAt {
    private Long id;
    private String platform;
    private String url;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;

    public static MerchantSocialMediaLinkResponseDeleteAt from(MerchantSocialMediaLink entity) {
        return MerchantSocialMediaLinkResponseDeleteAt.builder()
                .id(entity.getMerchantSocialId())
                .platform(entity.getPlatform())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)
                .deletedAt(entity.getDeletedAt() != null ? entity.getDeletedAt().toString() : null)
                .build();
    }
}
