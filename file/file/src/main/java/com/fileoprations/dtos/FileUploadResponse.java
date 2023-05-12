package com.fileoprations.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileUploadResponse {
    private  String fileName;
    private String contentType;
    private String url;


}
