package com.probono.Mapper;

import com.probono.entity.Files;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    void saveFiles(Files file);
}
