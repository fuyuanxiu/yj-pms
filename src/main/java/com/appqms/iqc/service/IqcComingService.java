package com.appqms.iqc.service;

import com.app.base.data.ApiResponseResult;

public interface IqcComingService {
	
	 public ApiResponseResult getList(String factory, String company,String keyword,String page) throws Exception;
    
}
