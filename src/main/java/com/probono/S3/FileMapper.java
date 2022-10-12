package com.probono.S3;

import com.probono.entity.Files;
import com.probono.repo.FileRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class FileMapper {

    @Autowired(required = true)
    private FileRepo fileRepo;

    public void DBSave(){
        Files saveFile = new Files();
        saveFile.setFilename(destinationFileName);
        saveFile.setFileOriname(sourceFileName);
        saveFile.setFileUrl(fileUrl);
        saveFile.setUserID(findid);
        Files addFile = fileRepo.save(saveFile);
    }


}
