package com.probono.S3;

import com.probono.entity.Files;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileDataService {

    private final S3UploaderService s3UploaderService;
    private final FileMapper fileMapper;

    public FileDataService(S3UploaderService s3UploaderService, FileMapper fileMapper){
        this.s3UploaderService = s3UploaderService;
        this.fileMapper = fileMapper;
    }

}
