package com.appqms.iqc.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.appqms.iqc.service.IqcComingService;

@Service(value = "iqcComingService")
@Transactional(propagation = Propagation.REQUIRED)
public class IqcComingImpl implements IqcComingService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult getList(String factory, String company, String keyword, String page) throws Exception {
		// TODO Auto-generated method stub
		List<Object> a  = this.getListRf( factory, company, keyword,Integer.parseInt(page));

		if(!a.get(0).toString().equals("0")){
			return ApiResponseResult.failure(a.get(1).toString());
		}
		
		return ApiResponseResult.success().data(a.get(2));
	}
	public List<Object> getListRf(String factory, String company, String keyword,int page) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_incoming_select(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, keyword);
		cs.setInt(4, 5);
		cs.setInt(5, page);
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();
		result.add(cs.getString(6));
		result.add(cs.getString(7));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(8);
		List l = new ArrayList();

		while(rs.next()){
			Map m = new HashMap();
			m.put("flot_no", rs.getString("FLOT_NO"));
			m.put("fname", rs.getString("KKFNAME"));
			
			m.put("item_name", rs.getString("ITEM_NAME"));		
			m.put("item_model", rs.getString("ITEM_MODEL"));
			m.put("item_serial", rs.getString("ITEM_SERIAL"));
			
			m.put("ffsupp_name", rs.getString("FFSUPP_NAME"));		
			m.put("gitem_type", rs.getString("GITEM_TYPE"));
			m.put("hl8", rs.getString("HL8"));
			m.put("cfcus_no", rs.getString("CFCUS_NO"));		
			m.put("flot_qty", rs.getString("FLOT_QTY"));
			m.put("fincoming_date", rs.getString("FINCOMING_DATE"));
			m.put("fprd_date", rs.getString("FPRD_DATE"));
			
			l.add(m);
			 }
		result.add(l);

		return result;
		}
		});
		return resultList;

		}

	
}
