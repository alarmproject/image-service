package io.my.image.bucket;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BucketService {
    private final AmazonS3 amazonS3;

    public void createBucket(String bucketName) {
        amazonS3.createBucket(bucketName);
    }

    public void deleteBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName);
    }



}
