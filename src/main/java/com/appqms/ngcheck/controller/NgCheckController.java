package com.appqms.ngcheck.controller;


import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.data.ApiResponseResult;
import com.appqms.ngcheck.service.NgCheckService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(description = "不良品管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/ng")
public class NgCheckController {

    @Autowired
    private NgCheckService ngCheckService;
    
    @ApiOperation(value = "查询站别", notes = "查询站别")
	@RequestMapping(value = "/getStatList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getStatList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory,
			@RequestParam(value = "lot_no")String lot_no) {		
      try {
    	  return ngCheckService.getStatList(company,factory,lot_no);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    
    @ApiOperation(value = "查询不良分类", notes = "查询不良分类")
	@RequestMapping(value = "/getNgtypeList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getNgtypeList(@RequestParam(value = "fstate")String fstate) {		
      try {
    	  return ngCheckService.getNgtypeList(fstate);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    
    @ApiOperation(value = "查询不良项目", notes = "查询不良项目")
	@RequestMapping(value = "/getNgcodeList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getNgcodeList(@RequestParam(value = "fstate")String fstate,@RequestParam(value = "ngtype")String ngtype) {		
      try {
    	  ngtype = URLDecoder.decode(ngtype,"UTF-8");
    	  return ngCheckService.getNgcodeList(fstate,ngtype);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    
    @ApiOperation(value = "保存", notes = "保存")
	@RequestMapping(value = "/saveNg", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult saveNg(@RequestParam(value = "factory")String factory,@RequestParam(value = "company")String company,
			@RequestParam(value = "usercode")String usercode,
			@RequestParam(value = "ngid")String ngid,@RequestParam(value = "fstate")String fstate,
			@RequestParam(value = "ngtype")String ngtype,@RequestParam(value = "ngcode")String ngcode,
			@RequestParam(value = "deal")String deal,@RequestParam(value = "qty")String qty,@RequestParam(value = "note")String note,
			@RequestParam(value = "barcode")String barcode) {		
      try {
    	  ngtype = URLDecoder.decode(ngtype,"UTF-8");
    	  ngcode = URLDecoder.decode(ngcode,"UTF-8");
    	  deal = URLDecoder.decode(deal,"UTF-8");
    	  return ngCheckService.saveNg(factory,company,usercode,ngid,fstate,ngtype,ngcode,deal,qty,note,barcode);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    
    
    @ApiOperation(value = "查询所有不良数据", notes = "查询所有不良数据")
   	@RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json")
   	public ApiResponseResult getList(@RequestParam(value = "page")String page) {		
         try {
       	  return ngCheckService.getList(page);
           } catch (Exception e) {
           	System.out.println(e.toString());
               return ApiResponseResult.failure("查询所有不良数据失败！");
           }
   	}
    

    
}
