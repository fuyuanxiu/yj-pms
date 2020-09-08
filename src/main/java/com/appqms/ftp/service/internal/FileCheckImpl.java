package com.appqms.ftp.service.internal;

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
import com.appqms.ftp.dao.FileCheckDao;
import com.appqms.ftp.service.FileCheckService;

@Service(value = "fileCheckService")
@Transactional(propagation = Propagation.REQUIRED)
public class FileCheckImpl implements FileCheckService {

	@Autowired
    private FileCheckDao fileCheckDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FtpClientService ftpClientService;
	
	
	@Override
	public ApiResponseResult uploadFileIqc(String factory, String company, String username, int mid, int type,String note, MultipartFile[] files)
			throws Exception {
		// TODO Auto-generated method stub
		List<String> a = getInfo(factory,company,username,mid,type);
		System.out.println(a);
		//[0, null, 192.168.1.160, ftp160, ftp-160, 21, QMS/11/37]
		if(a.get(0).equals("0")){
			//return ApiResponseResult.success("操作成功!").data(a);
			try{
				/*String[] filenames = new String[files.length];
				InputStream[] inputs = new InputStream[files.length];*/
				int i=0;
				for(MultipartFile file:files){
					/*filenames[i] = file.getOriginalFilename();
					inputs[i] = new ByteArrayInputStream(file.getBytes());*/
					String a6 = a.get(6);//.replace("/",File.separator);

					ApiResponseResult ar = ftpClientService.uploadFile(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a6,file.getOriginalFilename(),new ByteArrayInputStream(file.getBytes()));
					if(ar.getStatus().equals("0")){
						//ftp上传成功
						List<String> b = this.getIqcFile(factory, company, username, mid, type, 0, file.getOriginalFilename(), note, 0);
						System.out.println(b);
					}
				}
				//ftpClientService.uploadFiles(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a.get(6),filenames,inputs);
				
				//(String url, int port, String username, String password, String path, String filename, InputStream input
				/* return ftpClientService.uploadFile(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a.get(6),"test.png",
						 new ByteArrayInputStream(file.getBytes()));*/
				return ApiResponseResult.success("操作成功!");
			}catch(Exception e){
				System.out.println(e.toString());
				return ApiResponseResult.failure("上传文件到FTP失败!").data(e.toString());
			}
		}else{
			return ApiResponseResult.failure("获取FTP信息失败!").data(a.get(1));
		}
	}
	
	/**
	 * 
	 * 获取ftp的信息
	 * @param factory
	 * @param company
	 * @param uaername
	 * @param mid
	 * @param type1
	 * @return
	 */
	public List<String> getInfo(String factory,String company,String uaername,int mid,int type1) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Iqc_Get_Accpath(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setLong(4,mid);// 
		cs.setLong(5,type1);//
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(12,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(6));
		result.add(cs.getString(7));
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		result.add(cs.getString(12));
		return result;
		}
		});
		return resultList;

		}
	/**
	 * 文件操作
	 * @param factory
	 * @param company
	 * @param uaername
	 * @param mid
	 * @param type1
	 * @param Dmltype
	 * @param filename
	 * @param note
	 * @param fileid
	 * @return
	 */
	public List<String> getIqcFile(String factory,String company,String uaername,int mid,int type1,int Dmltype,String filename,String note,Integer fileid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Iqc_Insert_Accpath(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setLong(4,mid);// 
		cs.setLong(5,type1);//业务类型，11是IQC检验，71是IQC定期检验
		cs.setLong(6, Dmltype);//操作类型，0是新增,1是修改，2是删除
		cs.setString(7, filename);//文件名
		cs.setString(8, note);//备注说明
		cs.setLong(9, fileid);//附件记录ID，Pi_Dmltype<>0的时候不允许为空
		cs.registerOutParameter(10,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		return result;
		}
		});
		return resultList;

		}
	
	/**
     * 上传文件
     * @param fsFile
     * @param file
     * @return
     * @throws Exception
     */
/*    public ApiResponseResult upload(FsFile fsFile, MultipartFile file) throws Exception {
        if(null==file || file.isEmpty()) {
            return ApiResponseResult.failure("上传文件不能为空");
        }
        String qmsPath = env.getProperty("fs.qms.path");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String ymd = sdf.format(new Date());

        String path = qmsPath + "/" + ymd;

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateFileName = df.format(new Date()) + "_" + new Random().nextInt(1000);

        try {
            fsFile.setBsFileSize(file.getSize());
            if(null==fsFile.getBsContentType()) {
                fsFile.setBsContentType(file.getContentType());
            }
            if(null==file.getOriginalFilename()) {
                fsFile.setBsFileType("Unknown");
                return ApiResponseResult.failure("无法识别该文件类型！");
            }

            String originalFiletype = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."), file.getOriginalFilename().length());
            fsFile.setBsFileType(originalFiletype);

//            String originalFilename = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
            fsFile.setBsName(file.getOriginalFilename());
            fsFile.setBsFileName(dateFileName + originalFiletype);
            fsFile.setBsFilePath("/"+ymd);
            ApiResponseResult result = ftpClientService.uploadFile(path, dateFileName+fsFile.getBsFileType(), new ByteArrayInputStream(file.getBytes()));
            if(result.isResult()) {
                fsFile.setCreatedTime(new Date());
                fileDao.save(fsFile);
                return ApiResponseResult.success("文件上传成功！").data(fsFile);
            }
        } catch (Exception e) {
            //logger.error("upload file exception", e);
        }
        return ApiResponseResult.failure("上传文件发生异常");
    }*/

    /**
     * 下载文件
     * @param fsFileId
     * @param response
     * @return
     * @throws Exception
     */
/*    public ApiResponseResult get(Long fsFileId, HttpServletResponse response) throws Exception {
        Optional<FsFile> fsFiles = fsFileDao.findById(fsFileId);
        if(null==fsFiles) {
            return ApiResponseResult.failure("文件不存在或已被删除");
        }
        FsFile fsFile = fsFiles.get();
        String path = env.getProperty("fs.qms.path")+fsFile.getBsFilePath();
        ApiResponseResult result = ftpClientService.download(path, fsFile.getBsFileName());
        try {
//            String fileName = new String(fsFile.getBsName().getBytes("UTF-8"), "ISO-8859-1")+ fsFile.getBsFileType();
            String fileName = URLEncoder.encode(fsFile.getBsName(), "UTF-8");
//            response.setContentType("application/octet-stream");
            response.setContentType(fsFile.getBsContentType());
            // 设置response的Header
//            response.setHeader("Content-Disposition", "attachment;filename=" + new String((fsFile.getBsName()+fsFile.getBsFileType()).getBytes("UTF-8"), "ISO-8859-1"));
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName );
            response.addHeader("Content-Length", "" + fsFile.getBsFileSize());
            OutputStream os = response.getOutputStream();
            byte[] bytes = (byte[]) result.getData();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            logger.error("download file exception", e);
        }
        return null;
    }*/

    /**
     * 图片在线预览（非图片下载）
     * @param fsFileId
     * @param response
     * @return
     * @throws Exception
     */
/*    public ApiResponseResult onlineView(Long fsFileId, HttpServletResponse response) throws Exception {
    	Optional<FsFile> fsFiles = fsFileDao.findById(fsFileId);
        if(null==fsFiles) {
            return ApiResponseResult.failure("文件不存在或已被删除");
        }
        FsFile fsFile = fsFiles.get();
        String path = env.getProperty("fs.qms.path")+fsFile.getBsFilePath();
        ApiResponseResult result = ftpClientService.download(path, fsFile.getBsFileName());
        try {
            String fileName = URLEncoder.encode(fsFile.getBsName(), "UTF-8");  //文件名称
            String extName = fsFile.getBsFileType();  //文件后缀名
            response.setContentType(fsFile.getBsContentType());
            response.addHeader("Content-Disposition", "inline;filename=" + fileName );
            response.addHeader("Content-Length", "" + fsFile.getBsFileSize());
//            if(".png".equals(extName)){
//                response.setContentType("image/png");
//            }
            OutputStream os = response.getOutputStream();
            byte[] bytes = (byte[]) result.getData();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            logger.error("download file exception", e);
        }
        return null;
    }*/


	@Override
	public ApiResponseResult getFilesList(String mid) throws Exception {
		// TODO Auto-generated method stub
		//List<Map<String, Object>> countList1 = fileCheckDao.getFileList(mid);
		List<String> a = this.getRfFileList(mid);
		/* if(countList1.size() == 0){
	        	return ApiResponseResult.failure("文件为空");
	      }
		 return ApiResponseResult.success("").data(countList1);*/
		System.out.println(a);
		if(a.get(0).equals("0")){
			String[] fn = a.get(2).substring(0,a.get(2).length()-1).split("#");
			String[] fq = a.get(3).substring(0,a.get(3).length()-1).split("#");
			String[] ft = a.get(4).substring(0,a.get(4).length()-1).split("#");
			String[] mi = a.get(5).substring(0,a.get(5).length()-1).split("#");
			String[] cd = a.get(6).substring(0,a.get(6).length()-1).split("#");
			String[] fu = a.get(7).substring(0,a.get(7).length()-1).split("#");
			List l = new ArrayList();
			for(int i=0;i<fn.length;i++){
				Map m = new HashMap();
				m.put("FNOTE", fn[i]);
				m.put("FATTACH", fq[i]);
				m.put("FTYPE", ft[i]);
				m.put("MID", mi[i]);
				m.put("CD", cd[i]);
				m.put("FURL", fu[i]);
				
				
				l.add(m);
			}
			
			
			
			return ApiResponseResult.success().data(l);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	 public List<String> getRfFileList(String mid) {
	        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_ftp_file_list(?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, mid);
	                cs.registerOutParameter(2,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
	                cs.registerOutParameter(4,-10);// 注册输出参数 返回类型 返回批次号游标
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<String> result = new ArrayList<String>();
	                cs.execute();
	                result.add(cs.getString(2));
	                result.add(cs.getString(3));
	                //游标处理
	                ResultSet rs = (ResultSet)cs.getObject(4);
	        		String ids = "";String n = "";String n1 = "";String n2 = "";
	        		String n3 = "";String n4 = "";
	        		while(rs.next()){
	        			ids += rs.getString("FNOTE")+"#";
	        			n += rs.getString("FATTACH")+"#";
	        			n1 += rs.getString("FTYPE")+"#";
	        			n2 += rs.getString("MID")+"#";
	        			n3 += rs.getString("CD")+"#";
	        			n4 += rs.getString("FURL")+"#";
	        			 }
	        		result.add(ids);
	        		result.add(n);
	        		result.add(n1);
	        		result.add(n2);
	        		result.add(n3);
	        		result.add(n4);
	                return result;
	            }
	        });
	        return resultList;

	    }


	@Override
	public ApiResponseResult uploadFileIpqc(String factory, String company, String username, int mid, int type,
			String note, MultipartFile[] files) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = getInfoIpqc(factory,company,username,mid,type);
		System.out.println(a);
		//[0, null, 192.168.1.160, ftp160, ftp-160, 21, QMS/11/37]
		if(a.get(0).equals("0")){
			//return ApiResponseResult.success("操作成功!").data(a);
			try{
				/*String[] filenames = new String[files.length];
				InputStream[] inputs = new InputStream[files.length];*/
				int i=0;
				for(MultipartFile file:files){
					/*filenames[i] = file.getOriginalFilename();
					inputs[i] = new ByteArrayInputStream(file.getBytes());*/
					ApiResponseResult ar = ftpClientService.uploadFile(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a.get(6),file.getOriginalFilename(),new ByteArrayInputStream(file.getBytes()));
					if(ar.getStatus().equals("0")){
						//ftp上传成功
						List<String> b = this.getIpqcFile(factory, company, username, mid, type, 0, file.getOriginalFilename(), note, 0);
						System.out.println(b);
					}
				}
				//ftpClientService.uploadFiles(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a.get(6),filenames,inputs);
				
				//(String url, int port, String username, String password, String path, String filename, InputStream input
				/* return ftpClientService.uploadFile(a.get(2),Integer.parseInt(a.get(5)),a.get(3),a.get(4),a.get(6),"test.png",
						 new ByteArrayInputStream(file.getBytes()));*/
				return ApiResponseResult.success("操作成功!");
			}catch(Exception e){
				System.out.println(e.toString());
				return ApiResponseResult.success("上传文件到FTP失败!").data(a.get(1));
			}
		}else{
			return ApiResponseResult.success("获取FTP信息失败!").data(a.get(1));
		}
	}
	/**
	 * 
	 * 获取ftp-IPQC的信息
	 * @param factory
	 * @param company
	 * @param uaername
	 * @param mid
	 * @param type1
	 * @return
	 */
	public List<String> getInfoIpqc(String factory,String company,String uaername,int mid,int type1) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Ipqc_Get_Accpath(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setLong(4,mid);// 
		cs.setLong(5,type1);//
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(12,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(6));
		result.add(cs.getString(7));
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		result.add(cs.getString(12));
		return result;
		}
		});
		return resultList;

		}
	
	/**
	 * 文件操作
	 * @param factory
	 * @param company
	 * @param uaername
	 * @param mid
	 * @param type1
	 * @param Dmltype
	 * @param filename
	 * @param note
	 * @param fileid
	 * @return
	 */
	public List<String> getIpqcFile(String factory,String company,String uaername,int mid,int type1,int Dmltype,String filename,String note,Integer fileid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Ipqc_Insert_Accpath(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setLong(4,mid);// 
		cs.setLong(5,type1);//业务类型，11是IQC检验，71是IQC定期检验
		cs.setLong(6, Dmltype);//操作类型，0是新增,1是修改，2是删除
		cs.setString(7, filename);//文件名
		cs.setString(8, note);//备注说明
		cs.setLong(9, fileid);//附件记录ID，Pi_Dmltype<>0的时候不允许为空
		cs.registerOutParameter(10,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult onlineView(String url, String fname,HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		//获取ftp地址账号密码以及端口号
		String ip = fileCheckDao.queryFtpServerIP().get(0).get("PV").toString();
		String num = fileCheckDao.queryFtpPortNum().get(0).get("PV").toString();
		String name = fileCheckDao.queryFtpUser().get(0).get("PV").toString();
		String psw = fileCheckDao.queryFtpPsw().get(0).get("PV").toString();
		 ApiResponseResult result = ftpClientService.download(ip, Integer.parseInt(num), name, psw,
				 url, fname);
		 try{
			 String fileName = URLEncoder.encode(fname, "UTF-8");  //文件名称
			 //String extName = fsFile.getBsFileType();  //文件后缀名
	            response.setContentType("image/png");
	            response.addHeader("Content-Disposition", "inline;filename=" + fileName );
	            //response.addHeader("Content-Length", "" + fsFile.getBsFileSize());
//	            if(".png".equals(extName)){
//	                response.setContentType("image/png");
//	            }
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
