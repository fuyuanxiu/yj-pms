package com.appqms.esop.service.internal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.app.base.data.ApiResponseResult;
import com.app.base.service.FtpClientService;
import com.app.query.dao.Parameter;
import com.app.query.dao.SQLParameter;
import com.appqms.esop.dao.ESOPCheckDao;
import com.appqms.esop.service.ESOPService;
import com.appqms.ftp.dao.FileCheckDao;
import com.appqms.ftp.service.FileCheckService;

@Service(value = "ESOPService")
@Transactional(propagation = Propagation.REQUIRED)
public class ESOPImpl implements ESOPService {

	@Autowired
    private ESOPCheckDao esopCheckDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FtpClientService ftpClientService;
	

	/**
	 * 图片在线预览（非图片下载）
	 * 
	 * @param fsFileId
	 * @param response
	 * @return
	 * @throws Exception
	 */
	/*
	 * public ApiResponseResult onlineView(Long fsFileId, HttpServletResponse
	 * response) throws Exception { Optional<FsFile> fsFiles =
	 * fsFileDao.findById(fsFileId); if(null==fsFiles) { return
	 * ApiResponseResult.failure("文件不存在或已被删除"); } FsFile fsFile = fsFiles.get();
	 * String path = env.getProperty("fs.qms.path")+fsFile.getBsFilePath();
	 * ApiResponseResult result = ftpClientService.download(path,
	 * fsFile.getBsFileName()); try { String fileName =
	 * URLEncoder.encode(fsFile.getBsName(), "UTF-8"); //文件名称 String extName =
	 * fsFile.getBsFileType(); //文件后缀名
	 * response.setContentType(fsFile.getBsContentType());
	 * response.addHeader("Content-Disposition", "inline;filename=" + fileName
	 * ); response.addHeader("Content-Length", "" + fsFile.getBsFileSize()); //
	 * if(".png".equals(extName)){ // response.setContentType("image/png"); // }
	 * OutputStream os = response.getOutputStream(); byte[] bytes = (byte[])
	 * result.getData(); os.write(bytes); os.flush(); os.close(); } catch
	 * (IOException e) { logger.error("download file exception", e); } return
	 * null; }
	 */

	@Override
	public ApiResponseResult getFilesList(String proNo,
    		String proc,
    		String ver,
    		String page,
    		String size,
    		String renum) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getRfFileList(proNo,proc,ver,page,size,renum);
		//System.out.println(list.get(0).toString());
		//System.out.println(list.toString());
		//System.out.println(list.get(2).toString());
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
			return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
		} 
		//return ApiResponseResult.success().data(list.get(2));//返回数据集合
		/*轮播处理*/
		List<Map<String, Object>> l = (List<Map<String, Object>>) list.get(2);
		List<Map<String, Object>> l_new =l;
		int len=l.size();
		l_new.add(l.get(0));//第一张加入最后一张
		l_new.add(0,l.get(len-1));//最后一张放在第一位
		
		return ApiResponseResult.success().data(l_new);//返回数据集合
	}
	 public List getRfFileList(String proNo,
	    		String proc,
	    		String ver,
	    		String page,
	    		String size,
	    		String renum) {
	        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appesop_ftplist(?,?,?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, proNo);//产品编码
	                cs.setString(2, proc);//工序
	                cs.setString(3, ver);//版本
	                cs.setString(4, page);//每页记录数
	                cs.setString(5, size);//当前页码
	                cs.setString(6, renum);//总记录数
	                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
	                cs.registerOutParameter(9,-10);// 注册输出参数 返回类型 返回游标
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<Object> result = new ArrayList<Object>();
	                List<Map<String, Object>> l = new ArrayList();
	                cs.execute();
	                result.add(cs.getString(7));
	                result.add(cs.getString(8));
	                if (cs.getString(7).toString().equals("0")) {
						// 游标处理
						ResultSet rs = (ResultSet) cs.getObject(9);

						try {
							l = fitMap(rs);
						} catch (Exception e) {
							e.printStackTrace();
						}
						result.add(l);
					}
					System.out.println(l);
	                return result;
	            }
	        });
	        return resultList;
	    }
	 private List<Map<String, Object>> fitMap(ResultSet rs) throws Exception {
			List<Map<String, Object>> list = new ArrayList<>();
			if (null != rs) {
				Map<String, Object> map;
				int colNum = rs.getMetaData().getColumnCount();
				List<String> columnNames = new ArrayList<String>();
				for (int i = 1; i <= colNum; i++) {
					columnNames.add(rs.getMetaData().getColumnName(i));
				}
				while (rs.next()) {
					map = new HashMap<String, Object>();
					for (String columnName : columnNames) {
						map.put(columnName, rs.getString(columnName));
					}
					list.add(map);
				}
			}
			return list;
		}
	 @Override
		public ApiResponseResult onlineView(String url, String fname,HttpServletResponse response) throws Exception {
			// TODO Auto-generated method stub
			//获取ftp地址账号密码以及端口号
			String ip = esopCheckDao.queryFtpServerIP().get(0).get("PV").toString();
			String num = esopCheckDao.queryFtpPortNum().get(0).get("PV").toString();
			String name = esopCheckDao.queryFtpUser().get(0).get("PV").toString();
			String psw = esopCheckDao.queryFtpPsw().get(0).get("PV").toString();
			 ApiResponseResult result = ftpClientService.download(ip, Integer.parseInt(num), name, psw,
					 url, fname);
			 try{
				 String fileName = URLEncoder.encode(fname, "UTF-8");  //文件名称
				 //String extName = fsFile.getBsFileType();  //文件后缀名
		            response.setContentType("image/png");
		            response.addHeader("Content-Disposition", "inline;filename=" + fileName );
		            //response.addHeader("Content-Length", "" + fsFile.getBsFileSize());
//		            if(".png".equals(extName)){
//		                response.setContentType("image/png");
//		            }
		            OutputStream os = response.getOutputStream();
		            byte[] bytes = (byte[]) result.getData();
		            os.write(bytes);
		            os.flush();
		            os.close();
			 }catch (Exception e) {
				 System.out.println(e.toString());
			 }
			return null;
		}
}
