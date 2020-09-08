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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.appqms.iqc.dao.IqcDao;
import com.appqms.iqc.service.IqcService;

@Service(value = "iqcService")
@Transactional(propagation = Propagation.REQUIRED)
public class IqcImpl implements IqcService {

    @Autowired
    private IqcDao iqcDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult  getFlotNo(String factory, String company,String flot) throws Exception {
		// TODO Auto-generated method stub
		//String sql = "select flot_no from QMS_INCOMING_INTERFACE Q where Q.fcheck_result is null and q.ftype=11 group by flot_no";
		//String sql = "select flot_no from QMS_INCOMING_INTERFACE Q  group by flot_no";
       // List<Map<String, Object>> countList = iqcDao.getFlotNoByType("11");//this.findBySql(sql, SQLParameter.newInstance(), null);
		//return ApiResponseResult.success("操作成功!").data(countList);
		 List<String> a = this.getFlotNoRf("appqms_iqc_checkitem_pc",factory, company,flot,"FLOT_NO");
	        System.out.println(a);
	        if(a.get(0).equals("0")){
	        	//准备一个String数组
	        	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
	        	//String数组转List
	        	List<String> strsToList1= Arrays.asList(strs);
	        	return ApiResponseResult.success("").data(strsToList1);
	        }else{
	        	return ApiResponseResult.failure(a.get(1));
	        }
		
	}
	public List<String> getFlotNoRf(String pro_name,String factory,String company,String flot,String back_name) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call "+pro_name+"(?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
        cs.setString(3, flot);
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
		String n = "";
		while(rs.next()){
			n += rs.getString(back_name)+",";

			 }
		result.add(n);
		return result;
		}
		});
		return resultList;

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
		//String sql = "select q.company,q.factory,q.fitem_no,q.flot_no from QMS_INCOMING_INTERFACE Q  where   q.flot_no='"+flot_no+"' and q.ftype=11";
        List<Map<String, Object>> countList = iqcDao.getInfoByFlotNoAndType(flot_no, "11");//this.findBySql(sql, SQLParameter.newInstance(), null);
        if(countList.size() == 0){
        	return ApiResponseResult.failure("批次号不正确!");
        }
        Map<String, Object> map = countList.get(0);
        
		List<Object> a = this.doCheckitem(map.get("FACTORY").toString(),map.get("COMPANY").toString(), username.toUpperCase(),map.get("FITEM_NO").toString(),flot_no);
       //121212-[1, 检验项目：TG/热应力/剥离强度测试/吸水率/阻燃性/相比漏电起痕/，需先做定期检验。, null, null]
		//4-[0, null, null, 33]
		System.out.println(a);
		if(a.get(0).equals("0") || a.get(0).equals("99")){
			//成功
			 
			/* countList = iqcDao.getCheckitem(str1[0],"11");//this.findBySql(sql, SQLParameter.newInstance(), null);
			 if(countList.size() == 0){
		        	return ApiResponseResult.failure("检验项目为空!");
		      }*/
			 Map m = new HashMap();
			 m.put("List", a.get(4));
			 m.put("Flot", a.get(3));
			if(a.get(0).equals("99")){
				return ApiResponseResult.success(a.get(2).toString()).data(m);
			}
			 return ApiResponseResult.success("").data(m);
		}else if(a.get(0).equals("1")){
			//错误
			return ApiResponseResult.success(a.get(1).toString()).data("1");
		}/*else if(a.get(0).equals("99")){
			//警告
			return ApiResponseResult.success(a.get(2)).data("99");
		}*/
		
		return ApiResponseResult.failure("存储过程返回未知错误！类型是"+a.get(0));
	}
	public List<Object> doCheckitem(String factory,String company,String uaername,String fitem_no,String flot_no) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Appqms_Iqc_Checkitem_Create(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, uaername);
		cs.setString(4, fitem_no);
		cs.setString(5, flot_no);
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(9,-10);// 注册输出参数 返回类型
		cs.registerOutParameter(10,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();

		result.add(cs.getString(6));
		result.add(cs.getString(7));
		result.add(cs.getString(8));
		//result.add(cs.getString(9));
		 //游标处理
        ResultSet rs = (ResultSet)cs.getObject(9);
        List l = new ArrayList();
        while(rs.next()){
			Map m1 = new HashMap();
			m1.put("ID", rs.getString("ID"));
			m1.put("FLOT_NO", rs.getString("FLOT_NO"));
			m1.put("FLOT_ACTUALNO", rs.getString("FLOT_ACTUALNO"));
			m1.put("ITEM_NAME", rs.getString("ITEM_NAME"));
			m1.put("ITEM_MODEL", rs.getString("ITEM_MODEL"));
			m1.put("ITEM_SERIAL", rs.getString("ITEM_SERIAL"));
			
			m1.put("fnote",  rs.getString("FNG_REASON"));
			
			m1.put("fsubmit", rs.getString("FSUBMIT"));
			m1.put("fcheck_result", rs.getString("FCHECK_RESULT"));
			
			String a = rs.getString("FFINAL_RESULT") +"";
			if(a.equals("合格") ){
				m1.put("ffinal_result","deal1");
			}else if(a.equals("特采") ){
				m1.put("ffinal_result","deal2");
			}else if(a.equals("退货") ){
				m1.put("ffinal_result","deal3");
			}else{
				m1.put("ffinal_result","");
			}
			
			m1.put("fprd_date", rs.getString("FPRD_DATE"));
			
			m1.put("flot_qty", rs.getString("FLOT_QTY"));
			
			m1.put("finspection_standards", rs.getString("FINSPECTION_STANDARDS"));
			
			l.add(m1);
			
        }
        result.add(l);
        
        
        
        ResultSet rs10 = (ResultSet)cs.getObject(10);
        List l10 = new ArrayList();
        while(rs10.next()){
        	Map m = new HashMap();
        	m.put("ID", rs10.getString("ID"));
        	m.put("FSAMPLE_QTY", rs10.getString("FSAMPLE_QTY"));
        	m.put("FIS_QUAN", rs10.getString("FIS_QUAN"));
        	m.put("MID", rs10.getString("MID"));
        	m.put("FCHECK_RESULT", rs10.getString("FCHECK_RESULT"));
        	m.put("FNAME", rs10.getString("FNAME"));
        	m.put("FLOWER", rs10.getString("FLOWER"));
        	m.put("FUPPER", rs10.getString("FUPPER"));
        	m.put("FREQU", rs10.getString("FREQU"));
        	l10.add(m);
        }
        result.add(l10);
        
        
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
		String storedProc = "{call Appqms_Iqc_Insertvalues(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
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
    public ApiResponseResult getRealFlotNo(String factory,String company,String flotId) throws Exception{
        if(StringUtils.isEmpty(flotId)){
            return ApiResponseResult.failure("请选择批次号！");
        }
        Integer id = Integer.parseInt(flotId);

        List<String> a = this.getPcData(factory, company, id);
        if(a.get(0).equals("0")){
            //准备一个String数组
            String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
            //String数组转List
            List<String> strsToList1= Arrays.asList(strs);
            return ApiResponseResult.success("").data(strsToList1);
        }else{
            return ApiResponseResult.failure(a.get(1));
        }
    }

    public List<String> getPcData(String factory,String company, Integer flotId) {
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_iqc_insertvalues_pcdata(?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);
                cs.setString(2, company);
                cs.setInt(3, flotId);
                cs.registerOutParameter(4,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
                cs.registerOutParameter(6,-10);// 注册输出参数 返回类型 返回批次号游标
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
                String n = "";
                while(rs.next()){
                    n += rs.getString("FSUPP_LOT")+",";
                    System.out.println(rs.getString("FSUPP_LOT"));
                }
                result.add(n);
                return result;
            }
        });
        return resultList;

    }

    @Override
    public ApiResponseResult doRealFlotNo(String factory,String company,String username,String flotId,String realFlotNo) throws Exception{
        if(StringUtils.isEmpty(flotId)){
            return ApiResponseResult.failure("请选择批次号！");
        }
        Integer id = Integer.parseInt(flotId);
        if(StringUtils.isEmpty(realFlotNo)){
            return ApiResponseResult.failure("请选择实际批次号！");
        }

        List<String> a = this.doRealPc(factory, company, username, id, realFlotNo);
        if(a.get(0).equals("0")){
            return ApiResponseResult.success(a.get(1));
        }else {
            return ApiResponseResult.failure(a.get(1));
        }
    }

    public List<String> doRealPc(String factory,String company,String username,Integer flotId,String realFlotNo) {
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_iqc_insertvalues_pc(?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);
                cs.setString(2, company);
                cs.setString(3, username);
                cs.setInt(4, flotId);
                cs.setString(5, realFlotNo);
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<String> result = new ArrayList<String>();
                cs.execute();
                result.add(cs.getString(6));
                result.add(cs.getString(7));
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

        List<String> a = this.doAppearAll(factory, company, username, id);
        if(a.get(0).equals("0")){
            return ApiResponseResult.success(a.get(1));
        }else {
            return ApiResponseResult.failure(a.get(1));
        }
    }

    public List<String> doAppearAll(String factory,String company,String username,Integer flotId) {
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_iqc_insertvaluesall(?,?,?,?,?,?)}";// 调用的sql
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
	@Override
	public ApiResponseResult addCheckMemo(String company, String factory, String username, String pid, String itemid,
			String valueid, String memo) throws Exception {
		// TODO Auto-generated method stub
	        List<String> a = this.addCheckMemoRf(company,factory,username,pid,itemid,valueid,memo);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	
	private  List<String> addCheckMemoRf(String company, String factory, String username, String pid, String itemid,
			String valueid, String memo){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_insertvalues_memo(?,?,?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, factory);
	                cs.setString(2, company);
	                cs.setString(3, username);
	                cs.setInt(4, Integer.parseInt(pid));
	                cs.setInt(5, Integer.parseInt(itemid));
	                cs.setInt(6, Integer.parseInt(valueid));
	                cs.setString(7, memo);
	                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
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
	public ApiResponseResult doCheckitemByFiel(String factory, String company, String username, String pid,
			String fielname, String fielval) throws Exception {
		// TODO Auto-generated method stub
		 List<String> a = this.doCheckitemByFielRf(company,factory,username,pid,fielname,fielval);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	private  List<String> doCheckitemByFielRf(String company, String factory, String username, String pid, 
			String fielname, String fielval){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_checkitem_fieldval(?,?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, factory);
	                cs.setString(2, company);
	                cs.setString(3, username);
	                cs.setInt(4, Integer.parseInt(pid));
	                cs.setString(5, fielname);
	                cs.setString(6,fielval);
	                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
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
	public ApiResponseResult doSumbit(String factory, String company, String username, String pid, String opertype)
			throws Exception {
		// TODO Auto-generated method stub
		if(opertype.equals("1")){
			opertype = "提交";
		}else if(opertype.equals("2")){
			opertype = "撤销提交";
		}else if(opertype.equals("3")){
			opertype = "删除";
		}
		List<String> a = this.doSumbitRf(company,factory,username,pid,opertype);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	
	private  List<String> doSumbitRf(String company, String factory, String username, String pid, 
			String opertype){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_checkitem_submit(?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(2, factory);
	                cs.setString(1, company);
	                cs.setString(4, username);
	                cs.setInt(3, Integer.parseInt(pid));
	                cs.setString(5, opertype);
	                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<String> result = new ArrayList<String>();
	                cs.execute();
	                result.add(cs.getString(6));
	                result.add(cs.getString(7));
	                return result;
	            }
	        });
	        return resultList;
		
	}
	@Override
	public ApiResponseResult doSaveByFiel(String factory, String company, String username, String pid, String fielName,
			String fielValue) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.doSaveByFielRf("appqms_iqc_checkitem_field_yxq",company,factory,username,pid,fielName,fielValue);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1)).data(a.get(2));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	private  List<String> doSaveByFielRf(String pac_name,String company, String factory, String username, String pid, 
			String fielName,String fielValue){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	            	String storedProc = "{call "+pac_name+"(?,?,?,?,?,?,?,?,?)}";// 调用的sql
	            	if(pac_name.equals("appqms_iqc_ng_updatefield")){
	            		storedProc = "{call "+pac_name+"(?,?,?,?,?,?,?,?)}";// 调用的sql
	            	}
	                
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, factory);
	                cs.setString(2, company);
	                cs.setString(3, username);
	                if(pac_name.equals("appqms_iqc_ng_updatefield")){
	                	cs.setString(4, pid);
	                }else{
	                	cs.setInt(4, Integer.parseInt(pid));
	                }
	                cs.setString(5, fielName);
	                cs.setString(6, fielValue);
	                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
	                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回信息
	                if(!pac_name.equals("appqms_iqc_ng_updatefield")){
	                	cs.registerOutParameter(9,-10);// 注册输出参数 返回类型 返回信息
	                }
	                
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<String> result = new ArrayList<String>();
	                cs.execute();
	                result.add(cs.getString(7));
	                result.add(cs.getString(8));
	                
	                if(!pac_name.equals("appqms_iqc_ng_updatefield")){
	                	//游标处理
		        		ResultSet rs = (ResultSet)cs.getObject(9);
		        		String n = "";
		        		while(rs.next()){
		        			n += rs.getString("MEMO");
		        			 }
		        		result.add(n);
	                }
	              
	        		
	                return result;
	            }
	        });
	        return resultList;
		
	}
	
	@Override
	public ApiResponseResult  getNgFlotNo(String factory, String company,String flot) throws Exception {
		// TODO Auto-generated method stub 
        List<String> a = this.getFlotNoRf("appqms_iqc_ng_pc",factory, company,flot,"FSUPP_LOT");
        System.out.println(a);
        if(a.get(0).equals("0")){
        	if(a.get(2).length() == 0){
        		return ApiResponseResult.success("").data(new ArrayList());
        	}
        	//准备一个String数组
        	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
        	//String数组转List
        	List<String> strsToList1= Arrays.asList(strs);
        	return ApiResponseResult.success(a.get(1)).data(strsToList1);
        }else{
        	return ApiResponseResult.failure(a.get(1));
        }
	}
	@Override
	public ApiResponseResult doNgCreate(String company, String factory, String username, String pid,String did, String ftype,
			String flotno) throws Exception {
		// TODO Auto-generated method stub
		List<Object> a = this.doNgCreateRf(company, factory, username, pid,did, Integer.parseInt(ftype),flotno);
			System.out.println(a);
			if(a.get(0).equals("0")){
				//成功
				 Map m = new HashMap();
				 m.put("Flot", a.get(2));
				 return ApiResponseResult.success("").data(m);
			}else{
				return ApiResponseResult.failure(a.get(1).toString());
			}
				
	}
	public List<Object> doNgCreateRf(String company, String factory, String username, String pid, String did,int ftype,String flotno) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_ng_create(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, pid);
		cs.setString(4, did);
		cs.setString(5, username);
		cs.setInt(6, ftype);
		cs.setString(7, flotno);
		cs.registerOutParameter(8,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();

		result.add(cs.getString(8));
		result.add(cs.getString(9));
		//result.add(cs.getString(9));
		if(cs.getString(8).equals("0")){
			 //游标处理
	        ResultSet rs = (ResultSet)cs.getObject(10);
	        List l = new ArrayList();
	        while(rs.next()){
				Map m1 = new HashMap();
				m1.put("FBAR_CODE", rs.getString("FBAR_CODE"));
				m1.put("FNG_CODE", rs.getString("FNG_CODE"));
				m1.put("FLOT_QTY", rs.getString("FLOT_QTY"));
				m1.put("FNOTE", rs.getString("FNOTE"));
				m1.put("APLY_FLAG", rs.getString("APLY_FLAG"));
				m1.put("FOA_BFSL", rs.getString("FOA_BFSL"));
				
				m1.put("FOA_TCSL",  rs.getString("FOA_TCSL"));
				
				m1.put("FOA_FGSL", rs.getString("FOA_FGSL"));
				m1.put("FITEM_NO", rs.getString("FITEM_NO"));
				
				m1.put("ID", rs.getString("ID"));
				
				l.add(m1);
				
	        }
	        result.add(l);
	        
	        
	        
	        /*ResultSet rs10 = (ResultSet)cs.getObject(10);
	        List l10 = new ArrayList();
	        while(rs10.next()){
	        	Map m = new HashMap();
	        	m.put("REQUESTNAME", rs10.getString("REQUESTNAME"));
	        	m.put("NODENAME", rs10.getString("NODENAME"));
	        	m.put("NOWNODENAME", rs10.getString("NOWNODENAME"));
	        	m.put("OPERATEDATE", rs10.getString("OPERATEDATE"));
	        	m.put("REMARK", rs10.getString("REMARK"));
	        	m.put("RECEIVEDPERSONS", rs10.getString("RECEIVEDPERSONS"));
	        	m.put("REQUESTID", rs10.getString("REQUESTID"));
	        	l10.add(m);
	        }
	        result.add(l10);*/
		}
		
        
        
        return result;

		}
		});
		return resultList;

		}
	
	@Override
	public ApiResponseResult doNgUpdatefield(String factory, String company, String username, String pid, String fielName,
			String fielValue) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.doSaveByFielRf("appqms_iqc_ng_updatefield",company,factory,username,pid,fielName,fielValue);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	@Override
	public ApiResponseResult getNgAplyList(String factory, String company, String pid)
			throws Exception {
		// TODO Auto-generated method stub
		List<Object> a = this.getNgAplyListRf(company, factory, pid);
		System.out.println(a);
		if(a.get(0).equals("0")){
			//成功
			 return ApiResponseResult.success("").data(a.get(2));
		}else{
			return ApiResponseResult.failure(a.get(1).toString());
		}
	}
	public List<Object> getNgAplyListRf(String company, String factory,  String pid) {
		List<Object> resultList = (List<Object>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_ng_select(?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, pid);
		cs.registerOutParameter(4,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(6,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<Object> result = new ArrayList<Object>();
		cs.execute();

		result.add(cs.getString(4));
		result.add(cs.getString(5));
		if(cs.getString(4).equals("0")){
			 //游标处理
	       ResultSet rs10 = (ResultSet)cs.getObject(6);
	        List l10 = new ArrayList();
	        while(rs10.next()){
	        	Map m = new HashMap();
	        	m.put("REQUESTNAME", rs10.getString("REQUESTNAME"));
	        	m.put("NODENAME", rs10.getString("NODENAME"));
	        	m.put("NOWNODENAME", rs10.getString("NOWNODENAME"));
	        	m.put("OPERATEDATE", rs10.getString("OPERATEDATE"));
	        	m.put("REMARK", rs10.getString("REMARK"));
	        	m.put("RECEIVEDPERSONS", rs10.getString("RECEIVEDPERSONS"));
	        	m.put("REQUESTID", rs10.getString("REQUESTID"));
	        	l10.add(m);
	        }
	        result.add(l10);
		}

        return result;

		}
		});
		return resultList;

		}

	
}
