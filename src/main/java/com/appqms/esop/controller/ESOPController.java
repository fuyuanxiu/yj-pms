package com.appqms.esop.controller;


import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.app.base.data.ApiResponseResult;
import com.appqms.esop.service.ESOPService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(description = "附件管理")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/esop")
public class ESOPController {

    @Autowired
    private ESOPService esopService;

	
	@ApiOperation(value = "查询该检验项目的所有照片", notes = "查询该检验项目的所有照片")
	@RequestMapping(value = "/getFilesList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFilesList(@RequestParam(value = "proNo") String proNo,
			@RequestParam(value = "proc") String proc, @RequestParam(value = "ver") String ver,
			@RequestParam(value = "page") String page, @RequestParam(value = "size") String size,
			@RequestParam(value = "renum") String renum){
		try {
			return esopService.getFilesList(proNo, proc, ver, page, size, renum);
		} catch (Exception e) {
			System.out.println(e.toString());
			return ApiResponseResult.failure("获取图片失败！");
		}
	}
	
	@ApiOperation(value="图片在线预览", notes="图片在线预览")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public void view(@RequestParam(value = "url", required = true) String url,@RequestParam(value = "fname", required = true) String fname) {
		try {
			esopService.onlineView(url, fname,getResponse());
		} catch (Exception e) {
			System.out.println(e.toString());
           // return ApiResponseResult.failure("查询批次号失败！");
		}
	}
	
	protected HttpServletResponse getResponse() {
        return getServletRequestAttributes().getResponse();
    }
	/**
	 * 获取servlet属性
	 * @return
	 */
	protected final ServletRequestAttributes getServletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }
	
    
}
