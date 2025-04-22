package com.manager.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.manager.model.ApplicationProperties;
import com.manager.model.FileLog;
import com.manager.repository.FileLogRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class S3ServiceImpl implements S3Service{
	
	@Autowired
	ApplicationProperties appProperties;
	
	private final FileLogRepository logRepository;
	
	private S3Client s3;
	
	private final AmazonS3 amazonS3;
	
	@PostConstruct
    public void init() {
        s3 = S3Client.builder()
                .region(Region.of(appProperties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(appProperties.getAccessKey(), appProperties.getSecretKey()))
                )
                .build();
    }

	@Override
	public String uploadFile(MultipartFile file) throws IOException {
		String fileName = file.getOriginalFilename();
		PutObjectRequest putRequest = PutObjectRequest.builder()
										.bucket(appProperties.getBucketName())
										.key(fileName)
										.build();
		
		s3.putObject(putRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
        saveLog(fileName, "UPLOAD");
        
		return "File uploaded successfully: " + fileName;
	}

	@Override
	public String deleteFile(String fileName) {
		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(appProperties.getBucketName())
                .key(fileName)
                .build();

        s3.deleteObject(deleteRequest);
        saveLog(fileName, "DELETE");
        return "File deleted successfully: " + fileName;
	}
	
	@Override
    public List<String> listFiles() {
        ObjectListing objectListing = amazonS3.listObjects(appProperties.getBucketName());
        return objectListing.getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
	
	@Override
    public ResponseEntity<byte[]> downloadFile(String fileName) throws IOException {
    	// Get the file from S3 bucket
        S3Object s3Object = amazonS3.getObject(appProperties.getBucketName(), fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        // Write the content to a byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        
        // Read and write the content of the file
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // Set the correct headers for downloading the file
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Ensures it's treated as binary
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(fileName).build());

        // Return the file as byte array along with headers
        return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);
    }
	
	private void saveLog(String fileName, String operation) {
        FileLog log = new FileLog();
        log.setFileName(fileName);
        log.setOperation(operation);
        log.setTimeStamp(LocalDateTime.now());
        logRepository.save(log);
    }
}
