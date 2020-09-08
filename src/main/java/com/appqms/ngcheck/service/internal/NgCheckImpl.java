package com.appqms.ngcheck.service.internal;

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
import com.appqms.ngcheck.dao.NgCheckDao;
import com.appqms.ngcheck.service.NgCheckService;

@Service(value = "ngCheckService")
@Transactional(propagation = Propagation.REQUIRED)
public class NgCheckImpl implements NgCheckService {

    @Autowired
    private NgCheckDao ngCheckDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getStatList(String company, String factory,String lot_no) throws Exception {
		// TODO Auto-generated method stub
		/* List<Map<String, Object>> countList = ngCheckDao.getStatList(company,factory);
		 return ApiResponseResult.success("操作成功!").data(countList);*/
		List<String> a = this.getStatListRf(factory, company, lot_no);
		List countList = new ArrayList();
		if(!StringUtils.isEmpty(a.get(0))){
			String[] str =  a.get(0).substring(0,a.get(0).length()-1).split(",");
			String[] str1 =  a.get(1).substring(0,a.get(1).length()-1).split(",");
			
			for(int i=0;i<str.length;i++){
				Map m1 = new HashMap();
				m1.put("FNAME", str[i]);
				m1.put("FCODE", str1[i]);
				countList.add(m1);
			}
		}
		 return ApiResponseResult.success("操作成功!").data(countList);
	}
	public List<String> getStatListRf(String factory,String company, String lot_no) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_NG_STATE(?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, lot_no);
		cs.registerOutParameter(2,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(2);
		String n = "";String c = "";
		while(rs.next()){
			n += rs.getString("FNAME")+",";
			c += rs.getString("FCODE")+",";
			 }
		result.add(n);
		result.add(c);
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult getNgtypeList(String fstate) throws Exception {
		// TODO Auto-generated method stub
		Map result = this.getNgtypeListRf(fstate);
		List<Map<String, Object>> countList =  (List<Map<String, Object>>)result.get("Result");//ngCheckDao.getNgtypeList(fstate);
		 return ApiResponseResult.success("操作成功!").data(countList);
	}
	public Map getNgtypeListRf(String fstate) {
		Map resultList = (Map) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_NG_NGTYPE(?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, fstate);
		cs.registerOutParameter(2,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		Map result = new HashMap();
		cs.execute();
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(2);
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
	

	@Override
	public ApiResponseResult getNgcodeList(String fstate, String ngtype) throws Exception {
		// TODO Auto-generated method stub
		Map result = this.getNgcodeListRf(fstate,ngtype);
		List<Map<String, Object>> countList =  (List<Map<String, Object>>)result.get("Result");//ngCheckDao.getNgcodeList(fstate, ngtype);
		 return ApiResponseResult.success("操作成功!").data(countList);
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

	@Override
	public ApiResponseResult saveNg(String factory,String company,String usercode,String ngid, String fstate, String ngtype, String ngcode, String deal,
			String qty, String note, String barcode) throws Exception {
		// TODO Auto-generated method stub
		String ftype = StringUtils.isEmpty(ngid)?"0":"2";
		List<String> a = this.doSaveNg(factory, company, usercode, ftype, ngid, fstate,  ngtype, ngcode, deal, qty, note, barcode);
		System.out.println(a);
		if(a.get(0).equals("0")){
			 return ApiResponseResult.success("操作成功!").data(a.get(2));
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}

	public List<String> doSaveNg(String factory,String company, String usercode,String ftype,String ngid, String fstate,String ngtype, String ngcode, String deal,
			String qty, String note, String barcode) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_Ng_Insertvalues_New(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, usercode);
		cs.setString(4, ftype);
		cs.setString(5, ngid);
		cs.setString(6, fstate);
		cs.setString(7, ngtype);
		cs.setString(8, ngcode);
		cs.setString(9, deal);
		cs.setString(10, qty);
		cs.setString(11, note);
		cs.setString(12, barcode);
		cs.registerOutParameter(13,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(14,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(15,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(13));
		result.add(cs.getString(14));
		result.add(cs.getString(15));
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult getList(String page) throws Exception {
		// TODO Auto-generated method stub
		int rows = 12;
		int p = Integer.parseInt(page);
		int start = (p-1)*rows;
		int end = p*rows+1;
		//获取总数量
		/*List<Map<String, Object>> countList = ngCheckDao.getCount();
		int total = Integer.parseInt(countList.get(0).get("COU").toString());
		int totalPage = 1;
		if (0 == total % rows)
            totalPage = total /rows;
            // 假设总数是51，不能够被5整除的，那么就有11页
        else
            totalPage = total / rows + 1;
		
		int status = 0;//未结束
		Map m = new HashMap();
		
		if(p > totalPage){
			countList = null;
			status = 1;
		}else{
			countList = ngCheckDao.getList(start, end);
		}*/
		Map m = new HashMap();
		List<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
		int status = 0;//未结束
		Map result = this.getUnListRf(start+"", end+"");
		if(result.get("Flag").toString().equals("0")){
			countList = (List<Map<String, Object>>)result.get("Result");
			status = countList.size()<12?1:0;
		}else{
			return ApiResponseResult.failure(result.get("Text").toString());
		}
		m.put("status", status);
		m.put("List", countList);
		return ApiResponseResult.success("操作成功!").data(m);
	}
	
	public Map getUnListRf(String start,String end) {
		Map resultList = (Map) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_NG_INFO(?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, start);
		cs.setString(2, end);
		cs.registerOutParameter(3,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(4,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(5,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		Map result = new HashMap();
		cs.execute();
		result.put("Flag", cs.getString(3));
		result.put("Text", cs.getString(4));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(5);
		List<Map> lm = new ArrayList<Map>();
		while(rs.next()){
			Map<String, Object> m = new HashMap();
			m.put("ID", rs.getString("ID"));
			m.put("FDEAL_TYPE", rs.getString("FDEAL_TYPE"));
			m.put("FBAR_LOT", rs.getString("FBAR_LOT"));
			m.put("FNG_QTY", rs.getString("FNG_QTY"));
			m.put("FBAR_CODE", rs.getString("FBAR_CODE"));
			m.put("LASTUPDATE_DATE", rs.getString("LASTUPDATE_DATE"));
			m.put("STAT_NAME", rs.getString("STAT_NAME"));
			m.put("FNG_TYPE", rs.getString("FNG_TYPE"));
			m.put("FNG_CODE_NAME", rs.getString("FNG_CODE_NAME"));
			lm.add(m);
			 }
		result.put("Result", lm);
		return result;
		}
		});
		return resultList;

		}

	
}
