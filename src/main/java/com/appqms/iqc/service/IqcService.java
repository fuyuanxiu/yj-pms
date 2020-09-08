package com.appqms.iqc.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;

public interface IqcService {
    
    public ApiResponseResult getFlotNo(String factory, String company,String flot) throws Exception;

    public ApiResponseResult getCheckitem(String username,String flot_no) throws Exception;
    
    public ApiResponseResult getCheckResult(String did) throws Exception;
    
    public ApiResponseResult addCheckResult(String did,String mid,String values,int type,String company,String factory,String username,int valid) throws Exception;

    public ApiResponseResult getRealFlotNo(String factory,String company,String flotId) throws Exception;

    public ApiResponseResult doRealFlotNo(String factory,String company,String username,String flotId,String realFlotNo) throws Exception;

    public ApiResponseResult doAppearance(String factory,String company,String username,String flotId) throws Exception;

	public ApiResponseResult addCheckMemo(String company, String factory, String username, String pid, String itemid,
			String valueid, String memo)throws Exception;
	
	 public ApiResponseResult doCheckitemByFiel(String factory,String company,String username,String pid,String fielname,String fielval) throws Exception;
	 
	 public ApiResponseResult doSumbit(String factory,String company,String username,String pid,String opertype) throws Exception;
	 
	 public ApiResponseResult doSaveByFiel(String factory,String company,String username,String pid,String fielName,String fielValue) throws Exception;
	 
	 public ApiResponseResult getNgFlotNo(String factory, String company,String flot) throws Exception;

	 public ApiResponseResult doNgCreate(String company,String factory,String username,String pid,String did,String ftype,String flotno) throws Exception;
	 
	 public ApiResponseResult doNgUpdatefield(String factory,String company,String username,String pid,String fielName,String fielValue) throws Exception;
	 
	 public ApiResponseResult getNgAplyList(String factory,String company,String pid) throws Exception;
	 
	 
}
