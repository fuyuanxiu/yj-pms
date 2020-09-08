package com.appqms.ftp.controller;


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
import com.appqms.ftp.service.FileCheckService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(description = "附件管理")
@CrossOrigin
@ControllerAdvice
@RestController
@RequestMapping(value = "/file")
public class FileCheckController {

    @Autowired
    private FileCheckService fileCheckService;

	
	@ApiOperation(value = "获取ftp的基础数据", notes = "获取ftp的基础数据")
	@RequestMapping(value = "/uploadFileIqc", method = RequestMethod.POST)
	public ApiResponseResult uploadFileIqc(@RequestParam(value = "type1") String type,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "mid") String mid,@RequestParam(value = "username") String username,
			@RequestParam(value = "note") String note, @RequestParam("file0") MultipartFile[] files0,
			@RequestParam("file1") MultipartFile[] files1,@RequestParam("file2") MultipartFile[] files2,
			@RequestParam("file3") MultipartFile[] files3,@RequestParam("file4") MultipartFile[] files4,
			@RequestParam("file5") MultipartFile[] files5,@RequestParam("file6") MultipartFile[] files6,
			@RequestParam("file7") MultipartFile[] files7,@RequestParam("file8") MultipartFile[] files8) {		
      try {
    	  //11是IQC检验，71是IQC定期检验
    	  MultipartFile[]  files =  new MultipartFile[9];
    	  if(files0.length > 0){
    		  files[0] = files0[0];
    	  }
    	  if(files1.length > 0){
    		  files[1] = files1[0];
    	  }
    	  if(files2.length > 0){
    		  files[2] = files2[0];
    	  }
    	  if(files3.length > 0){
    		  files[3] = files3[0];
    	  }
    	  if(files4.length > 0){
    		  files[4] = files4[0];
    	  }
    	  if(files5.length > 0){
    		  files[5] = files5[0];
    	  }
    	  if(files6.length > 0){
    		  files[6] = files6[0];
    	  }
    	  if(files7.length > 0){
    		  files[7] = files7[0];
    	  }
    	  if(files8.length > 0){
    		  files[8] = files8[0];
    	  }
    	  return fileCheckService.uploadFileIqc(factory,company,username,Integer.parseInt(mid),Integer.parseInt(type),note,files);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("获取ftp的基础数据失败！");
        }
	}
	
	@ApiOperation(value = "获取ftp的基础数据", notes = "获取ftp的基础数据")
	@RequestMapping(value = "/uploadFileIpqc", method = RequestMethod.POST)
	public ApiResponseResult uploadFileIpqc(@RequestParam(value = "type1") String type,
			@RequestParam(value = "company") String company,@RequestParam(value = "factory") String factory,
			@RequestParam(value = "mid") String mid,@RequestParam(value = "username") String username,
			@RequestParam(value = "note") String note, @RequestParam("file0") MultipartFile[] files0,
			@RequestParam("file1") MultipartFile[] files1,@RequestParam("file2") MultipartFile[] files2,
			@RequestParam("file3") MultipartFile[] files3,@RequestParam("file4") MultipartFile[] files4,
			@RequestParam("file5") MultipartFile[] files5,@RequestParam("file6") MultipartFile[] files6,
			@RequestParam("file7") MultipartFile[] files7,@RequestParam("file8") MultipartFile[] files8) {		
      try {
    	  //11是IQC检验，71是IQC定期检验
    	  MultipartFile[]  files =  new MultipartFile[9];
    	  if(files0.length > 0){
    		  files[0] = files0[0];
    	  }
    	  if(files1.length > 0){
    		  files[1] = files1[0];
    	  }
    	  if(files2.length > 0){
    		  files[2] = files2[0];
    	  }
    	  if(files3.length > 0){
    		  files[3] = files3[0];
    	  }
    	  if(files4.length > 0){
    		  files[4] = files4[0];
    	  }
    	  if(files5.length > 0){
    		  files[5] = files5[0];
    	  }
    	  if(files6.length > 0){
    		  files[6] = files6[0];
    	  }
    	  if(files7.length > 0){
    		  files[7] = files7[0];
    	  }
    	  if(files8.length > 0){
    		  files[8] = files8[0];
    	  }
    	  return fileCheckService.uploadFileIpqc(factory,company,username,Integer.parseInt(mid),Integer.parseInt(type),note,files);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("获取ftp的基础数据失败！");
        }
	}
	
	@ApiOperation(value = "查询该检验项目的所有照片", notes = "查询该检验项目的所有照片")
	@RequestMapping(value = "/getFilesList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getFilesList(@RequestParam(value = "mid") String mid) {		
      try {
    	  return fileCheckService.getFilesList(mid);
        } catch (Exception e) {
        	System.out.println(e.toString());
            return ApiResponseResult.failure("查询文件列表失败！");
        }
	}
	
	@ApiOperation(value="下载文件", notes="下载文件")
	@RequestMapping(value = "/getFile", method = RequestMethod.GET)
	public void get(@RequestParam(value = "fsFileId", required = true) Long fsFileId) {
		try {
			//fileCheckService.getFile(fsFileId, getResponse());
		} catch (Exception e) {
			System.out.println(e.toString());
           // return ApiResponseResult.failure("查询批次号失败！");
		}
	}

	@ApiOperation(value="图片在线预览", notes="图片在线预览")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public void view(@RequestParam(value = "url", required = true) String url,@RequestParam(value = "fname", required = true) String fname) {
		try {
			fileCheckService.onlineView(url, fname,getResponse());
		} catch (Exception e) {
			System.out.println(e.toString());
           // return ApiResponseResult.failure("查询批次号失败！");
		}
	}
	
	/**
	 * 获取response
	 * @return
	 */
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
