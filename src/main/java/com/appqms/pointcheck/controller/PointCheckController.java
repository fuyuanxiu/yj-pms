package com.appqms.pointcheck.controller;


import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.data.ApiResponseResult;
import com.appqms.pointcheck.service.PointCheckService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


@Api(description = "点检管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/pc")
public class PointCheckController {

    @Autowired
    private PointCheckService pointCheckService;

	
	@ApiOperation(value = "查询站别", notes = "查询站别")
	@RequestMapping(value = "/getStatList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getStatList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory) {		
      try {
    	  return pointCheckService.getStatList(company,factory);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "查询线体", notes = "查询线体")
	@RequestMapping(value = "/getLineList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getLineList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory,
			@RequestParam(value = "fstate")String fstate) {		
      try {
    	  return pointCheckService.getLineList(company,factory,fstate);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "查询方案", notes = "查询方案")
	@RequestMapping(value = "/getProjectList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getProjectList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory,
			@RequestParam(value = "fstate")String fstate,@RequestParam(value = "fline")String fline) {		
      try {
    	  return pointCheckService.getProjectList(company,factory,fstate,fline);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "查询点检单号", notes = "查询点检单号")
	@RequestMapping(value = "/getBillNoList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getBillNoList(@RequestParam(value = "fstate")String fstate,@RequestParam(value = "fline")String fline,@RequestParam(value = "fpro")String fpro) {		
      try {
    	  fpro = URLDecoder.decode(fpro,"UTF-8");
    	  return pointCheckService.getBillNoList(fstate,fline,fpro);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "查询检验项目", notes = "查询检验项目")
	@RequestMapping(value = "/getItemList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getItemList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory,
			@RequestParam(value = "fstate")String fstate,@RequestParam(value = "fline")String fline,
			@RequestParam(value = "fpro")String fpro,@RequestParam(value = "usercode")String usercode,
			@RequestParam(value = "pno")String pno) {		
      try {
    	  System.out.println(fpro);
    	  fpro = URLDecoder.decode(fpro,"UTF-8");
    	  System.out.println(fpro);
    	  return pointCheckService.getItemList(company,factory,fstate,fline,fpro,usercode,pno);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "查询检验项目的详细信息", notes = "查询检验项目的详细信息")
	@RequestMapping(value = "/getItemDetail", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getItemDetail(@RequestParam(value = "fid")String fid,@RequestParam(value = "mid")String mid) {		
      try {
    	  return pointCheckService.getItemDetail(fid,mid);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}

	@ApiOperation(value = "根据检验项目id录入检验结果", notes = "根据检验项目id录入检验结果")
	@RequestMapping(value = "/addCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult addCheckResult(@RequestParam(value = "did") String did,@RequestParam(value = "values") String values,@RequestParam(value = "fnode") String fnode,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "mid") String mid,@RequestParam(value = "username") String username) {		
      try {
    	  if(StringUtils.isEmpty(did)){
    		  return ApiResponseResult.failure("检验项目id为空！");
    	  }
    	  return pointCheckService.addCheckResult(did,mid,values,fnode,company,factory,username);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询检验结果失败！");
        }
	}

    
}
