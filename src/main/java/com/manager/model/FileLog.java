package com.manager.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FileLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String fileName;
	private String operation;
	private LocalDateTime timeStamp;
}
