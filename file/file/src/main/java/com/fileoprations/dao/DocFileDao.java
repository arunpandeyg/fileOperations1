package com.fileoprations.dao;

import com.fileoprations.dtos.FileDocument;
import org.springframework.data.repository.CrudRepository;

public interface DocFileDao  extends CrudRepository<FileDocument, Long> {

    FileDocument findByFileName(String fileName);

}

