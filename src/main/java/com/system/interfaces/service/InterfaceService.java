package com.system.interfaces.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;

public interface InterfaceService {

    
    public String queryVersion() throws Exception;
    
    public String queryRunEnv() throws Exception;
    
    public String queryPurview(String userno,String pmachtype) throws Exception;
    
    public String getEqCon(String function_Name,String MachineCode,String XmlDate) throws Exception;
    
    public ApiResponseResult getRfSetup(String functionName) throws Exception;
    
    public ApiResponseResult getExcProc(String functionName,String fileName,String pmachtype,String fileValue, String outFiles) throws Exception;
    
    public ApiResponseResult queryAppVersion() throws Exception;
    
    public ApiResponseResult changPsw(String userCode,String newp) throws Exception;

}
