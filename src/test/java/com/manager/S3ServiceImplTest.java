package com.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.manager.model.ApplicationProperties;
import com.manager.model.FileLog;
import com.manager.repository.FileLogRepository;
import com.manager.service.S3ServiceImpl;

@ExtendWith(MockitoExtension.class)
public class S3ServiceImplTest {
	@InjectMocks
    private S3ServiceImpl s3Service;

    @Mock
    private ApplicationProperties appProperties;

    @Mock
    private FileLogRepository logRepository;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private S3Object s3Object;

    @Mock
    private S3ObjectInputStream s3ObjectInputStream;
    
    private final String bucketName = "test-bucket";
    private final String fileName = "test.txt";
    private final byte[] fileContent = "Test file content".getBytes();

    @BeforeEach
    void setup() throws IOException {
        when(appProperties.getBucketName()).thenReturn(bucketName);
        when(appProperties.getRegion()).thenReturn("ap-south-1");
        when(appProperties.getAccessKey()).thenReturn("accessKey");
        when(appProperties.getSecretKey()).thenReturn("secretKey");

        // Force PostConstruct init
        s3Service.init();
    }
    
    @Test
    void testUploadFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "text/plain", fileContent);

        String result = s3Service.uploadFile(multipartFile);

        assertEquals("File uploaded successfully: " + fileName, result);
        verify(logRepository).save(any(FileLog.class));
    }
    
    @Test
    void testDeleteFile() {
        String result = s3Service.deleteFile(fileName);

        assertEquals("File deleted successfully: " + fileName, result);
        verify(logRepository, times(1)).save(any(FileLog.class));
    }
    
    @Test
    void testListFiles() {
        S3ObjectSummary summary1 = new S3ObjectSummary();
        summary1.setKey("file1.txt");

        S3ObjectSummary summary2 = new S3ObjectSummary();
        summary2.setKey("file2.txt");

        ObjectListing listing = mock(ObjectListing.class);
        when(amazonS3.listObjects(bucketName)).thenReturn(listing);
        when(listing.getObjectSummaries()).thenReturn(List.of(summary1, summary2));

        List<String> files = s3Service.listFiles();

        assertEquals(List.of("file1.txt", "file2.txt"), files);
    }
    
    @Test
    void testDownloadFile() throws IOException {
        when(amazonS3.getObject(bucketName, fileName)).thenReturn(s3Object);
        when(s3Object.getObjectContent()).thenReturn(new S3ObjectInputStream(
                new ByteArrayInputStream(fileContent), null
        ));

        ResponseEntity<byte[]> response = s3Service.downloadFile(fileName);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileContent, response.getBody());
        assertEquals("attachment; filename=\"" + fileName + "\"", 
                     response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }


}
