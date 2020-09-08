package com.appqms.ngcheck.service;



import org.springframework.web.bind.annotation.RequestParam;

import com.app.base.data.ApiResponseResult;

public interface NgCheckService {
	 public ApiResponseResult getStatList(String company,String factory,String lot_no) throws Exception;  
	 
	 public ApiResponseResult getNgtypeList(String fstate) throws Exception; 
	 
	 public ApiResponseResult getNgcodeList(String fstate,String ngtype) throws Exception; 
	 
	 public ApiResponseResult saveNg(String factory,String company,String usercode,String ngid,String fstate,String ngtype,String ngcode,String deal,String qty,String note,String barcode) throws Exception;

	 public ApiResponseResult getList(String page) throws Exception;
}
