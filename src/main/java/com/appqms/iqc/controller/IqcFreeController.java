package com.appqms.iqc.controller;


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
import com.appqms.iqc.service.IqcFreeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


@Api(description = "IQC半年免检")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/iqcFree")
public class IqcFreeController {

    @Autowired
    private IqcFreeService iqcFreeService;

	
	@ApiOperation(value = "查询批次号", notes = "查询批次号")
	@RequestMapping(value = "/getFlotNo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFlotNo(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "flot") String flot) {		
      try {
    	  return iqcFreeService.getFlotNo( factory,  company, flot);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}

	@ApiOperation(value = "根据批次号获取检验项目", notes = "根据批次号获取检验项目")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/getCheckitem", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  getCheckitem(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,@RequestParam(value = "flot_no") String flot_no) {		
      try {
    	  if(StringUtils.isEmpty(flot_no)){
    		  return ApiResponseResult.failure("批次号不能为空！");
    	  }
    	  //String username = URLDecoder.decode(functionName,"UTF-8");
    	  return iqcFreeService.getCheckitem( factory,  company,flot_no);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	@ApiOperation(value = "根据检验项目id查询检验结果", notes = "根据检验项目id查询检验结果")
	@RequestMapping(value = "/getCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getCheckResult(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company, @RequestParam(value = "username") String username,@RequestParam(value = "flot_no") String flot_no) {		
      try {
    	  return iqcFreeService.getCheckResult(factory,company,username,flot_no);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询检验结果失败！");
        }
	}
	
	@ApiOperation(value = "根据检验项目id查询检验结果", notes = "根据检验项目id查询检验结果")
	@RequestMapping(value = "/saveCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult saveCheckResult(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company, @RequestParam(value = "username") String username,
			@RequestParam(value = "pi_pid") String pi_pid,@RequestParam(value = "pi_values") String pi_values,
			@RequestParam(value = "pi_name") String pi_name) {		
      try {
    	  pi_pid = URLDecoder.decode(pi_pid,"UTF-8");
    	  pi_values = URLDecoder.decode(pi_values,"UTF-8");
    	  return iqcFreeService.saveCheckResult(factory,company,username,pi_pid,pi_values,pi_name);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询检验结果失败！");
        }
	}
	
	@ApiOperation(value = "根据检验项目id录入检验结果", notes = "根据检验项目id录入检验结果")
	@RequestMapping(value = "/addCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult addCheckResult(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company, @RequestParam(value = "username") String username,@RequestParam(value = "pi_itemid") String pi_itemid,@RequestParam(value = "pi_values") String pi_values
			,@RequestParam(value = "pi_type") String pi_type,@RequestParam(value = "pi_valid") String pi_valid) {		
      try {
    	  pi_itemid = URLDecoder.decode(pi_itemid,"UTF-8");
    	  pi_values = URLDecoder.decode(pi_values,"UTF-8");
    	  return iqcFreeService.addCheckResult(factory,company,username,pi_itemid,pi_values,pi_type,pi_valid);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("录入检验结果失败！");
        }
	}
	
	@ApiOperation(value = "查询实际批次号", notes = "查询实际批次号")
    @RequestMapping(value = "/getRealFlotNo", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult getRealFlotNo(@RequestParam(value = "factory") String factory,
                                           @RequestParam(value = "company") String company,@RequestParam(value = "flotId") String flotId) {
        try {
            return iqcFreeService.getRealFlotNo(factory,company,flotId);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ApiResponseResult.failure("查询实际批次号失败！");
        }
    }
	@ApiOperation(value = "选择实际批次号后的操作", notes = "选择实际批次号后的操作")
    @RequestMapping(value = "/doRealFlotNo", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult doRealFlotNo(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
                                          @RequestParam(value = "username") String username,@RequestParam(value = "flotId") String flotId,@RequestParam(value = "realFlotNo") String realFlotNo) {
        try {
            return iqcFreeService.doRealFlotNo(factory,company,username,flotId,realFlotNo);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ApiResponseResult.failure("查询实际批次号失败！");
        }
    }
	
	@ApiOperation(value = "根据检验项目id和字段名修改信息", notes = "根据检验项目id和字段名修改信息")
	@RequestMapping(value = "/doFreeItemField", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult doFreeItemField(
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
			@RequestParam(value = "fielname") String fielname,@RequestParam(value = "fielval") String fielval) {		
      try {
    	  if(StringUtils.isEmpty(pid)){
    		  return ApiResponseResult.failure("请先选择批次号！");
    	  }
    	  return iqcFreeService.doFreeItemField(company,factory,username,pid,fielname,fielval);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("录入检验结果失败！");
        }
	}
    
    @ApiOperation(value = "根据检验项目id提交按钮", notes = "根据检验项目id提交按钮")
  	@RequestMapping(value = "/doSumbit", method = RequestMethod.POST, produces = "application/json")
  	public ApiResponseResult doSumbit(
  			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
  			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
  			@RequestParam(value = "opertype") String opertype) {		
        try {
      	  if(StringUtils.isEmpty(pid)){
      		  return ApiResponseResult.failure("请先选择批次号！");
      	  }
      	  return iqcFreeService.doSumbit(company,factory,username,pid,opertype);
          } catch (Exception e) {
          	System.out.println(e.toString());
              return ApiResponseResult.failure("录入检验结果失败！");
          }
  	}

}
