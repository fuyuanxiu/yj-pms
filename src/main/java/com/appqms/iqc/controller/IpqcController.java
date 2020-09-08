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
import com.appqms.iqc.service.IpqcService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


@Api(description = "IPQC管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/ipqc")
public class IpqcController {

    @Autowired
    private IpqcService ipqcService;

	
	@ApiOperation(value = "查询批次号", notes = "查询批次号")
	@RequestMapping(value = "/getFlotNo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFlotNo() {		
      try {
    	  return ipqcService.getFlotNo();
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}

	@ApiOperation(value = "根据批次号获取检验项目", notes = "根据批次号获取检验项目")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/getCheckitem", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  getCheckitem(@RequestParam(value = "username") String username,@RequestParam(value = "flot_no") String flot_no) {		
      try {
    	  if(StringUtils.isEmpty(username)){
    		  return ApiResponseResult.failure("登录失效，请重新登录！");
    	  }
    	  if(StringUtils.isEmpty(flot_no)){
    		  return ApiResponseResult.failure("批次号不能为空！");
    	  }
    	  //String username = URLDecoder.decode(functionName,"UTF-8");
    	  return ipqcService.getCheckitem(username,flot_no);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	@ApiOperation(value = "根据检验项目id查询检验结果", notes = "根据检验项目id查询检验结果")
	@RequestMapping(value = "/getCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getCheckResult(@RequestParam(value = "did") String did) {		
      try {
    	  if(StringUtils.isEmpty(did)){
    		  return ApiResponseResult.failure("检验项目id为空！");
    	  }
    	  return ipqcService.getCheckResult(did);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询检验结果失败！");
        }
	}
	
	@ApiOperation(value = "根据检验项目id录入检验结果", notes = "根据检验项目id录入检验结果")
	@RequestMapping(value = "/addCheckResult", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult addCheckResult(@RequestParam(value = "did") String did,@RequestParam(value = "values") String values,@RequestParam(value = "type") int type,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "mid") String mid,@RequestParam(value = "username") String username,
			@RequestParam(value = "valid") String valid) {		
      try {
    	  if(StringUtils.isEmpty(did)){
    		  return ApiResponseResult.failure("检验项目id为空！");
    	  }
    	  return ipqcService.addCheckResult(did,mid,values,type,company,factory,username,Integer.parseInt(valid));
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询检验结果失败！");
        }
	}
	
	@ApiOperation(value = "查询站别", notes = "查询站别")
	@RequestMapping(value = "/getStatNo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getStatNo(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "stat") String stat) {		
      try {
    	  return ipqcService.getStatNo(factory, company,stat);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	@ApiOperation(value = "查询批次号", notes = "查询批次号")
	@RequestMapping(value = "/getFlotFrist", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFlotFrist(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "flot") String flot,@RequestParam(value = "stat") String stat,@RequestParam(value = "page") String page) {		
      try {
    	  return ipqcService.getFlotFrist(factory, company,flot,stat,page);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
	
	@ApiOperation(value = "根据批次号获取检验项目", notes = "根据批次号获取检验项目")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/getCheckitemFrist", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  getCheckitemFrist(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			@RequestParam(value = "username") String username,@RequestParam(value = "flot") String flot,@RequestParam(value = "stat") String stat,
			@RequestParam(value = "lx") String lx) {		
      try {
    	  if(StringUtils.isEmpty(username)){
    		  return ApiResponseResult.failure("登录失效，请重新登录！");
    	  }
    	  if(StringUtils.isEmpty(flot)){
    		  return ApiResponseResult.failure("批次号不能为空！");
    	  }
    	  String stat1 = URLDecoder.decode(stat,"UTF-8");
    	  return ipqcService.getCheckitemFrist(factory, company,username,flot,stat1,lx);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	@ApiOperation(value = "根据批次号获取检验项目", notes = "根据批次号获取检验项目")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/saveInsertValues", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  saveInsertValues(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			@RequestParam(value = "username") String username,@RequestParam(value = "pid") String pid) {		
      try {
    	  if(StringUtils.isEmpty(username)){
    		  return ApiResponseResult.failure("登录失效，请重新登录！");
    	  }
    	  return ipqcService.saveInsertValues(factory, company,username,pid);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	@ApiOperation(value = "根据批次号获取检验项目", notes = "根据批次号获取检验项目")
	@ApiImplicitParam(name = "username", value = "用户账号", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/saveFielValuesFirst", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  saveFielValuesFirst(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			@RequestParam(value = "username") String username,@RequestParam(value = "pid") String pid,
			@RequestParam(value = "fieldname") String fieldname,@RequestParam(value = "fieldval") String fieldval) {		
      try {
    	  if(StringUtils.isEmpty(username)){
    		  return ApiResponseResult.failure("登录失效，请重新登录！");
    	  }
    	  return ipqcService.saveFielValuesFirst(factory, company,username,pid,fieldname,fieldval);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	@ApiOperation(value = "外观一键合格", notes = "外观一键合格")
    @RequestMapping(value = "/doAppearance", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult doAppearance(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
                                          @RequestParam(value = "username") String username,@RequestParam(value = "flotId") String flotId) {
        try {
            return ipqcService.doAppearance(factory,company,username,flotId);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ApiResponseResult.failure("外观一键合格操作失败！");
        }
    }
    
}
