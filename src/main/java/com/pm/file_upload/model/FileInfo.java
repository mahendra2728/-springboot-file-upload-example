package com.pm.file_upload.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file_tbl")
public class FileInfo {

    @Id
    private String fileId;
    private String fileName;
    private String filePath;
    private LocalDateTime fileUploadDate;
}
