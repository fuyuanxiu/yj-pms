package com.appqms.repair.controller;


import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.data.ApiResponseResult;
import com.appqms.repair.service.RepairService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(description = "不良品管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;
    
    @ApiOperation(value = "查询站别", notes = "查询站别")
   	@RequestMapping(value = "/getLotList", method = RequestMethod.POST, produces = "application/json")
   	public ApiResponseResult getLotList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory) {		
         try {
       	  return repairService.getLotList(company,factory);
           } catch (Exception e) {
           	System.out.println(e.toString());
               return ApiResponseResult.failure("查询批次号失败！");
           }
   	}
    
    @ApiOperation(value = "查询站别", notes = "查询站别")
	@RequestMapping(value = "/getStatList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getStatList(@RequestParam(value = "company")String company,@RequestParam(value = "factory")String factory,
			@RequestParam(value = "fid")String fid) {		
      try {
    	  return repairService.getStatList(company,factory,fid);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}

    
    @ApiOperation(value = "保存", notes = "保存")
	@RequestMapping(value = "/saveRepair", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult saveRepair(@RequestParam(value = "factory")String factory,@RequestParam(value = "company")String company,
			@RequestParam(value = "usercode")String usercode,
			@RequestParam(value = "ngid")String ngid,@RequestParam(value = "fstate")String fstate,
			@RequestParam(value = "deal")String deal,@RequestParam(value = "qty")String qty,@RequestParam(value = "note")String note,
			@RequestParam(value = "barcode")String barcode) {		
      try {
    	  deal = URLDecoder.decode(deal,"UTF-8");
    	  return repairService.saveRepair(factory,company,usercode,ngid,fstate,deal,qty,note,barcode);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    
    @ApiOperation(value = "查询待维修的工单列表", notes = "查询待维修的工单列表")
   	@RequestMapping(value = "/getUnList", method = RequestMethod.POST, produces = "application/json")
   	public ApiResponseResult getUnList(@RequestParam(value = "page")String page,@RequestParam(value = "lot_no")String lot_no) {		
         try {
       	  return repairService.getUnList(page,lot_no);
           } catch (Exception e) {
           	System.out.println(e.toString());
               return ApiResponseResult.failure("查询所有不良数据失败！");
           }
   	}
    
    
    @ApiOperation(value = "查询所有不良数据", notes = "查询所有不良数据")
   	@RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json")
   	public ApiResponseResult getList(@RequestParam(value = "page")String page,@RequestParam(value = "result")String result) {		
         try {
       	  return repairService.getList(page,result);
           } catch (Exception e) {
           	System.out.println(e.toString());
               return ApiResponseResult.failure("查询所有不良数据失败！");
           }
   	}
    

    
}
