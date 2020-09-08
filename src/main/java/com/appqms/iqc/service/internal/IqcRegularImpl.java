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
import com.appqms.iqc.dao.IqcDao;
import com.appqms.iqc.service.IqcRegularService;

@Service(value = "iqcRegularService")
@Transactional(propagation = Propagation.REQUIRED)
public class IqcRegularImpl implements IqcRegularService {

    @Autowired
    private IqcDao iqcDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult  getFlotNo(String factory, String company,String flot) throws Exception {
		// TODO Auto-generated method stub 
        List<String> a = this.getFlotNoRf("appqms_iqc_regularitem_pc",factory, company,flot);
        System.out.println(a);
        if(a.get(0).equals("0")){
        	if(a.get(2).length() == 0){
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



	@Override
	public ApiResponseResult getCheckitem(String  factory,String  company,String flot_no) throws Exception {

		List<String> a = this.doCheckitem(factory,company,flot_no);
       //121212-[1, 检验项目：TG/热应力/剥离强度测试/吸水率/阻燃性/相比漏电起痕/，需先做定期检验。, null, null]
		//4-[0, null, null, 33]
		System.out.println(a);
		if(a.get(0).equals("0")){
			//成功
			//准备一个String数组
			if(a.get(2).length() == 0){
				return ApiResponseResult.success("").data(a.get(2));
			}
        	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
        	//String数组转List
        	List<String> strsToList1= Arrays.asList(strs);
        	return ApiResponseResult.success("").data(strsToList1);
		}else{
			//错误
			return ApiResponseResult.failure(a.get(1));
		}
		
		//return ApiResponseResult.failure("存储过程返回未知错误！类型是"+a.get(0));
	}
	public List<String> doCheckitem(String factory,String company,String flot_no) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_regularitem_xm(?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, flot_no);
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
			n += rs.getString("FNAME")+",";
			 System.out.println(rs.getString("FNAME"));
			 }
		result.add(n);
				
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult getCheckResult(String factory,String company,String username,String flot_no,String fname) throws Exception {
		// TODO Auto-generated method stub
		List<String> a =  this.getCheckItemRf(factory, company, username,flot_no,fname);
		System.out.println(a);
		if(a.get(0).equals("0") || a.get(0).equals("99")){
			Map m = new HashMap();
			m.put("id", a.get(2).substring(0,a.get(2).length()-1));
			m.put("requ", a.get(3).substring(0,a.get(3).length()-1));
			m.put("test_date", a.get(4).substring(0,a.get(4).length()-1));
			m.put("result", a.get(5).substring(0,a.get(5).length()-1));
			
			m.put("item_name", a.get(8).substring(0,a.get(8).length()-1));
			m.put("item_model", a.get(9).substring(0,a.get(9).length()-1));
			m.put("item_serial", a.get(10).substring(0,a.get(10).length()-1));
			System.out.println(a.get(11).substring(0,a.get(11).length()-1));
			System.out.println(a.get(11).substring(0,a.get(11).length()-1).equals("null"));
			m.put("fnote", a.get(11).substring(0,a.get(11).length()-1).equals("null")?"":a.get(11).substring(0,a.get(11).length()-1));
			m.put("ffinal_result", a.get(12).substring(0,a.get(12).length()-1));
			m.put("fsubmit", a.get(13).substring(0,a.get(13).length()-1));
			
			System.out.println(a.get(14).substring(0,a.get(14).length()-1));
			
			String funit =  a.get(16).substring(0,a.get(16).length()-1).equals("null")?"":a.get(16).substring(0,a.get(16).length()-1);
			m.put("flower", a.get(14).substring(0,a.get(14).length()-1).equals("null")?"":a.get(14).substring(0,a.get(14).length()-1)+funit);
			m.put("fupper", a.get(15).substring(0,a.get(15).length()-1).equals("null")?"":a.get(15).substring(0,a.get(15).length()-1)+funit);
			m.put("funit",funit);
			
			System.out.println(a.get(17));
			m.put("fcus_no", a.get(17).toString().equals("null")?"":a.get(17));
			
			m.put("fcheck_unit", a.get(18).toString().equals("null")?"":a.get(18));
			
			List l = new ArrayList();
			l.add(m);
			
			//子数据集
			List l1 = new ArrayList();
			if(a.get(6).length() > 0){
				String[] str =  a.get(6).substring(0,a.get(6).length()-1).split(",");
				String[] str1 =  a.get(7).substring(0,a.get(7).length()-1).split(",");
				
				for(int i=0;i<str.length;i++){
					Map m1 = new HashMap();
					m1.put("ID", str[i]);
					m1.put("VALUES", str1[i]);
					l1.add(m1);
				}
			}
			Map mm = new HashMap();
			mm.put("S", l);
			mm.put("SS", l1);
			if( a.get(0).equals("99")){
				return ApiResponseResult.success(a.get(1)).data(mm);
			}
			return ApiResponseResult.success("").data(mm);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}



	@Override
	public ApiResponseResult addCheckResult(String factory,String company,String username,String pi_itemid,String pi_values,String pi_type,String pi_valid) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = doAddValues(factory,company,username,pi_itemid,pi_values,pi_type,pi_valid);
		//20191112-fyx-
		List<String> b = null;
		if(pi_type.equals("0")){
			b = this.saveCheckResultTg(factory, company, username, pi_itemid);
		}
		
		
		System.out.println(a);
		if(a.get(0).equals("0")){
			Map m = new HashMap();
			m.put("ID", a.get(2));
			m.put("VALUES", pi_values);
			List l = new ArrayList();
			l.add(m);
			//20191112-fyx-判断是否有新增
			if(pi_type.equals("0")){
				if(b.get(0).equals("0")){
					String i = b.get(2);
					String v = b.get(3);
					if(StringUtils.isEmpty(i) || StringUtils.isEmpty(v) || i.equals("null")|| v.equals("null")){
						
					}else{
						Map m1 = new HashMap();
						m1.put("ID", i);
						m1.put("VALUES", v);
						l.add(m1);
					}
				}
			}
			Map s = new HashMap();
			s.put("RES", l);
			s.put("RESULT", a.get(3));
			s.put("FFINAL_RESULT", a.get(4));
			return ApiResponseResult.success("操作成功!").data(s);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
		
	}	
	public List<String> doAddValues(String factory,String company,String username,String pi_itemid,String pi_values,String pi_type,String pi_valid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_regularitem_val(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, pi_itemid);
		cs.setString(5, pi_values);
		cs.setString(6,pi_type);// 注
		cs.setString(7,pi_valid);// 
		cs.registerOutParameter(8,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(10,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(11,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(11);
		String n1 = "";String n2 = "";
		while(rs.next()){
			n1 += rs.getString("FCHECK_RESULT");
			n2 += rs.getString("FFINAL_RESULT");
			
			 }
		result.add(n1);
		result.add(n2);
				
		return result;
		}
		});
		return resultList;

		}
	
	public List<String> getFlotNoRf(String prc_name,String factory,String company,String flot) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call "+prc_name+"(?,?,?,?,?,?)}";// 调用的sql
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
			n += rs.getString("FLOT_NO")+",";
			 System.out.println(rs.getString("FLOT_NO"));
			 }
		result.add(n);
		return result;
		}
		});
		return resultList;

		}
	
	public List<String> getCheckItemRf(String factory,String company,String username,String flot_no,String fname) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_regularitem(?,?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, flot_no);
		cs.setString(5, fname);
		cs.registerOutParameter(6,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(8,-10);// 注册输出参数 返回类型
		cs.registerOutParameter(9,-10);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(6));
		result.add(cs.getString(7));
		//游标处理
		ResultSet rs = (ResultSet)cs.getObject(8);
		String ids = "";String n = "";String n1 = "";String n2 = "";
		String n5 = "";String n6 = "";String n7 = "";
		String n8 = "";String n9="";String n10="";
		String n11="";String n12="";String n13="";
		String n14 = "";String n15 = "";
		while(rs.next()){
			ids += rs.getString("ID")+",";
			n += rs.getString("FREQU")+",";
			n1 += rs.getString("FTEST_DATE")+",";
			n2 += rs.getString("FCHECK_RESULT")+",";
			
			n5 += rs.getString("ITEM_NAME")+",";
			n6 += rs.getString("ITEM_MODEL")+",";
			n7 += rs.getString("ITEM_SERIAL")+",";
			
			n8 += rs.getString("FNOTE")+",";
			n9 += rs.getString("FFINAL_RESULT")+",";
			n10 += rs.getString("FSUBMIT")+",";
			
			/*n11 += rs.getString("FLOWER")+",";
			n12 += rs.getString("FUPPER")+",";*/
			n13 += rs.getString("FUNIT")+",";
			
			n11 += rs.getDouble("FLOWER")+",";
			n12 += rs.getDouble("FUPPER")+",";
			
			n14 += rs.getString("FCUS_NO")+"";
			
			n15 += rs.getString("FCHECK_UNIT")+"";
			
			 }
		result.add(ids);
		result.add(n);
		result.add(n1);
		result.add(n2);
		//
		ResultSet rs1 = (ResultSet)cs.getObject(9);
		String n3 = "";String n4="";
		while(rs1.next()){
			n3 += rs1.getString("ID")+",";
			n4 += rs1.getString("FCHECK_VALUES")+",";
			 }
		result.add(n3);
		result.add(n4);
		
		result.add(n5);
		result.add(n6);
		result.add(n7);
		
		result.add(n8);
		result.add(n9);
		result.add(n10);
		
		result.add(n11);
		result.add(n12);
		result.add(n13);
		
		result.add(n14);
		
		result.add(n15);
		
		return result;
		}
		});
		return resultList;

		}

	@Override
	public ApiResponseResult saveCheckResult(String factory, String company, String username, String pi_itemid,
			String pi_values, String pi_valdate) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.saveCheckResultRf(factory, company, username, pi_itemid, pi_values, pi_valdate);
		//20191109-sxw-调用存储过程appqms_iqc_regularitem_tg
        //List<String> b = this.saveCheckResultTg(factory, company, username, pi_itemid);
		if(a.get(0).equals("0")){
		    return ApiResponseResult.success("操作成功!");
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	
	public List<String> saveCheckResultRf(String factory, String company, String username, String pi_itemid,String pi_values,
			String pi_valdate) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_regularitem_values(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, pi_itemid);
		cs.setString(5, pi_values);
		cs.setString(6, pi_valdate);
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

    public List<String> saveCheckResultTg(String factory, String company, String username, String pi_itemid){
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_iqc_regularitem_tg(?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);
                cs.setString(2, company);
                cs.setString(3, pi_itemid);
                cs.setString(4, username);
                cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回标识
                cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回错误信息
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型 返回检验值ID
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型 返回检验值的结果
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<String> result = new ArrayList<String>();
                cs.execute();
                result.add(cs.getString(5));
                result.add(cs.getString(6));
                result.add(cs.getString(7));
                result.add(cs.getString(8));
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

        List<String> a = this.getRfPcData(factory, company, flotId);
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

    public List<String> getRfPcData(String factory,String company, String flotId) {
        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call appqms_iqc_regularitem_pcdata(?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);
                cs.setString(2, company);
                cs.setString(3, flotId);
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
                String storedProc = "{call appqms_iqc_regularitem_lrpc(?,?,?,?,?,?,?)}";// 调用的sql
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
	public ApiResponseResult doRegularitemField(String factory, String company, String username, String pid,
			String fielname, String fielval) throws Exception {
		// TODO Auto-generated method stub
		 List<String> a = this.doRegularitemFieldRf(company,factory,username,pid,fielname,fielval);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	private  List<String> doRegularitemFieldRf(String company, String factory, String username, String pid, 
			String fielname, String fielval){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_regularitem_field(?,?,?,?,?,?,?,?)}";// 调用的sql
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
		}else if(opertype.equals("3")){
			opertype = "删除";
		}else{
			opertype = "撤销提交";
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
	                String storedProc = "{call appqms_iqc_regularitem_submit(?,?,?,?,?,?,?)}";// 调用的sql
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
		String storedProc = "{call appqms_iqc_regularitem_select(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, keyword);
		cs.setInt(4, 10);
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
			m.put("fname", rs.getString("FNAME"));
			m.put("ftest_date", rs.getString("FTEST_DATE"));
			
			m.put("item_name", rs.getString("ITEM_NAME"));		
			m.put("item_model", rs.getString("ITEM_MODEL"));
			m.put("item_serial", rs.getString("ITEM_SERIAL"));
			
			m.put("fcus_no", rs.getString("FCUS_NO"));
			
			l.add(m);
			 }
		result.add(l);

		return result;
		}
		});
		return resultList;

		}
	
	
	


	
}
