package io.my.image;

import io.my.image.image.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HealthcheckController {
    private final ImageRepository imageRepository;

    @GetMapping("/healthcheck")
    public ResponseEntity<String> healthcheck() {
        log.info("is it {}", imageRepository.findById(0L));

        return ResponseEntity.ok("image service health");
    }
}
