package com.system.log.service.internal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.system.log.dao.SysLogDao;
import com.system.log.entity.SysLog;
import com.system.log.service.SysLogService;


@Service(value = "sysLogService")
@Transactional(propagation = Propagation.REQUIRED)
public class SysLogImpl implements SysLogService {

    @Autowired
    private SysLogDao sysLogDao;

	@Override
	public ApiResponseResult add(SysLog sysLog) throws Exception {
		// TODO Auto-generated method stub
		sysLogDao.save(sysLog);
        return ApiResponseResult.success("添加成功！");
	}
	

}
