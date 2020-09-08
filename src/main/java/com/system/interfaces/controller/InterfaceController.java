package com.system.interfaces.controller;


import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.data.ApiResponseResult;
import com.system.interfaces.service.InterfaceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@Api(description = "用户管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/interface")
public class InterfaceController {

    @Autowired
    private InterfaceService interfaceService;
    
    @ApiOperation(value = "查询版本号", notes = "查询版本号")
	@ApiImplicitParam()
	@RequestMapping(value = "/queryVersion", method = RequestMethod.POST, produces = "application/json")
 	public String queryVersion() throws Exception {
 		try{
 			return interfaceService.queryVersion();
 		}catch(Exception e){
 			return null;
 		}
 	}
 	
 	@ApiOperation(value = "运行环境", notes = "运行环境")
	@ApiImplicitParam()
	@RequestMapping(value = "/queryRunEnv", method = RequestMethod.POST, produces = "application/json")
  	public String queryRunEnv() throws Exception {
  		try{
  			return interfaceService.queryRunEnv();
  		}catch(Exception e){
  			return null;
  		}
  	}
  	
  	@ApiOperation(value = "查询权限", notes = "查询权限")
  	@ApiImplicitParams({
		@ApiImplicitParam(name = "Userno", value = "用户工号", dataType = "String", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "pmachtype", value = "用户工号", dataType = "String", paramType = "query", defaultValue = "") })
	@RequestMapping(value = "/queryPurview", method = RequestMethod.GET, produces = "application/json")
	public String queryPurview(@RequestParam(value = "Userno") String Userno,@RequestParam(value = "pmachtype") String pmachtype) {		
      try {
    	  return  interfaceService.queryPurview(Userno,pmachtype);
        } catch (Exception e) {
        	return "";
        }
	}
  	
  	@ApiOperation(value = "查询权限", notes = "查询权限")
  	@ApiImplicitParams({
		@ApiImplicitParam(name = "function_Name", value = "功能名称", dataType = "String", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "pmachtype", value = "用户工号", dataType = "String", paramType = "query", defaultValue = ""),
		@ApiImplicitParam(name = "XmlDate", value = "用户工号", dataType = "String", paramType = "query", defaultValue = "")})
	@RequestMapping(value = "/getEqCon", method = RequestMethod.GET, produces = "application/json")
	public String getEqCon(@RequestParam(value = "function_Name") String function_Name,@RequestParam(value = "MachineCode") String MachineCode,
			@RequestParam(value = "XmlDate") String XmlDate) {		
      try {
    	  function_Name = URLDecoder.decode(function_Name,"UTF-8");
    	  return  interfaceService.getEqCon(function_Name,MachineCode,XmlDate);
        } catch (Exception e) {
        	return "";
        }
	}
  	
  	
 	
}
