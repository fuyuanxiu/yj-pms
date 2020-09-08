package com.appqms.iqc.controller;


import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.data.ApiResponseResult;
import com.appqms.iqc.service.IqcService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@Api(description = "IQC管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/iqc")
public class IqcController {

    @Autowired
    private IqcService iqcService;

	
	@ApiOperation(value = "查询批次号", notes = "查询批次号")
	@RequestMapping(value = "/getFlotNo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFlotNo(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "flot") String flot) {		
      try {
    	  return iqcService.getFlotNo(factory,  company, flot);
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
    	  return iqcService.getCheckitem(username,flot_no);
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
    	  return iqcService.getCheckResult(did);
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
    	  return iqcService.addCheckResult(did,mid,values,type,company,factory,username,Integer.parseInt(valid));
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
            return iqcService.getRealFlotNo(factory,company,flotId);
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
            return iqcService.doRealFlotNo(factory,company,username,flotId,realFlotNo);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ApiResponseResult.failure("查询实际批次号失败！");
        }
    }

    @ApiOperation(value = "外观一键合格", notes = "外观一键合格")
    @RequestMapping(value = "/doAppearance", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult doAppearance(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
                                          @RequestParam(value = "username") String username,@RequestParam(value = "flotId") String flotId) {
        try {
            return iqcService.doAppearance(factory,company,username,flotId);
        } catch (Exception e) {
            System.out.println(e.toString());
            return ApiResponseResult.failure("外观一键合格操作失败！");
        }
    }
    
    @ApiOperation(value = "根据检验项目id录入检验备注", notes = "根据检验项目id录入检验备注")
	@RequestMapping(value = "/addCheckMemo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult addCheckMemo(@RequestParam(value = "itemid") String itemid,@RequestParam(value = "memo") String memo,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
			@RequestParam(value = "valueid") String valueid) {		
      try {
    	  if(StringUtils.isEmpty(valueid)){
    		  return ApiResponseResult.failure("检验项目id为空！");
    	  }
    	  return iqcService.addCheckMemo(company,factory,username,pid,itemid,valueid,memo);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("录入检验结果失败！");
        }
	}
    
    
    @ApiOperation(value = "根据检验项目id和字段名修改信息", notes = "根据检验项目id和字段名修改信息")
	@RequestMapping(value = "/doCheckitemByFiel", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult doCheckitemByFiel(
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
			@RequestParam(value = "fielname") String fielname,@RequestParam(value = "fielval") String fielval) {		
      try {
    	  if(StringUtils.isEmpty(pid)){
    		  return ApiResponseResult.failure("请先选择批次号！");
    	  }
    	  return iqcService.doCheckitemByFiel(company,factory,username,pid,fielname,fielval);
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
      	  return iqcService.doSumbit(company,factory,username,pid,opertype);
          } catch (Exception e) {
          	System.out.println(e.toString());
              return ApiResponseResult.failure("录入检验结果失败！");
          }
  	}
    
    @ApiOperation(value = "根据检验项目id保存信息", notes = "根据检验项目id保存信息")
  	@RequestMapping(value = "/doSaveByFiel", method = RequestMethod.POST, produces = "application/json")
  	public ApiResponseResult doSaveByFiel(
  			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
  			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
  			@RequestParam(value = "fielName") String fielName,@RequestParam(value = "fielValue") String fielValue) {		
        try {
      	  if(StringUtils.isEmpty(pid)){
      		  return ApiResponseResult.failure("请先选择批次号！");
      	  }
      	  return iqcService.doSaveByFiel(company,factory,username,pid,fielName,fielValue);
          } catch (Exception e) {
          	System.out.println(e.toString());
              return ApiResponseResult.failure("录入检验结果失败！");
          }
  	}
    
    @ApiOperation(value = "查询Ng批次号", notes = "查询Ng批次号")
	@RequestMapping(value = "/getNgFlotNo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getNgFlotNo(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "flot") String flot) {		
      try {
    	  return iqcService.getNgFlotNo( factory,  company, flot);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询Ng批次号失败！");
        }
	}
    
    @ApiOperation(value = "选择实际批次后执行", notes = "选择实际批次后执行")
	@RequestMapping(value = "/doNgCreate", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult doNgCreate(@RequestParam(value = "ftype") String ftype,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
			@RequestParam(value = "flotno") String flotno,@RequestParam(value = "did") String did) {		
      try {
    	  return iqcService.doNgCreate(company,factory,username,pid, did,ftype,flotno);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("录入检验结果失败！");
        }
	}
    
    @ApiOperation(value = "根据检验项目id保存信息", notes = "根据检验项目id保存信息")
  	@RequestMapping(value = "/doNgUpdatefield", method = RequestMethod.POST, produces = "application/json")
  	public ApiResponseResult doNgUpdatefield(
  			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
  			@RequestParam(value = "pid") String pid,@RequestParam(value = "username") String username,
  			@RequestParam(value = "fielName") String fielName,@RequestParam(value = "fielValue") String fielValue) {		
        try {
      	  if(StringUtils.isEmpty(pid)){
      		  return ApiResponseResult.failure("请先选择批次号！");
      	  }
      	  return iqcService.doNgUpdatefield(company,factory,username,pid,fielName,fielValue);
          } catch (Exception e) {
          	System.out.println(e.toString());
              return ApiResponseResult.failure("录入检验结果失败！");
          }
  	}
    
    @ApiOperation(value = "审批信息", notes = "审批信息")
  	@RequestMapping(value = "/getNgAplyList", method = RequestMethod.POST, produces = "application/json")
  	public ApiResponseResult getNgAplyList(
  			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
  			@RequestParam(value = "pid") String pid) {		
        try {
      	  return iqcService.getNgAplyList(company,factory,pid);
          } catch (Exception e) {
          	System.out.println(e.toString());
              return ApiResponseResult.failure("录入检验结果失败！");
          }
  	}
}
