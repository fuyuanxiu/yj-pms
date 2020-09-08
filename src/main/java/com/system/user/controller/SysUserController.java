package com.system.user.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.system.user.entity.SysUser;
import com.system.user.service.SysUserService;

import javax.servlet.http.HttpServletRequest;

@Api(description = "用户管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/sysUser")
public class SysUserController extends WebController{

    @Autowired
    private SysUserService sysUserService;
    

    @ApiOperation(value = "注册用户", notes = "注册用户")
    @PostMapping("/add")
    public ApiResponseResult add(@RequestBody(required=false) SysUser sysUser){
        try{
            return sysUserService.add(sysUser);
        }catch(Exception e){
            logger.error(e.getMessage(), e);
            return ApiResponseResult.failure("用户注册失败！");
        }
    }


    @ApiOperation(value = "修改密码", notes = "修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    public ApiResponseResult changePassword(@RequestParam(required=false) String loginName,
                                            @RequestParam(required=false) String oldPassword,
                                            @RequestParam(required=false) String password,
                                            @RequestParam(required = false) String rePassword){
        try{
            return sysUserService.changePassword(loginName, oldPassword, password, rePassword);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return ApiResponseResult.failure("修改密码失败！");
        }
    }

    @ApiOperation(value = "重置密码（管理员修改密码使用）", notes = "重置密码（管理员修改密码使用）")
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ApiResponseResult resetPassword(@RequestParam(required=false) Long id,
                                            @RequestParam(required=false) String password,
                                            @RequestParam(required = false) String rePassword){
        try{
            return sysUserService.resetPassword(id, password, rePassword);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            return ApiResponseResult.failure("重置密码失败！");
        }
    }
    
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "编码", dataType = "Long", paramType = "query", defaultValue = "")
    })
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public ApiResponseResult getUserInfo(@RequestParam(value = "token") String username ) {
        try {
            return sysUserService.getUserInfo(username);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            return ApiResponseResult.failure("用户注册失败！");
        }
    }

   
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    @RequestMapping(value = "/getlist", method = RequestMethod.GET)
    public ApiResponseResult getlist(@RequestParam(value = "usercode", required = false) String usercode,
                                     @RequestParam(value = "username", required = false) String username) {
        try {
        	//logger.error("test");
        	Sort sort = new Sort(Sort.Direction.DESC, "id");
            return sysUserService.getlist(usercode, username, super.getPageRequest(sort));
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            return ApiResponseResult.failure("获取用户列表失败！");
        }
    }
}
