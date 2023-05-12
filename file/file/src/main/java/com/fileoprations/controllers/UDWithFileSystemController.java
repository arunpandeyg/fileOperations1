package com.fileoprations.controllers;

import com.fileoprations.dtos.FileUploadResponse;
import com.fileoprations.services.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/")
public class UDWithFileSystemController {
    private final FileStorageService fileStorageService;

    public UDWithFileSystemController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
    @PostMapping("single/upload")
    FileUploadResponse singleFileUpload(@RequestParam("file") MultipartFile file){
        String fileName = fileStorageService.storeFile(file);
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("download/")
                .path(fileName)
                .toUriString();
        String contentType = file.getContentType();
        FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);
        return response;
    }
    @GetMapping("download/{fileName}")
    ResponseEntity<Resource> downloadingSingFile(@PathVariable String fileName, HttpServletRequest request){
        Resource resource = fileStorageService.downloadFile(fileName);
       // MediaType contentType = MediaType.APPLICATION_PDF;
        String mimeType;
        try {
             mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        return  ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
               // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName="+resource.getFilename())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inLine;fileName="+resource.getFilename())
                .body(resource);
    }
    @PostMapping("multiple/upload")
    List<FileUploadResponse> multipleUpload(@RequestParam("files") MultipartFile[] files){
        if (files.length > 7){
                throw new RuntimeException("please select less then or equal 7 files !!");
        }
        List<FileUploadResponse> uploadResponseList = new ArrayList<>();
        Arrays.asList(files)
                .stream()
                .forEach(file ->{
                    String fileName = fileStorageService.storeFile(file);
                    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("download/")
                            .path(fileName)
                            .toUriString();
                    String contentType = file.getContentType();
                    FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);
                    uploadResponseList.add(response);
                });
        return uploadResponseList;
    }
    //zip file or multiple files
    @GetMapping("zipDownload")
    void zipDownload(@RequestParam("fileName") String[] files, HttpServletResponse response) throws IOException{
        //zip stream for output
        try(ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())){
            Arrays.asList(files).stream().forEach(file -> {
                Resource resource = fileStorageService.downloadFile(file);
                ZipEntry zipEntry = new ZipEntry(resource.getFilename());
                try {
                    zipEntry.setSize(resource.contentLength());
                    zos.putNextEntry(zipEntry);
                    StreamUtils.copy(resource.getInputStream(), zos);
                    zos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException("something went wrong while zipping!!");
                }
            });
            zos.finish();
        }
        response.setStatus(200);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName=zipfile" );
    }
}
