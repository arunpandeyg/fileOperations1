package com.fileoprations.controllers;

import com.fileoprations.dao.DocFileDao;
import com.fileoprations.dtos.FileDocument;
import com.fileoprations.dtos.FileUploadResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RestController
public class UDWithDBController {
    @Autowired
    private final DocFileDao docFileDao;

    public UDWithDBController(DocFileDao docFileDao) {
        this.docFileDao = docFileDao;
    }
    @PostMapping("/single/uploadDB")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String name = StringUtils.cleanPath(file.getOriginalFilename());
        FileDocument fileDocument = new FileDocument();
        fileDocument.setFileName(name);
        fileDocument.setDocFile(file.getBytes());

        docFileDao.save(fileDocument);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFormDB/")
                .path(name)
                .toUriString();
        String contentType = file.getContentType();
        FileUploadResponse response = new FileUploadResponse(name, contentType, url);
        return response;
    }
    @GetMapping("/downloadFormDB/{fileName}")
    ResponseEntity<byte[]> downloadingSingFile(@PathVariable String fileName, HttpServletRequest request){
        FileDocument doc = docFileDao.findByFileName(fileName);



        String  mimeType = request.getServletContext().getMimeType(doc.getFileName());
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inLine;fileName="+ doc.getFileName())
                .body(doc.getDocFile());
    }
}
