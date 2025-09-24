package dev.aniketkadam.annapoorna.file;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAssetResponse {

    private String id;
    private String url;
    private String secureUrl;
}
