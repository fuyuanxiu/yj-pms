package com.appqms.pointcheck.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.app.base.data.ApiResponseResult;
import com.appqms.pointcheck.dao.PointCheckDao;
import com.appqms.pointcheck.service.PointCheckService;

@Service(value = "pointCheckService")
@Transactional(propagation = Propagation.REQUIRED)
public class PointCheckImpl implements PointCheckService {

    @Autowired
    private PointCheckDao pointCheckDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult  getStatList(String company,String factory) throws Exception {
		// TODO Auto-generated method stub
        //List<Map<String, Object>> countList = pointCheckDao.getStatList(company,factory);//this.findBySql(sql, SQLParameter.newInstance(), null);
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
		String[] strArray={company,factory};String[] out = {"FNAME","FCODE"};
		Map result = this.getListRf(5, "{call APPQMS_DJ_STATE(?,?,?,?,?)}", strArray,out);
		if(result.get("Flag").toString().equals("0")){
			countList = (List<Map<String, Object>>)result.get("Result");
		}else{
			return ApiResponseResult.failure(result.get("Text").toString());
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}


	@Override
	public ApiResponseResult getLineList(String company, String factory, String fstate) throws Exception {
		// TODO Auto-generated method stub
		//List<Map<String, Object>> countList = pointCheckDao.getLineList(company, factory, fstate);
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
		String[] strArray={company,factory,fstate};String[] out = {"FNAME","FCODE"};
		Map result = this.getListRf(6, "{call APPQMS_DJ_LINE(?,?,?,?,?,?)}", strArray,out);
		if(result.get("Flag").toString().equals("0")){
			countList = (List<Map<String, Object>>)result.get("Result");
		}else{
			return ApiResponseResult.failure(result.get("Text").toString());
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}
	

	@Override
	public ApiResponseResult getProjectList(String company, String factory, String fstate, String fline) throws Exception {
		// TODO Auto-generated method stub
		//List<Map<String, Object>> countList = pointCheckDao.getProjectList(company, factory, fstate, fline);//this.findBySql(sql, SQLParameter.newInstance(), null);
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
		String[] strArray={company,factory,fstate,fline};String[] out = {"FNAME","FCODE"};
		Map result = this.getListRf(7, "{call APPQMS_DJ_PROJECT(?,?,?,?,?,?,?)}", strArray,out);
		if(result.get("Flag").toString().equals("0")){
			countList = (List<Map<String, Object>>)result.get("Result");
		}else{
			return ApiResponseResult.failure(result.get("Text").toString());
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}
	
	@Override
	public ApiResponseResult getBillNoList(String fstate, String fline,String proj) throws Exception {
		// TODO Auto-generated method stub
		//List<Map<String, Object>> countList = pointCheckDao.getBillNoList(fstate, fline, proj);
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
		String[] strArray={fstate,fline,proj};String[] out = {"ID","FNAME","FCODE"};
		Map result = this.getListRf(6, "{call APPQMS_DJ_ORDERS(?,?,?,?,?,?)}", strArray,out);
		if(result.get("Flag").toString().equals("0")){
			countList = (List<Map<String, Object>>)result.get("Result");
		}else{
			return ApiResponseResult.failure(result.get("Text").toString());
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}


	@Override
	public ApiResponseResult getItemList(String company, String factory, String fstate, String fline, String fpro,
			String usercode, String pno) throws Exception {
		// TODO Auto-generated method stub
//		List<Map<String, Object>> countList = pointCheckDao.getCheckitem("151","2011");//this.findBySql(sql, SQLParameter.newInstance(), null);
//		 if(countList.size() == 0){
//	        	return ApiResponseResult.failure("检验项目为空!");
//	      }
//		 return ApiResponseResult.success("").data(countList);
		String ftype = StringUtils.isEmpty(pno)?"0":"1";
		List<String> a = this.doCheckitem(factory, company, usercode, ftype, pno, fstate, fline, fpro);
		System.out.println(a);
		if(a.get(0).equals("0")){
			List<Map<String, Object>> countList = pointCheckDao.getCheckitem(a.get(2),"2011");//this.findBySql(sql, SQLParameter.newInstance(), null);
			 if(countList.size() == 0){
		        	return ApiResponseResult.failure("检验项目为空!");
		      }
			 return ApiResponseResult.success("").data(countList);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	
	
	public List<String> doCheckitem(String factory,String company,String username,String ftype,String fno,String fstate,
			String fline,String fpro) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call AppQms_Chk_Create(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, ftype);
		cs.setString(5, fno);
		cs.setString(6, fstate);
		cs.setString(7, fline);
		cs.setString(8, fpro);
		cs.registerOutParameter(9,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(9));
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getItemDetail(String fid, String mid) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> countList = pointCheckDao.getCheckitemById(mid, fid);
		return ApiResponseResult.success("操作成功!").data(countList);
	}


	@Override
	public ApiResponseResult addCheckResult(String did, String mid, String values, String fnode, String company,
			String factory, String username) throws Exception {
		// TODO Auto-generated method stub
		values = values.equals("0")?"合格":"不合格";
		List<String> a = this.doAddResult(did, mid, values, fnode, company, factory, username);
		if(a.get(0).equals("0")){
			return ApiResponseResult.success(a.get(1));
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	
	public List<String> doAddResult(String did, String mid, String values, String fnode, String company,
			String factory, String username) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Chk_Edit(?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, mid);
		cs.setString(5, did);
		cs.setString(6, values);
		cs.setString(7, fnode);
		cs.registerOutParameter(8,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(8));
		result.add(cs.getString(9));
		return result;
		}
		});
		return resultList;

		}
	
	public Map getNgcodeListRf(String fstate, String ngtype) {
		Map resultList = (Map) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_NG_NGITEM(?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, fstate);
		cs.setString(2, ngtype);
		cs.registerOutParameter(3,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		Map result = new HashMap();
		cs.execute();
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(3);
		List<Map> lm = new ArrayList<Map>();
		while(rs.next()){
			Map<String, Object> m = new HashMap();
			m.put("FNAME", rs.getString("FNAME"));
			m.put("FCODE", rs.getString("FCODE"));
			lm.add(m);
			 }
		result.put("Result", lm);
		return result;
		}
		});
		return resultList;

		}

	private Map getListRf(int can,String storedProc,String[] param,String[] outParam) {
		Map resultList = (Map) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		CallableStatement cs = con.prepareCall(storedProc);// 调用的sql
		for(int i=0;i<param.length;i++){
			cs.setString(i+1, param[i]);
		}
		cs.registerOutParameter(can-2,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(can-1,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(can,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		Map result = new HashMap();
		cs.execute();
		result.put("Flag", cs.getString(can-2));
		result.put("Text", cs.getString(can-1));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(can);
		List<Map> lm = new ArrayList<Map>();
		while(rs.next()){
			Map<String, Object> m = new HashMap();
			for(String str:outParam){
				m.put(str, rs.getString(str));
			}
			lm.add(m);
			 }
		result.put("Result", lm);
		return result;
		}
		});
		return resultList;

		}
	

	
}
