package io.my.image.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import io.my.image.common.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final AmazonS3 amazonS3;
    private final AwsS3Properties properties;
    private final ImageRepository imageRepository;

    public PostResponseBody postImage(MultipartFile file, String bucketName) {
        if (bucketName == null) bucketName = properties.getBucketName();

        String fileName = makeFileName(file.getOriginalFilename());

        ObjectMetadata objectMetadata = buildObjectMetadata(file, fileName);

        PostResponseBody responseBody = new PostResponseBody();
        try {
            amazonS3.putObject(new PutObjectRequest(
                    bucketName,
                    fileName,
                    file.getInputStream(),
                    objectMetadata));

            Image entity = new Image();
            entity.setFileName(fileName);
            imageRepository.save(entity);
            responseBody.setId(entity.getId());
            responseBody.setFileName(fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBody;
    }

    private String makeFileName(String originalFileName) {
        return UUID.randomUUID() + "_" + originalFileName;
    }

    private ObjectMetadata buildObjectMetadata(MultipartFile file, String fileName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setHeader("filename", fileName);
        return objectMetadata;
    }

    public void downloadImage(
            HttpServletRequest request,
            HttpServletResponse response,
            String bucketName, String fileName) {
        if (bucketName == null) bucketName = properties.getBucketName();
        S3Object s3Object = amazonS3.getObject(bucketName, fileName);

        S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
        ObjectMetadata objectMetadata = s3Object.getObjectMetadata();

        OutputStream os = null;
        FileInputStream fis = null;

        try {
            byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
            fileName=  getEncodedFilename(request, fileName);

            response.setContentType(objectMetadata.getContentType());
            response.setHeader("Content-Transfer-Encoding", objectMetadata.getContentEncoding());
            response.setHeader( "Content-Disposition", "attachment; filename=\"" + fileName + "\";" );
            response.setHeader("Content-Length", String.valueOf(objectMetadata.getContentLength()));
            response.setHeader("Set-Cookie", "fileDownload=true; path=/");
            FileCopyUtils.copy(bytes, response.getOutputStream());
        } catch(IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            assert false;
            try {
                fis.close();
            } catch (IOException e) {
                log.debug(e.getMessage(), e);
            }
            try {
                os.close();
            } catch (IOException e) {
                log.debug(e.getMessage(), e);
            }
        }


    }
    private String getEncodedFilename(HttpServletRequest request, String displayFileName) throws UnsupportedEncodingException {
        String header = request.getHeader("User-Agent");

        String encodedFilename;

        if (header.contains("MSIE")) {
            encodedFilename = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (header.contains("Trident")) {
            encodedFilename = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        } else if (header.contains("Chrome")) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < displayFileName.length(); i++) {
                char c = displayFileName.charAt(i);
                if (c > '~') {
                    stringBuilder.append(URLEncoder.encode("" + c, StandardCharsets.UTF_8));
                } else {
                    stringBuilder.append(c);
                }
            }
            encodedFilename = stringBuilder.toString();
        } else if (header.contains("Opera")) {
            encodedFilename = decodeString(displayFileName);
        } else if (header.contains("Safari")) {
            encodedFilename = URLDecoder.decode(decodeString(displayFileName), StandardCharsets.UTF_8);
        } else {
            encodedFilename = URLDecoder.decode(decodeString(displayFileName), StandardCharsets.UTF_8);
        }
        return encodedFilename;

    }

    private String decodeString(String displayFileName) throws UnsupportedEncodingException {
        return "\"" + new String(displayFileName.getBytes(StandardCharsets.UTF_8), "8859_1") + "\"";
    }

    @Transactional
    public void deleteImage(String bucketName, String fileName) {
        if (bucketName == null) bucketName = properties.getBucketName();
        imageRepository.deleteByFileName(fileName);
        amazonS3.deleteObject(bucketName, fileName);
    }


}
