package com.motherskitchen.backend.Aws.S3;

import lombok.*;

@Getter
@Setter
@Builder
public class UploadDTO {
    private final String url;
    private final String key;
}
