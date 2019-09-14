package com.lemon.community.controller;

import com.lemon.community.dto.FileUploadDTO;
import com.lemon.community.enums.FileUploadRetEnum;
import com.lemon.community.provider.UCloudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileUploadController {
    @Autowired
    private UCloudProvider uCloudProvider;

    @ResponseBody
    @RequestMapping("/file/upload")
    public FileUploadDTO upload(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        try {
            return uCloudProvider.upload(file.getInputStream(), file.getContentType(), file.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            FileUploadDTO fileUploadDTO = new FileUploadDTO();
            fileUploadDTO.setSuccess(FileUploadRetEnum.FILE_UPLOAD_FAILED.getResult());
            fileUploadDTO.setMessage(FileUploadRetEnum.FILE_UPLOAD_FAILED.getMessage());
            return fileUploadDTO;
        }
    }
}
