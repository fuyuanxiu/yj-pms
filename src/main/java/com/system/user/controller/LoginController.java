package com.system.user.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.app.aspect.MyLog;
import com.app.base.data.ApiResponseResult;
import com.app.base.utils.MD5Util;
import com.app.config.annotation.UserLoginToken;
import com.app.config.service.TokenService;
import com.system.user.entity.SysUser;
import com.system.user.service.SysUserService;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(description = "登录管理模块")
@RestController
public class LoginController {

    @Autowired
    private SysUserService sysUserService;

	@ApiOperation(value = "用户登录验证", notes = "根据用户Id验证用户密码是否正确，进行登录验证; 登录成功后，置为上线")
	@ApiImplicitParam(name = "username", value = "用户Id", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult login(@RequestBody Map<String, Object> params) {		
		try {
			String username = params.get("username").toString().toUpperCase();
			String password = params.get("password").toString();
    		
    		if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)){
                return ApiResponseResult.failure("用户名或密码不能为空！");
            }
    		
    		List<Map<String, Object>> userForBase=sysUserService.findByUserCode(username);
            if(userForBase.size() == 0){
            	return ApiResponseResult.failure("用户不存在！");
            }else {
            	//验证密码
            	String p = userForBase.get(0).get("FPASSWORD").toString();
            	p =proPass(p);
            	if(!p.equals(password)){
            		return ApiResponseResult.failure("用户名或者密码不正确！");
            	}else{
            		return ApiResponseResult.success("登录成功！").data(userForBase.get(0));
            	}
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("用户登录失败！"+e.toString());
        }
	}
//	 解密算法
	private String proPass(String src) throws Exception {
		String result = "";
		int first = new Integer(src.substring(0, 1)).intValue();
		String src_tem = src.substring(1);
		byte[] b = src_tem.getBytes("iso8859-1");
		byte[] temp = b;
		int i = 0;
		for (; i < b.length; i++) {
			temp[i] = new Integer(new Integer(temp[i]).intValue() ^ (first + 18))
			.byteValue();
		}
		result = new String(temp);
		return result;
	}
	
	
	@ApiOperation(value = "查询版本号", notes = "查询版本号")
	@RequestMapping(value = "/queryVersion", method = RequestMethod.GET, produces = "application/json")
	public ApiResponseResult queryVersion() {		
      try {
    		List<Map<String, Object>> userForBase=sysUserService.queryVersion();
            if(userForBase.size() == 0){
            	return ApiResponseResult.failure("版本号不存在！");
            }else {
            	return ApiResponseResult.success("查询版本号成功！").data(userForBase);
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询版本号失败！");
        }
	}
	
	@ApiOperation(value = "查询运行环境", notes = "查询运行环境")
	@RequestMapping(value = "/queryRunEnv", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult queryRunEnv() {		
      try {
    		List<Map<String, Object>> userForBase=sysUserService.queryRunEnv();
            if(userForBase.size() == 0){
            	return ApiResponseResult.failure("运行环境不存在！");
            }else {
            	return ApiResponseResult.success("查询运行环境成功！").data(userForBase);
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询运行环境失败！");
        }
	}
	
	@ApiOperation(value = "查询权限", notes = "查询权限")
	@ApiImplicitParam(name = "userno", value = "用户Id", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/queryPurview", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult queryPurview(@RequestParam(value = "userno") String userno) {		
      try {
    		List<Map<String, Object>> userForBase=sysUserService.queryPurview(userno);
            if(userForBase.size() == 0){
            	return ApiResponseResult.failure("权限不存在！");
            }else {
            	return ApiResponseResult.success("查询权限成功！").data(userForBase);
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询权限失败！");
        }
	}
	
	@ApiOperation(value = "功能界面", notes = "功能界面")
	@ApiImplicitParam(name = "functionName", value = "方法名称", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/getRfSetup", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  getRfSetup(@RequestParam(value = "functionName") String functionName) {		
      try {
    	  System.out.println(functionName);
    	  String username = URLDecoder.decode(functionName,"UTF-8");
    	  return sysUserService.getRfSetup(functionName);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	@ApiOperation(value = "功能执行存储过程 ", notes = "功能执行存储过程 ")
	@ApiImplicitParam(name = "functionName", value = "方法名称", paramType = "Query", required = true, dataType = "String")
	@RequestMapping(value = "/getExcProc", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult  getExcProc(@RequestParam(value = "functionName") String functionName,
			@RequestParam(value = "fileName") String fileName,@RequestParam(value = "pmachtype") String pmachtype,
			@RequestParam(value = "fileValue") String fileValue,@RequestParam(value = "outFiles") String outFiles) {		
      try {
    	  System.out.println(functionName);
    	  functionName = URLDecoder.decode(functionName,"UTF-8");
    	  return sysUserService.getExcProc(functionName,fileName,pmachtype,fileValue,outFiles);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询功能界面失败！");
        }
	}
	
	
	@ApiOperation(value = "查询app版本号", notes = "查询app版本号")
	@RequestMapping(value = "/queryAppVersion", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult queryAppVersion() {		
      try {
    		return sysUserService.queryAppVersion();
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询版本号失败！");
        }
	}
	
	
	@ApiOperation(value = "修改密码", notes = "修改密码")
	@RequestMapping(value = "/changPsw", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult changPsw(@RequestParam(value = "usercode") String usercode,@RequestParam(value = "oldp") String oldp,@RequestParam(value = "newp") String newp) {		
		try {

    		if(StringUtils.isEmpty(usercode) || StringUtils.isEmpty(oldp) || StringUtils.isEmpty(newp)){
                return ApiResponseResult.failure("用户名或密码不能为空！");
            }
    		
    		List<Map<String, Object>> userForBase=sysUserService.findByUserCode(usercode);
            if(userForBase.size() == 0){
            	return ApiResponseResult.failure("用户不存在！");
            }else {
            	//验证密码
            	String p = userForBase.get(0).get("FPASSWORD").toString();
            	p =proPass(p);
            	if(!p.equals(oldp)){
            		return ApiResponseResult.failure("用户名或者密码不正确！");
            	}else{
            		return sysUserService.changPsw(usercode, encryptPass(newp));
            		//return ApiResponseResult.success("登录成功！").data(userForBase.get(0));
            	}
            }
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("用户登录失败！"+e.toString());
        }
	}
    
//	 解密算法
	private String encryptPass(String str) throws Exception {
		//byte[] temp = new byte[255] ;
		byte[] b = str.getBytes("iso8859-1");
		byte[] temp =b;
		int i = 0;
		for (; i < b.length; i++) {
			temp[i] = new Integer(new Integer(b[i]).intValue() ^ (8 + 18))
					.byteValue();
		}
		String result = 8 + new String(temp);
		return result;
	}
}
