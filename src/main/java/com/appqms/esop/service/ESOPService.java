package com.appqms.esop.service;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import com.app.base.data.ApiResponseResult;

public interface ESOPService {
    public ApiResponseResult getFilesList(String proNo,
    		String proc,
    		String ver,
    		String page,
    		String size,
    		String renum) throws Exception;
    
    public ApiResponseResult onlineView(String url, String fname,HttpServletResponse response) throws Exception;
}