package com.lemon.community.provider;

import cn.ucloud.ufile.UfileClient;
import cn.ucloud.ufile.api.object.ObjectConfig;
import cn.ucloud.ufile.auth.ObjectAuthorization;
import cn.ucloud.ufile.auth.UfileObjectLocalAuthorization;
import cn.ucloud.ufile.bean.PutObjectResultBean;
import cn.ucloud.ufile.exception.UfileClientException;
import cn.ucloud.ufile.exception.UfileServerException;
import com.lemon.community.dto.FileUploadDTO;
import com.lemon.community.enums.FileUploadRetEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UCloudProvider {
    @Value("${ucloud.ufile.public-key}")
    private String publicKey;
    @Value("${ucloud.ufile.private-key}")
    private String privateKey;
    @Value("${ucloud.ufile.region}")
    private String region;
    @Value("${ucloud.ufile.proxySuffix}")
    private String proxySuffix;
    @Value("${ucloud.ufile.bucketName}")
    private String bucketName;
    @Value("${ucloud.ufile.expiresDuration}")
    private Integer expiresDuration;

    /**
     * @param inputStream 上传文件的流
     * @param mimeType    文件的类型
     * @param fileName    文件的名称
     * @return
     */
    public FileUploadDTO upload(InputStream inputStream, String mimeType, String fileName) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        //需要的授权器和config
        ObjectAuthorization bucketAuthorizer = new UfileObjectLocalAuthorization(publicKey, privateKey);
        ObjectConfig config = new ObjectConfig(region, proxySuffix);

        String generatedFileName;
        String[] splitedFileName = StringUtils.split(fileName, ".");
        if (splitedFileName.length > 1) {//根据传入的文件名生成新的文件名，避免云存储时文件名相同造成覆盖
            generatedFileName = UUID.randomUUID().toString() + "." + splitedFileName[splitedFileName.length - 1];
        } else {//传入的文件名格式不正确
            return null;
        }
        try {
            PutObjectResultBean response = UfileClient.object(bucketAuthorizer, config)
                    .putObject(inputStream, mimeType)
                    .nameAs(generatedFileName)
                    .toBucket(bucketName)
                    // withVerifyMd5(false),是否上传校验MD5, Default = true .
                    //指定progress callback的间隔, Default = 每秒回调
                    //.withProgressConfig(ProgressConfig.callbackWithPercent(10))
                    //配置进度监听
                    //response:retCode(执行失败时的错误代码，成功时为0),eTag(已经上传文件在UFile中的哈希值),action,message（执行失败时候的错误信息，成功时为null）
                    .setOnProgressListener((bytesWritten, contentLength) -> {
                    })
                    .execute();
            if (response != null && response.getRetCode() == 0) {
                String url = UfileClient.object(bucketAuthorizer, config)
                        .getDownloadUrlFromPrivateBucket(generatedFileName, bucketName, expiresDuration)
                        .createUrl();
                fileUploadDTO.setUrl(url);
                fileUploadDTO.setSuccess(FileUploadRetEnum.FILE_UPLOAD_SUCCESS.getResult());
                return fileUploadDTO;
            } else {
                fileUploadDTO.setSuccess(FileUploadRetEnum.FILE_UPLOAD_FAILED.getResult());
                fileUploadDTO.setMessage(FileUploadRetEnum.FILE_UPLOAD_FAILED.getMessage());
                return fileUploadDTO;
            }
        } catch (UfileClientException e) {
            e.printStackTrace();
            fileUploadDTO.setSuccess(FileUploadRetEnum.FILE_UPLOAD_FAILED.getResult());
            fileUploadDTO.setMessage(FileUploadRetEnum.FILE_UPLOAD_FAILED.getMessage());
            return fileUploadDTO;
        } catch (UfileServerException e) {
            e.printStackTrace();
            fileUploadDTO.setSuccess(FileUploadRetEnum.FILE_UPLOAD_FAILED.getResult());
            fileUploadDTO.setMessage(FileUploadRetEnum.FILE_UPLOAD_FAILED.getMessage());
            return fileUploadDTO;
        }
    }
}

