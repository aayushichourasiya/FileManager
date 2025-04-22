package com.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.manager.model.FileLog;

public interface FileLogRepository extends JpaRepository<FileLog,Long>{

}
