package com.manager.config;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    @Bean
    public AmazonS3 amazonS3() {
        // Replace with your AWS region if necessary
        return AmazonS3ClientBuilder.standard()
                .withRegion("ap-south-1") 
                .build();
    }
}
