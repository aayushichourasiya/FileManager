package com.manager.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manager.service.S3Service;

@RestController
public class FileLogController {
	
	private S3Service s3Service;
	
	@Autowired
    public FileLogController(S3Service s3Service) {
        this.s3Service = s3Service;
    }
	
	@PostMapping("/s3/upload")
	public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) throws Exception{
		return ResponseEntity.ok(s3Service.uploadFile(file));
	}
	
	@DeleteMapping("/s3/delete")
	public ResponseEntity<String> deleteFile(@RequestParam String fileName){
		return ResponseEntity.ok(s3Service.deleteFile(fileName));
	}
	
	@GetMapping("/s3/files")
    public ResponseEntity<List<String>> listFiles() {
        return ResponseEntity.ok(s3Service.listFiles());
    }

    @GetMapping("/s3/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) throws IOException {
        return s3Service.downloadFile(fileName);
    }
}
