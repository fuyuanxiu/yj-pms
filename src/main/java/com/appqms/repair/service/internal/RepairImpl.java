package com.appqms.repair.service.internal;

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
import com.appqms.repair.dao.RepairDao;
import com.appqms.repair.service.RepairService;

@Service(value = "repairService")
@Transactional(propagation = Propagation.REQUIRED)
public class RepairImpl implements RepairService {

    @Autowired
    private RepairDao repairDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
	public ApiResponseResult getLotList(String company, String factory) throws Exception {
		// TODO Auto-generated method stub
		 //List<Map<String, Object>> countList = repairDao.getStatList(company,factory);
		List<String> a = this.getLotListRf(factory, company);
		System.out.println(a);
		List countList = new ArrayList();
		if(!StringUtils.isEmpty(a.get(0))){
			String[] str =  a.get(0).substring(0,a.get(0).length()-1).split(",");
			String[] str1 =  a.get(1).substring(0,a.get(1).length()-1).split(",");
			
			for(int i=0;i<str.length;i++){
				Map m1 = new HashMap();
				m1.put("ID", str1[i]);
				m1.put("VALUES", str[i]);
				countList.add(m1);
			}
		}
		 return ApiResponseResult.success("操作成功!").data(countList);
	}
    
	@Override
	public ApiResponseResult getStatList(String company, String factory,String fid) throws Exception {
		// TODO Auto-generated method stub
		 //List<Map<String, Object>> countList = repairDao.getStatList(company,factory);
		List<String> a = this.getStatListRf(factory, company,fid);
		System.out.println(a);
		List countList = new ArrayList();
		if(!StringUtils.isEmpty(a.get(0))){
			String[] str =  a.get(0).substring(0,a.get(0).length()-1).split(",");
			String[] str1 =  a.get(1).substring(0,a.get(1).length()-1).split(",");
			String[] str2 =  a.get(2).substring(0,a.get(2).length()-1).split(",");
			
			for(int i=0;i<str.length;i++){
				Map m1 = new HashMap();
				m1.put("CODE", str1[i]);
				m1.put("NAME", str[i]);
				m1.put("QTY", str2[i]);
				countList.add(m1);
			}
		}
		 return ApiResponseResult.success("操作成功!").data(countList);
	}
	public List<String> getStatListRf(String factory,String company,String pid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_WX_STATE(?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, pid);
		cs.registerOutParameter(2,-10);
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(2);
		String n = "";String ids = "";String qtys = "";
		while(rs.next()){
			n += rs.getString("STATE_NAME")+",";
			ids += rs.getString("STATE_CODE")+",";
			qtys += rs.getString("SQTY")+",";
			 }
		result.add(n);
		result.add(ids);
		result.add(qtys);
		return result;
		}
		});
		return resultList;

		}
	public List<String> getLotListRf(String factory,String company) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_WX_LOTNO(?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.registerOutParameter(1,-10);
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(1);
		String n = "";String ids = "";
		while(rs.next()){
			n += rs.getString("LOT_NO")+",";
			ids += rs.getString("FID")+",";
			 }
		result.add(n);
		result.add(ids);
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult getNgtypeList(String fstate) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> countList = repairDao.getNgtypeList(fstate);
		 return ApiResponseResult.success("操作成功!").data(countList);
	}

	@Override
	public ApiResponseResult getNgcodeList(String fstate, String ngtype) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> countList = repairDao.getNgcodeList(fstate, ngtype);
		 return ApiResponseResult.success("操作成功!").data(countList);
	}

	@Override
	public ApiResponseResult saveRepair(String factory,String company,String usercode,String ngid, String fstate,  String deal,
			String qty, String note, String barcode) throws Exception {
		// TODO Auto-generated method stub
		String ftype = StringUtils.isEmpty(ngid)?"0":"2";
		List<String> a = this.saveRepairRf(factory, company, usercode, ftype, ngid, fstate,  deal, qty, note, barcode);
		System.out.println(a);
		if(a.get(0).equals("0")){
			 return ApiResponseResult.success("操作成功!").data(a.get(2));
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	public List<String> saveRepairRf(String factory,String company,String usercode,String ftype,String ngid, String fstate,  String deal,
			String qty, String note, String barcode) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_WX_Insertvalues(?,?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, usercode);
		cs.setString(4, ftype);
		cs.setString(5, ngid);
		cs.setString(6, fstate);
		cs.setString(7, deal);
		cs.setString(8, qty);
		cs.setString(9, note);
		cs.setString(10, barcode);
		cs.registerOutParameter(11,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(12,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(13,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(11));
		result.add(cs.getString(12));
		result.add(cs.getString(13));
		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getList(String page,String result) throws Exception {
		// TODO Auto-generated method stub
		List<String> a  = this.getListRf( "10", page, result);
		List countList = new ArrayList();
		if(!a.get(0).equals("0")){
			return ApiResponseResult.failure(a.get(1));
		}
		if(!StringUtils.isEmpty(a.get(2))){
			Map color = new HashMap();String[] colors = {"FFDAB9","F0FFF0","FFF8DC","FFDEAD","F5FFFA","FFFFF0","F0FFFF","FFDEAD",
                    "E6E6FA","FFFACD","FFE4B5","FFF8DC","FFF5EE","FFF0F5","DCDCDC","FFFAFA","F8F8FF"};
			String[] str2 =  a.get(2).substring(0,a.get(2).length()-1).split(",");
			String[] str3 =  a.get(3).substring(0,a.get(3).length()-1).split(",");
			String[] str4 =  a.get(4).substring(0,a.get(4).length()-1).split(",");
			String[] str5 =  a.get(5).substring(0,a.get(5).length()-1).split(",");
			String[] str6 =  a.get(6).substring(0,a.get(6).length()-1).split(",");
			String[] str7 =  a.get(7).substring(0,a.get(7).length()-1).split(",");
			String[] str8 =  a.get(8).substring(0,a.get(8).length()-1).split(",");
			for(int i=0;i<str2.length;i++){
				Map m1 = new HashMap();
				m1.put("FID", str2[i]);
				m1.put("LOT_NO", str3[i]);
				m1.put("FSTATE", str4[i]);
				m1.put("FSTATENAME", str5[i]);
				m1.put("SQTY", str6[i]);
				m1.put("FDATE", str7[i]);
				m1.put("RESULT", str8[i]);
				if(!color.containsKey(str4[i])){
					color.put(str4[i], colors[0]);
					colors = deleteFirst(colors);
				}
				m1.put("COLOR", color.get(str4[i]));
				countList.add(m1);
			}
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}

	@Override
	public ApiResponseResult getUnList(String page, String lot_no) throws Exception {
		// TODO Auto-generated method stub
		List<String> a  = this.getUnListRf("", "", page, "3", lot_no);
		List countList = new ArrayList();
		if(!a.get(0).equals("0")){
			return ApiResponseResult.failure(a.get(1));
		}
		if(!StringUtils.isEmpty(a.get(2))){
			Map color = new HashMap();String[] colors = {"FFDAB9","F0FFF0","FFF8DC","FFDEAD","F5FFFA","FFFFF0","F0FFFF","FFDEAD",
			                                           "E6E6FA","FFFACD","FFE4B5","FFF8DC","FFF5EE","FFF0F5","DCDCDC","FFFAFA","F8F8FF"};
			String[] str2 =  a.get(2).substring(0,a.get(2).length()-1).split(",");
			String[] str3 =  a.get(3).substring(0,a.get(3).length()-1).split(",");
			String[] str4 =  a.get(4).substring(0,a.get(4).length()-1).split(",");
			String[] str5 =  a.get(5).substring(0,a.get(5).length()-1).split(",");
			String[] str6 =  a.get(6).substring(0,a.get(6).length()-1).split(",");
			String[] str7 =  a.get(7).substring(0,a.get(7).length()-1).split(",");
			for(int i=0;i<str2.length;i++){
				Map m1 = new HashMap();
				m1.put("FID", str2[i]);
				m1.put("LOT_NO", str3[i]);
				m1.put("FSTATE", str4[i]);
				m1.put("FSTATENAME", str5[i]);
				m1.put("SQTY", str6[i]);
				m1.put("FDATE", str7[i]);
				if(!color.containsKey(str4[i])){
					color.put(str4[i], colors[0]);
					colors = deleteFirst(colors);
				}
				m1.put("COLOR", color.get(str4[i]));
				countList.add(m1);
			}
		}
		return ApiResponseResult.success("操作成功!").data(countList);
	}
	public  String[] deleteFirst(String[] arr) {
		String[] temp = new String[arr.length - 1];
	        System.arraycopy(arr, 1, temp, 0, temp.length);
	        return temp;
	    }
	
	public List<String> getUnListRf(String factory,String company,String page,String size, String lot_no) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_WX_LOTNO_RF(?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, lot_no);
		cs.setString(2, "");
		cs.setString(3, size);
		cs.setString(4, page);
		cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(7,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(5));
		result.add(cs.getString(6));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(7);
		String fid = "";String lot_no = "";String fstate = "";
		String fststeName = "";String qty = "";
		String fdate = "";
		while(rs.next()){
			fid += rs.getString("FID")+",";
			lot_no += rs.getString("LOT_NO")+",";
			fstate += rs.getString("FSTATE")+",";
			fststeName += rs.getString("FSTATENAME")+",";
			qty += rs.getString("SQTY")+",";
			fdate += rs.getString("FDATE")+",";
			 }
		result.add(fid);
		result.add(lot_no);
		result.add(fstate);
		result.add(fststeName);
		result.add(qty);
		result.add(fdate);
		return result;
		}
		});
		return resultList;

		}
	
	public List<String> getListRf(String size,String page, String result) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call APPQMS_REPAIR_INFO(?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, size);
		cs.setString(2, page);
		cs.setString(3, result);
		cs.registerOutParameter(4,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(6,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(4));
		result.add(cs.getString(5));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(6);
		String fid = "";String lot_no = "";String fstate = "";
		String fststeName = "";String qty = "";
		String fdate = "";String results = "";
		while(rs.next()){
			fid += rs.getString("ID")+",";
			lot_no += rs.getString("FBAR_CODE")+",";
			fstate += rs.getString("FSTATE")+",";
			fststeName += rs.getString("FSTATENAME")+",";
			qty += rs.getString("FRP_QTY")+",";
			fdate += rs.getString("CREATE_DATE")+",";
			results += rs.getString("FRP_RESULT")+",";
			 }
		result.add(fid);
		result.add(lot_no);
		result.add(fstate);
		result.add(fststeName);
		result.add(qty);
		result.add(fdate);
		result.add(results);
		return result;
		}
		});
		return resultList;

		}

	
}
