package com.pm.file_upload.controller;

import com.pm.file_upload.model.FileInfo;
import com.pm.file_upload.service.FileOperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/api/files")
public class FileUploadDownloadController {

    @Autowired
    private FileOperationService fileOperationService;

    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(@RequestParam("file") MultipartFile file){
        FileInfo response =  fileOperationService.save(file);
        if(Objects.nonNull(response)){
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    @GetMapping("/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String fileId) throws MalformedURLException {
        Resource file = fileOperationService.load(fileId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }


    @GetMapping
    public ResponseEntity<List<FileInfo>> findAllFiles(){
        List<FileInfo> allFiles = fileOperationService.findAllFiles();
        if(Objects.nonNull(allFiles)){
            return ResponseEntity.status(HttpStatus.OK).body(allFiles);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}
