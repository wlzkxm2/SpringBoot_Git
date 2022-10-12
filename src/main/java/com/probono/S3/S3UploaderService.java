package com.probono.S3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class S3UploaderService {
    // 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 버킷 주소
    @Value("${cloud.aws.s3.bucket.url}")
    private String defaultUrl;

    private final AmazonS3Client amazonS3Client;

    public S3UploaderService(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String upload(MultipartFile uploadFile) throws IOException{
        String origName = uploadFile.getOriginalFilename();
        String url;

        try{
            // 확장자 검색
            final String ext = origName.substring(origName.lastIndexOf('.'));

            // 파일 이름 암호화
            final String saveFileName = getUuid() + ext;

            // 파일 객체 생성
            // System.getProperty 는 시스템 환경에대한 정보를 얻을수 있다
            File file = new File(System.getProperty("user.dir") + saveFileName);
            // 파일 변환
            uploadFile.transferTo(file);
            // 파일 업로드
            uploadOnS3(saveFileName, file);
            // 주소 할당
            url = defaultUrl + saveFileName;
            // 파일 삭제
            file.delete();
        }catch (StringIndexOutOfBoundsException e){
            url = null;
        }

        return url;
    }

    private static String getUuid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    private void uploadOnS3(final String findName, final File file){
        // AWS S3 전송 객체 생성
        final TransferManager transferManager = new TransferManager(this.amazonS3Client);

        // 요청객체 생성
        final PutObjectRequest request = new PutObjectRequest(bucket, findName, file);

        // 업로드 시도
        final Upload upload = transferManager.upload(request);

        try{
            upload.waitForCompletion();
        }catch (AmazonClientException amazonClientException){
            log.error(amazonClientException.getMessage());
        }catch (InterruptedException e){
            log.error(e.getMessage());
        }
    }

}
