package com.appqms.pointcheck.service;



import com.app.base.data.ApiResponseResult;

public interface PointCheckService {
    
    public ApiResponseResult getStatList(String company,String factory) throws Exception;
    
    public ApiResponseResult getLineList(String company,String factory,String fstate) throws Exception;
    
    public ApiResponseResult getProjectList(String company,String factory,String fstate,String fline) throws Exception;
    
    public ApiResponseResult getBillNoList(String fstate, String fline,String proj) throws Exception;
    
    public ApiResponseResult getItemList(String company,String factory,String fstate,String fline,String fpro,String usercode,String pno) throws Exception;

    public ApiResponseResult getItemDetail(String fid,String mid) throws Exception;
    
    public ApiResponseResult addCheckResult(String did,String mid,String values,String fnode,String company,String factory,String username) throws Exception;
    
}
