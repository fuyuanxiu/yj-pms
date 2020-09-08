package com.appqms.repair.service;



import org.springframework.web.bind.annotation.RequestParam;

import com.app.base.data.ApiResponseResult;

public interface RepairService {
	
	 public ApiResponseResult getLotList(String company,String factory) throws Exception; 
	 
	 public ApiResponseResult getStatList(String company,String factory,String fid) throws Exception;  
	 
	 public ApiResponseResult getNgtypeList(String fstate) throws Exception; 
	 
	 public ApiResponseResult getNgcodeList(String fstate,String ngtype) throws Exception; 
	 
	 public ApiResponseResult saveRepair(String factory,String company,String usercode,String ngid,String fstate,String deal,String qty,String note,String barcode) throws Exception;

	 public ApiResponseResult getList(String page,String result) throws Exception;
	 
	 public ApiResponseResult getUnList(String page,String lot_no) throws Exception;
}
