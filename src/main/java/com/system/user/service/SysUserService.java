package com.system.user.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;

import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;

public interface SysUserService {

    public ApiResponseResult add(SysUser sysUser) throws Exception;

    public ApiResponseResult changePassword(String loginName, String oldPassword, String password, String rePassword) throws Exception;

    public ApiResponseResult resetPassword(Long id, String password, String rePassword) throws Exception;

    public ApiResponseResult getUserInfo(String userCode) throws Exception;
    
    public List<Map<String, Object>> findByUserCode(String userCode) throws Exception;
    
    public List<Map<String, Object>> queryVersion() throws Exception;
    
    public List<Map<String, Object>> queryRunEnv() throws Exception;
    
    public List queryPurview(String userno) throws Exception;
    
    public ApiResponseResult getRfSetup(String functionName) throws Exception;
    
    public ApiResponseResult getExcProc(String functionName,String fileName,String pmachtype,String fileValue, String outFiles) throws Exception;
    
    public ApiResponseResult getlist(String usercode,String username,PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult queryAppVersion() throws Exception;
    
    public ApiResponseResult changPsw(String userCode,String newp) throws Exception;

}
