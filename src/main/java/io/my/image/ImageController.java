package io.my.image;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public BaseResponse<String> postImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "bucketName", required = false) String bucketName) {

        return new BaseResponse<>(imageService.postImage(file, bucketName));
    }

    @GetMapping
    public void downloadImage(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(name = "bucketName", required = false) String bucketName,
            @RequestParam("fileName") String fileName) {
        imageService.downloadImage(request, response, bucketName, fileName);
    }

    @DeleteMapping
    public BaseResponse<String> deleteImage(
            @RequestParam(name = "bucketName", required = false) String bucketName,
            @RequestParam("fileName") String fileName) {

        imageService.deleteImage(bucketName, fileName);

        return new BaseResponse<>("삭제 성공");
    }
}
