package com.appqms.iqc.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;

public interface IpqcService {
    
    public ApiResponseResult getFlotNo() throws Exception;

    public ApiResponseResult getCheckitem(String username,String flot_no) throws Exception;
    
    public ApiResponseResult getCheckResult(String did) throws Exception;
    
    public ApiResponseResult addCheckResult(String did,String mid,String values,int type,String company,String factory,String username,int valid) throws Exception;

    public ApiResponseResult getStatNo(String factory, String company,String stat) throws Exception;
    
    public ApiResponseResult getFlotFrist(String factory, String company,String flot,String stat,String page) throws Exception;
    
    public ApiResponseResult getCheckitemFrist(String factory, String company,String username,String flot,String stat,String lx) throws Exception;
    
    public ApiResponseResult saveInsertValues(String factory, String company,String username,String pid) throws Exception;
    
    public ApiResponseResult saveFielValuesFirst(String factory, String company,String username,String pid,String fieldname,String fieldval) throws Exception;
    
    public ApiResponseResult doAppearance(String factory,String company,String username,String flotId) throws Exception;

    
}
