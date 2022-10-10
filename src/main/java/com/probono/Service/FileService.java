package com.probono.Service;

import com.probono.Mapper.FileMapper;
import com.probono.entity.Files;
import com.probono.entity.User;
import com.probono.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class FileService {
    @Autowired
    FileRepo fileRepo;


    public void saveFile(Files files){
        Files f = new Files();
        f.setFilename(files.getFilename());
        f.setFileOriname(files.getFileOriname());
        f.setFileUrl(files.getFileUrl());

        fileRepo.save(f);
    }
}
