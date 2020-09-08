package com.appqms.ftp.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.app.base.data.ApiResponseResult;

public interface FileCheckService {
    
    public ApiResponseResult uploadFileIqc(String factory,String company,String username,int mid,int type,String note,  MultipartFile[] file) throws Exception;
    
    public ApiResponseResult uploadFileIpqc(String factory,String company,String username,int mid,int type,String note,  MultipartFile[] file) throws Exception;
    
    public ApiResponseResult getFilesList(String mid) throws Exception;
    
    public ApiResponseResult onlineView(String url, String fname,HttpServletResponse response) throws Exception;
}
