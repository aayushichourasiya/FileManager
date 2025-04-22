package com.manager.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
public class ApplicationProperties {
//	@Value("${aws.region}")
	private String region;
	
	private String bucketName;
	private String accessKey;
	private String secretKey;
}
