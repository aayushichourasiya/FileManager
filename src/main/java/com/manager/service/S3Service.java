package com.manager.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
	public String uploadFile(MultipartFile file) throws IOException;
	public String deleteFile(String fileName);
	List<String> listFiles();
    ResponseEntity<byte[]> downloadFile(String fileName) throws IOException;
}
