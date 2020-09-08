package com.system.interfaces.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.app.base.data.DataGrid;
import com.app.base.utils.MD5Util;
import com.auth0.jwt.JWT;
import com.system.interfaces.dao.InterfaceDao;
import com.system.interfaces.service.InterfaceService;
import com.system.user.entity.SysUser;
import com.utils.UserUtil;
import com.utils.enumeration.BasicStateEnum;

@Service(value = "interfaceService")
@Transactional(propagation = Propagation.REQUIRED)
public class InterfaceImpl implements InterfaceService {

    @Autowired
    private InterfaceDao interfaceDao;

    
    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public String queryVersion() throws Exception {
		List<Map<String, Object>> countList = interfaceDao.queryVersion();
		if(countList.size() == 0){
			return "mesdev";
		}else{
			Map<String, Object> m = countList.get(0);
			return m.get("PARAM_VALUE").toString();
		}
	}
	@Override
	public String queryRunEnv() throws Exception {
		List<Map<String, Object>> countList = interfaceDao.queryRunEnv();
		if(countList.size() == 0){
			return "mesdev";
		}else{
			Map<String, Object> m = countList.get(0);
			return m.get("PARAM_VALUE").toString();
		}
	}
	@Override
	public String queryPurview(String userno,String pmachtype) throws Exception {
		// TODO Auto-generated method stub
		String sql = "{call Prc_rf_j1_user_login(?,?,?)}";
		String[][] param={{"1",userno},{"1",pmachtype}};
		String[] outParam={"1"};
		//List<String> a = this.doPurview(userno,pmachtype);
		List<String> a = this.doProcedure(sql, param, outParam);
		if(a.size() > 0){
			return a.get(0);	
		}
		return "";
	}
	
	public List<String> doProcedure(String sql,String[][] param,String[] outParam){
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
			String storedProc = sql;//"{call Prc_rf_j1_user_login(?,?,?)}";// 调用的sql
			CallableStatement cs = con.prepareCall(storedProc);
			for(int i=0;i<param.length;i++){
	            if(param[i][0].equals("0") ){
	            	cs.setInt(i+1, Integer.parseInt( param[i][1]));
	            }else{
	            	cs.setString(i+1, param[i][1]);
	            }
	        }
			for(int o=0;o<outParam.length;o++){
				if(outParam[o].equals("0") ){
					cs.registerOutParameter(param.length+o+1,java.sql.Types.INTEGER);// 注册输出参数 返回类型
	            }else{
	            	cs.registerOutParameter(param.length+o+1,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
	            }
			}
			return cs;
			}
			}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
			List<String> result = new ArrayList<String>();
			cs.execute();

			//result.add(cs.getString(3));
			for(int o=0;o<outParam.length;o++){
				result.add(cs.getString(param.length+o+1));
			}
			return result;
			}
			});
			return resultList;
	}
	public List<String> doPurview(String userno,String pmachtype) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Prc_rf_j1_user_login(?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, userno.toUpperCase());
		cs.setString(2, pmachtype);
		cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(3));
		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getRfSetup(String functionName) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.doRfSetup(functionName);
//		[001
//		 [FILED4#进货仓库#VARCHAR2#0#3#60#245#N##1##0##0#23
//		 [FILED2#加急#VARCHAR2#0#1#60#245#N##1##0##0#23
//		 [FILED3#卡板号#VARCHAR2#1#3#60#245#N##1#FILED5#0#999999#0#23
//		 [FILED5#物料条码#VARCHAR2#0#3#60#245#P#{FILED4,FILED3,FILED5,FILED1,FILED2}{FILED1,FILED6,FILED7,INFO}#0##0##0#23
//		 [FILED1#采购单号#VARCHAR2#1#3#60#245#P#{FILED1}{FILED4,FILED3}#1#FILED5#0##0#23
//		 [FILED6#物料编码#VARCHAR2#1#3#60#245#N##1##0##0#23
//		 [FILED7#进货数量#VARCHAR2#1#3#60#245#N##1##0###23
//		 [INFO#提示信息#MEMO#1#3#60#245#N##1##0###100]
		if(a.size()>0){
			String s = a.get(0).substring(0);
			String[] strs = s.split("\\[");
			if(strs.length<1){
				return ApiResponseResult.failure("返回值的格式不正确!"+a);
			}
			//判断取值是否成功
			String str = strs[0];
			if(str.equals("002")){
				return ApiResponseResult.failure("取值发生错误!"+a);
			}
			//拼接字符串
			
			List<String> list = Arrays.asList(strs);
			List arr = new ArrayList<>();
			for(int i=1;i<list.size();i++){
				String[] s1 = list.get(i).split("#");
				arr.add(s1);
			}
			return ApiResponseResult.success("功能界面成功！").data(arr);
		}else{
			return ApiResponseResult.failure("取值为空，请检测输入的参数是否正确!");
		}
		
		//return ApiResponseResult.success("功能界面成功！").data(arr);
	}
	public List<String> doRfSetup(String functionName) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Prc_rf_setup(?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, functionName);
		cs.setString(2, "WCE");
		cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(3));
		return result;
		}
		});


		return resultList;

		}


	@Override
	public ApiResponseResult queryAppVersion() throws Exception {
		// TODO Auto-generated method stub 
		Map m = new HashMap();
		 List<Map<String, Object>> l = interfaceDao.queryAppVersion();
		 if(l.size() > 0){
			 m.put("Version", l.get(0).get("PV"));
		 }else{
			 return ApiResponseResult.failure("未设置更新版本");
		 }
		 
		 l = interfaceDao.queryApkUrl();
		 if(l.size() > 0){
			 m.put("Url", l.get(0).get("PV"));
		 }else{
			 return ApiResponseResult.failure("未设置更新版本的下载地址");
		 }
		 
		 l = interfaceDao.queryAppSize();
		 if(l.size() > 0){
			 m.put("Size", l.get(0).get("PV"));
		 }else{
			 m.put("Size", 0);
		 }
		
		return ApiResponseResult.success().data(m);
	}


	@Override
	public ApiResponseResult changPsw(String userCode, String newp) throws Exception {
		// TODO Auto-generated method stub
		interfaceDao.updatePwsByUserCode(userCode, newp);
		return ApiResponseResult.success();
	}


	@Override
	public ApiResponseResult getExcProc(String functionName, String fileName, String pmachtype, String fileValue, String outFiles)
			throws Exception {
		// TODO Auto-generated method stub
		//[001[4500108372,80000123,100,物料80000123今日已收货 100]

//		List<String[]> a = new ArrayList<String[]>();
//		String str = "4500108372,80000123,100,物料80000123今日已收货 100]";
//		str = str.substring(0,str.length() - 1);
//		String[] files = outFiles.split(",");
//		String[] res = str.split(",");
//		for(int i=0;i<files.length;i++){
//			String[] temp = new String[2];
//			temp[0] = files[i];
//			temp[1] = res[i];
//			//m.put(files[i], res[i]);
//			a.add(temp);
//		}
//		return ApiResponseResult.success().data(a);
		List<String> a = this.doExcProc(functionName,fileName,pmachtype,fileValue);
		System.out.println(a);
		if(a.size()>0){
			String s = a.get(0).substring(0);
			String[] strs = s.split("\\[");
			if(strs.length<1){
				return ApiResponseResult.failure("返回值的格式不正确!"+a);
			}
			//判断取值是否成功
			String str = strs[0];
			if(str.equals("002")){
				return ApiResponseResult.failure(strs[1]);
			}else{
				if(StringUtils.isEmpty(outFiles)){
					if(strs.length < 2){
						return ApiResponseResult.success().data("");
					}
					return ApiResponseResult.success().data(strs[1]);
				}else{
					List<String[]> at = new ArrayList<String[]>();
					//[001[4500108372,80000123,100,物料80000123今日已收货 100]
					String[] files = outFiles.split(",");
					String[] res = strs[1].split(",");
					for(int i=0;i<files.length;i++){
						String[] temp = new String[2];
						temp[0] = files[i];
						temp[1] = res[i];
						//m.put(files[i], res[i]);
						at.add(temp);
					}
					return ApiResponseResult.success().data(at);
				}
				
			}
			
		}
		return ApiResponseResult.success().data(a);
	}

	public List<String> doExcProc(String functionName, String fileName, String pmachtype, String fileValue) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call  Prc_rf_setup_ExcProc(?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, functionName);//功能名称
		cs.setString(2, fileName.trim());//字段名
		cs.setString(3,"WCE");//设备类型 wince5,wmb5,wmb6
		cs.setString(4, fileValue);//参数值[第一位是用户]
		cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(5));
		return result;
		}
		});


		return resultList;

		}
	@Override
	public String getEqCon(String function_Name, String MachineCode, String XmlDate) throws Exception {
		// TODO Auto-generated method stub
		String sql = "{call Prc_Eq_Conn(?,?,?,?)}";
		String[][] param={{"1",function_Name},{"1",MachineCode},{"1",XmlDate}};
		String[] outParam={"1"};
		//List<String> a = this.doPurview(userno,pmachtype);
		List<String> a = this.doProcedure(sql, param, outParam);
		if(a.size() > 0){
			return a.get(0);	
		}
		return "";
	}

	
	
}
