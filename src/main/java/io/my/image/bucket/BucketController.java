package io.my.image.bucket;

import io.my.image.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bucket")
public class BucketController {
    private final BucketService bucketService;

    @PostMapping("/{bucketName}")
    public BaseResponse<String> createBucket(@PathVariable("bucketName") String bucketName) {
        bucketService.createBucket(bucketName);
        return new BaseResponse<>(bucketName);
    }

    @DeleteMapping("/{bucketName}")
    public BaseResponse<String> deleteBucket(@PathVariable("bucketName") String bucketName) {
        bucketService.deleteBucket(bucketName);
        return new BaseResponse<>(bucketName);
    }

}
