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

import com.app.base.data.ApiResponseResult;
import com.app.query.dao.Parameter;
import com.app.query.dao.SQLParameter;
import com.appqms.iqc.dao.IqcDao;
import com.appqms.iqc.service.IpqcService;

@Service(value = "ipqcService")
@Transactional(propagation = Propagation.REQUIRED)
public class IpqcImpl implements IpqcService {

	@Autowired
    private IqcDao iqcDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult  getFlotNo() throws Exception {
		// TODO Auto-generated method stub
		String sql = "select flot_no from QMS_INCOMING_INTERFACE Q where Q.fcheck_result is null and q.ftype=11 group by flot_no";
		//String sql = "select flot_no from QMS_INCOMING_INTERFACE Q  group by flot_no";
        List<Map<String, Object>> countList = iqcDao.getFlotNoByType("1011");//this.findBySql(sql, SQLParameter.newInstance(), null);
		return ApiResponseResult.success("操作成功!").data(countList);
	}
	

	@Override
	public ApiResponseResult getCheckitem(String username,String flot_no) throws Exception {
/*		String sql1 = "select d.fname,d.frequ,d.id,d.mid,d.fst,d.flower,d.fupper,d.funit,d.forder,d.fsample_qty,d.fis_quan,d.fcheck_result from QMS_TESTITEM_DETAIL d where d.mid=37 order by d.forder";
		List<Map<String, Object>> countList1 = this.findBySql(sql1, SQLParameter.newInstance(), null);
		 if(countList1.size() == 0){
	        	return ApiResponseResult.failure("检验项目为空!");
	      }
		 return ApiResponseResult.success("").data(countList1);*/
		// TODO Auto-generated method stub
		//根据批次号获取参数
		String sql = "select q.company,q.factory,q.fitem_no,q.flot_no,q.fstate from QMS_INCOMING_INTERFACE Q  where   q.flot_no='"+flot_no+"' and q.ftype=1011 ";
		List<Map<String, Object>> countList = iqcDao.getInfoByFlotNoAndType(flot_no, "1011");//this.findBySql(sql, SQLParameter.newInstance(), null);
        if(countList.size() == 0){
        	return ApiResponseResult.failure("批次号不正确!");
        }
        Map<String, Object> map = countList.get(0);
        
		List<String> a = this.doCheckitem(map.get("FACTORY").toString(),map.get("COMPANY").toString(), username.toUpperCase(),map.get("FITEM_NO").toString(),flot_no,map.get("FSTATE").toString());
       //121212-[1, 检验项目：TG/热应力/剥离强度测试/吸水率/阻燃性/相比漏电起痕/，需先做定期检验。, null, null]
		//4-[0, null, null, 33]
		System.out.println(a);
		if(a.get(0).equals("0")){
			//成功
			sql = "select d.fname,d.frequ,d.id,d.mid,d.fst,d.flower,d.fupper,d.funit,d.forder,d.fsample_qty,d.fis_quan,d.fcheck_result from QMS_TESTITEM_DETAIL d where d.mid="+a.get(3)+" order by d.forder";
			 countList = iqcDao.getCheckitem(a.get(3),"1011");//this.findBySql(sql, SQLParameter.newInstance(), null);
			 if(countList.size() == 0){
		        	return ApiResponseResult.failure("检验项目为空!");
		      }
			 return ApiResponseResult.success("").data(countList);
		}else if(a.get(0).equals("1")){
			//错误
			return ApiResponseResult.success(a.get(1)).data("1");
		}else if(a.get(0).equals("99")){
			//警告
			return ApiResponseResult.success(a.get(2)).data("99");
		}
		
		return ApiResponseResult.failure("存储过程返回未知错误！类型是"+a.get(0));
	}
	public List<String> doCheckitem(String factory,String company,String uaername,String fitem_no,String flot_no,String state) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Ipqc_Checkitem_Create(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setString(4, fitem_no);
		cs.setString(5, flot_no);
		cs.setString(6, state);
		cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();

		result.add(cs.getString(7));
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getCheckResult(String did) throws Exception {
		// TODO Auto-generated method stub
		//String sql = "select d.fname,d.fsample_qty,d.fis_quan,d.flower,d.fupper,v.id,v.fcheck_result,v.fcheck_values,d.funit from  QMS_TESTITEM_VALUES v left join  QMS_TESTITEM_DETAIL d on d.id = v.mid where d.id="+did;
        List<Map<String, Object>> countList = iqcDao.getCheckResult(did);//this.findBySql(sql, SQLParameter.newInstance(), null);
        System.out.println(countList);
        List<Map<String, Object>> countList1 = iqcDao.getItem(did);
        Map map = new HashMap();
        map.put("INFO", countList1.get(0));
        map.put("DA", countList);
		return ApiResponseResult.success("操作成功!").data(map);
	}



	@Override
	public ApiResponseResult addCheckResult(String did,String mid, String values, int type,String company,String factory,String username,int valid) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = doAddValues(factory,company,username,mid,did,values,type,valid);
		System.out.println(a);
		if(a.get(0).equals("0")){
			return ApiResponseResult.success("操作成功!").data(a);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
		
	}	
	public List<String> doAddValues(String factory,String company,String uaername,String mid,String did,String values1,int type1,int valid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Ipqc_Insertvalues(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setString(4, mid);
		cs.setString(5, did);
		cs.setString(6,values1);// 注
		cs.setLong(7,type1);// 
		cs.setLong(8,valid);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(11,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(12,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		result.add(cs.getString(11));
		result.add(cs.getString(12));
		return result;
		}
		});
		return resultList;

		}
	@Override
	public ApiResponseResult getStatNo(String factory, String company, String stat) throws Exception {
		// TODO Auto-generated method stub
        List<Object> a  = this.getStatNoRf(factory, company,1,stat);
		if(!a.get(0).toString().equals("0")){
			return ApiResponseResult.failure(a.get(1).toString());
		}
		
		return ApiResponseResult.success().data(a.get(2));
	}
	public List<Object> getStatNoRf(String factory,String company,int lx,String stat) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_get_zb(?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
        cs.setInt(3, lx);
        cs.setString(4, stat);
		cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(7,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();
		result.add(cs.getString(5));
		result.add(cs.getString(6));
		if(cs.getString(5).toString().endsWith("0")){
			//游标处理
			ResultSet rs = (ResultSet)cs.getObject(7);
			List l = new ArrayList();

			while(rs.next()){
				Map m = new HashMap();	
				m.put("FNAME", rs.getString("FNAME"));
				m.put("FCODE", rs.getString("FSTATE"));
				
				l.add(m);
				 }
			result.add(l);
		}
		

		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getFlotFrist(String factory, String company, String flot, String stat,String page) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.getFlotFristRf(factory, company,flot,stat,1,Integer.parseInt(page),10);
        System.out.println(a);
        if(a.get(0).equals("0")){
        	if(a.get(2).length() ==  0){
        		return ApiResponseResult.success("").data(new ArrayList());
        	}
        	//准备一个String数组
        	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
        	//String数组转List
        	List<String> strsToList1= Arrays.asList(strs);
        	return ApiResponseResult.success("").data(strsToList1);
        }else{
        	return ApiResponseResult.failure(a.get(1));
        }
	}
	public List<String> getFlotFristRf(String factory,String company,String flot, String stat,int lx,int page,int size) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_get_pc(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setInt(3, lx);
		cs.setInt(4, size);
		cs.setInt(5, page);
		cs.setString(6, stat);
		cs.setString(7, flot);
		cs.registerOutParameter(8,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		if(cs.getString(8).toString().endsWith("0")){
			//游标处理
			ResultSet rs = (ResultSet)cs.getObject(10);
			String n = "";
			while(rs.next()){
				n += rs.getString("FLOT_NO")+",";
				 }
			result.add(n);
		}
		
		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult getCheckitemFrist(String factory, String company, String username, String flot,
			String stat, String lx) throws Exception {
		// TODO Auto-generated method stub
		List<Object> a  = this.getCheckitemFristRf( factory, company, username,flot,stat);
		if(a.get(0).toString().equals("0")){
			Map m = new HashMap();
			m.put("Main", a.get(2));
			m.put("Child", a.get(3));
			return ApiResponseResult.success().data(m);
			
		}
		
		return ApiResponseResult.failure(a.get(1).toString());
	}
	public List<Object> getCheckitemFristRf(String factory,String company,String username,String flot, String stat) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_firstcheck(?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, flot);
		cs.setString(5, stat);
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,-10);// 注册输出参数 返回类型
		cs.registerOutParameter(9,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();
		result.add(cs.getString(6));
		result.add(cs.getString(7));
		
		if(cs.getString(6).toString().endsWith("0")){
			//游标处理
			ResultSet rs1 = (ResultSet)cs.getObject(8);
			List l1 = new ArrayList();

			while(rs1.next()){
				Map m = new HashMap();
				m.put("ID", rs1.getString("ID"));
				m.put("FLOT_NO", rs1.getString("FLOT_NO"));
				m.put("FITEM_NO", rs1.getString("FITEM_NO"));
				
				m.put("FPART_NO", rs1.getString("FPART_NO"));		
				m.put("FLOT_QTY", rs1.getString("FLOT_QTY"));
				m.put("FSUPP_NO", rs1.getString("FSUPP_NO"));
				
				m.put("FCHECK_RESULT", rs1.getString("FCHECK_RESULT"));
				
				l1.add(m);
				 }
			result.add(l1);
			///子表
			
			ResultSet rs = (ResultSet)cs.getObject(9);
			List l = new ArrayList();

			while(rs.next()){
				Map m = new HashMap();
				m.put("MID", rs.getString("MID"));
				
				m.put("ID", rs.getString("ID"));
				m.put("FCHECK_RESULT", rs.getString("FCHECK_RESULT"));
				m.put("FNAME", rs.getString("FNAME"));
				
				m.put("FLOWER", rs.getString("FLOWER"));		
				m.put("FUPPER", rs.getString("FUPPER"));
				m.put("FREQU", rs.getString("FREQU"));
				
				m.put("FIS_QUAN", rs.getString("FIS_QUAN"));
				m.put("FSAMPLE_QTY", rs.getString("FSAMPLE_QTY"));
				
				l.add(m);
				 }
			result.add(l);
		}
		

		return result;
		}
		});
		return resultList;

		}
	public List<Object> getCheckitemFristRf_bak(String factory,String company,String username,String flot, String stat,int lx) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_create_Document(?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setInt(4, lx);
		cs.setString(5, flot);
		cs.setString(6, stat);
		cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(9,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();
		result.add(cs.getString(7));
		result.add(cs.getString(8));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(9);
		List l = new ArrayList();

		while(rs.next()){
			Map m = new HashMap();
			m.put("MID", rs.getString("MID"));
			
			m.put("ID", rs.getString("ID"));
			m.put("FCHECK_RESULT", rs.getString("FCHECK_RESULT"));
			m.put("FNAME", rs.getString("FNAME"));
			
			m.put("FLOWER", rs.getString("FLOWER"));		
			m.put("FUPPER", rs.getString("FUPPER"));
			m.put("FREQU", rs.getString("FREQU"));
			
			m.put("FIS_QUAN", rs.getString("FIS_QUAN"));
			m.put("FSAMPLE_QTY", rs.getString("FSAMPLE_QTY"));
			
			l.add(m);
			 }
		result.add(l);

		return result;
		}
		});
		return resultList;

		}


	@Override
	public ApiResponseResult saveInsertValues(String factory, String company, String username, String pid)
			throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.saveInsertValuesRf(factory, company,username,pid);
        System.out.println(a);
        if(a.get(0).equals("0")){
        	return ApiResponseResult.success(a.get(1));
        }else{
        	return ApiResponseResult.failure(a.get(1));
        }
	}
	public List<String> saveInsertValuesRf(String factory,String company,String username, String pid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_insertvaluesall(?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setInt(4, Integer.parseInt(pid));
		cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
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


	@Override
	public ApiResponseResult saveFielValuesFirst(String factory, String company, String username, String pid,
			String fieldname, String fieldval) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.saveFielValuesFirstRf(factory, company,username,pid,fieldname,  fieldval);
        System.out.println(a);
        if(a.get(0).equals("0")){
        	return ApiResponseResult.success(a.get(1));
        }else{
        	return ApiResponseResult.failure(a.get(1));
        }
	}
	public List<String> saveFielValuesFirstRf(String factory,String company,String username, String pid,String fieldname, String fieldval) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_ipqc_insertfieldvalues(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setInt(4, Integer.parseInt(pid));
		cs.setString(5, fieldname);
		cs.setString(6, fieldval);
		cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(7));
		result.add(cs.getString(8));

		return result;
		}
		});
		return resultList;

		}
	@Override
    public ApiResponseResult doAppearance(String factory,String company,String username,String flotId) throws Exception{
        if(StringUtils.isEmpty(flotId)){
            return ApiResponseResult.failure("请选择批次号！");
        }
        Integer id = Integer.parseInt(flotId);

        List<String> a = this.doAppearRf(factory, company, username, id);
        if(a.get(0).equals("0")){
            return ApiResponseResult.success(a.get(1));
        }else {
            return ApiResponseResult.failure(a.get(1));
        }
    }
	public List<String> doAppearRf(String factory,String company,String username,Integer flotId) {
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_ipqc_insertvaluesall(?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);
                cs.setString(2, company);
                cs.setString(3, username);
                cs.setInt(4, flotId);
                cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
                cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<String> result = new ArrayList<String>();
                cs.execute();
                result.add(cs.getString(5));
                result.add(cs.getString(6));
                return result;
            }
        });
        return resultList;
    }
	
}
