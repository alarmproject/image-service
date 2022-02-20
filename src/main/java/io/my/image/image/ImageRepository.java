package io.my.image.image;

import org.springframework.data.repository.CrudRepository;

public interface ImageRepository extends CrudRepository<Image, Long> {
    void deleteByFileName(String fileName);

}
