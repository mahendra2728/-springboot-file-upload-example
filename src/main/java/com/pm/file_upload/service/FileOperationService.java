package com.pm.file_upload.service;

import com.pm.file_upload.model.FileInfo;
import com.pm.file_upload.repository.FileInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class FileOperationService {

    @Autowired
    private FileInfoRepository fileInfoRepository;

    private Path root = null;

    public FileOperationService(@Value("${file.server.path:uploads}") String fileRootPath) {
        root = Paths.get(fileRootPath);
    }

    public FileInfo save(MultipartFile file) {
        try {
            init();
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
            return storeFileInDb(file.getOriginalFilename());
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    public void init() {
        try {
            if(Files.notExists(root)){
                log.info("Directory not present so creating it now");
                Files.createDirectories(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    private FileInfo storeFileInDb(String fileName){
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(fileName);
        fileInfo.setFileId(UUID.randomUUID().toString());
        fileInfo.setFileUploadDate(LocalDateTime.now());
        fileInfo.setFilePath(root+"/"+fileName);
        return  fileInfoRepository.save(fileInfo);
    }

    public Resource load(String fileId) throws MalformedURLException {
        FileInfo fileInfo = fileInfoRepository.findById(fileId).orElseGet(null);
        if(Objects.nonNull(fileInfo)){
            Path file = root.resolve(fileInfo.getFileName());
            Resource tempResource = new UrlResource(file.toUri());
            if (tempResource.exists() || tempResource.isReadable()) {
                return tempResource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }

        }else{
            log.error("fileInfo object is null for fileId ",fileId);
        }
        return null;
    }

    public List<FileInfo> findAllFiles() {
        return fileInfoRepository.findAll();
    }
}
