package com.fileoprations.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class FileUploadWithFECode {
    @GetMapping("/files")
    ModelAndView fileUpload(){
        return new ModelAndView("index.html");
    }

}
