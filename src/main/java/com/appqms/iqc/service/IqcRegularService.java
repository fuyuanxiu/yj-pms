package com.appqms.iqc.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;

public interface IqcRegularService {
    
    public ApiResponseResult getFlotNo(String factory, String company,String flot) throws Exception;

    public ApiResponseResult getCheckitem(String  factory,String  company,String flot_no) throws Exception;
    
    public ApiResponseResult getCheckResult(String factory,String company,String username,String flot_no,String fname) throws Exception;
    
    public ApiResponseResult saveCheckResult(String factory,String company,String username,String pi_itemid,String pi_values,String pi_valdate) throws Exception;
    
    public ApiResponseResult addCheckResult(String factory,String company,String username,String pi_itemid,String pi_values,String pi_type,String pi_valid) throws Exception;

    public ApiResponseResult doRealFlotNo(String factory,String company,String username,String flotId,String realFlotNo) throws Exception;
    
    public ApiResponseResult getRealFlotNo(String factory,String company,String flotId) throws Exception;
    
    public ApiResponseResult doRegularitemField(String factory,String company,String username,String pid,String fielname,String fielval) throws Exception;
	 
	 public ApiResponseResult doSumbit(String factory,String company,String username,String pid,String opertype) throws Exception;
	 
	 public ApiResponseResult getList(String factory, String company,String keyword,String page) throws Exception;
    
	 
	 
}
