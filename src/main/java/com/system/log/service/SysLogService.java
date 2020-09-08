package com.system.log.service;


import com.app.base.data.ApiResponseResult;
import com.system.log.entity.SysLog;

public interface SysLogService {

    public ApiResponseResult add(SysLog sysLog) throws Exception;

}
