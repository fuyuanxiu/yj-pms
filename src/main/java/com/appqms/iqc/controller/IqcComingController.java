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
import com.appqms.iqc.service.IqcComingService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;


@Api(description = "IQC来料管理模块")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/iqcComing")
public class IqcComingController {

    @Autowired
    private IqcComingService iqcComingService;
    
    @ApiOperation(value = "查询IQC来料报检记录", notes = "查询IQC来料报检记录")
	@RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getList(@RequestParam(value = "factory") String factory, @RequestParam(value = "company") String company,
			 @RequestParam(value = "keyword") String keyword,@RequestParam(value = "page")String page) {		
      try {
    	  return iqcComingService.getList( factory,  company, keyword,page);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询批次号失败！");
        }
	}
    

}
