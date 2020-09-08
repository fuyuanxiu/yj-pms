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
import com.appqms.iqc.service.IqcFreeService;

@Service(value = "iqcFreeService")
@Transactional(propagation = Propagation.REQUIRED)
public class IqcFreeImpl implements IqcFreeService {

    @Autowired
    private IqcDao iqcDao;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;


	@Override
	public ApiResponseResult  getFlotNo(String factory, String company,String flot) throws Exception {
		// TODO Auto-generated method stub
        List<String> a = this.getFlotNoRf(factory, company,flot);
        System.out.println(a);
        if(a.get(0).equals("0")){
        	List<String> strsToList1= new ArrayList<String>();
        	if(a.get(2).length() > 0){
        		//准备一个String数组
            	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
            	//String数组转List
            	strsToList1= Arrays.asList(strs);
        	}
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
		if(a.get(0).equals("0") || a.get(0).equals("99")){
			//成功
			//准备一个String数组
			if(a.get(2).length() == 0){
				return ApiResponseResult.success("").data(a.get(2));
			}
        	String[] strs =a.get(2).substring(0,a.get(2).length()-1).split(",");
        	//String数组转List
        	List<String> strsToList1= Arrays.asList(strs);
        	if(a.get(0).equals("99")){
        		return ApiResponseResult.success(a.get(1)).data(strsToList1);
        	}
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
	public ApiResponseResult getCheckResult(String factory,String company,String username,String flot_no) throws Exception {
		// TODO Auto-generated method stub
		List<String> a =  this.getCheckItemRf(factory, company, username,flot_no);
		
		List<String> b =  this.getRfRealLot(factory, company, flot_no);
		
		System.out.println(a);
		if(a.get(0).equals("0")  || a.get(0).equals("99") ){
			Map m = new HashMap();
			m.put("id", a.get(2).substring(0,a.get(2).length()-1));
			m.put("flot_actualno", a.get(3).substring(0,a.get(3).length()-1));
			m.put("fitem_exterior", a.get(4).substring(0,a.get(4).length()-1));
			m.put("result", a.get(5).substring(0,a.get(5).length()-1));
			
			m.put("item_name", a.get(6).substring(0,a.get(6).length()-1));
			m.put("item_model", a.get(7).substring(0,a.get(7).length()-1));
			m.put("item_serial", a.get(8).substring(0,a.get(8).length()-1));
			
			System.out.println(a.get(9).substring(0,a.get(9).length()-1));
			System.out.println(a.get(9).substring(0,a.get(9).length()-1).equals("null"));
			m.put("fnote", a.get(9).substring(0,a.get(9).length()-1).equals("null")?"":a.get(9).substring(0,a.get(9).length()-1));
			m.put("ffinal_result", a.get(10).substring(0,a.get(10).length()-1));
			m.put("fsubmit", a.get(11).substring(0,a.get(11).length()-1));
			
			List l = new ArrayList();
			l.add(m);
			
			//子数据集
			List<String>  l1 = new ArrayList<String> ();
			/*if(a.get(6).length() > 0){
				String[] str =  a.get(6).substring(0,a.get(6).length()-1).split(",");
				String[] str1 =  a.get(7).substring(0,a.get(7).length()-1).split(",");
				
				for(int i=0;i<str.length;i++){
					Map m1 = new HashMap();
					m1.put("ID", str[i]);
					m1.put("VALUES", str1[i]);
					l1.add(m1);
				}
			}*/
			if(b.get(0).equals("0")){
				 //准备一个String数组
	            String[] strs =b.get(2).substring(0,b.get(2).length()-1).split(",");
	            //String数组转List
	            l1= Arrays.asList(strs);
			}
			
            
			Map mm = new HashMap();
			mm.put("S", l);
			mm.put("SS", l1);
			if(a.get(0).equals("99")){
        		return ApiResponseResult.success(a.get(1)).data(mm);
        	}
			return ApiResponseResult.success().data(mm);
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
			
			return ApiResponseResult.success("操作成功!").data(l);
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
		
	}	
	public List<String> doAddValues(String factory,String company,String username,String pi_itemid,String pi_values,String pi_type,String pi_valid) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_regularitem_val(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
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
		return cs;
		}
		}, new CallableStatementCallback() {
		public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		List<String> result = new ArrayList<String>();
		cs.execute();
		result.add(cs.getString(8));
		result.add(cs.getString(9));
		result.add(cs.getString(10));
		return result;
		}
		});
		return resultList;

		}
	
	public List<String> getFlotNoRf(String factory,String company,String flot) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_exemption_pc(?,?,?,?,?,?)}";// 调用的sql
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
	
	public List<String> getCheckItemRf(String factory,String company,String username,String flot_no) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_exemption(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, flot_no);
		cs.registerOutParameter(5,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		cs.registerOutParameter(7,-10);// 注册输出参数 返回类型
		cs.registerOutParameter(8,-10);// 注册输出参数 返回类型
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
		String ids = "";String n = "";String n1 = "";String n2 = "";
		String n3 = "";String n4 = "";String n5 = "";
		String n6 = "";String n7="";String n8="";
		while(rs.next()){
			ids += rs.getString("ID")+",";
			n += rs.getString("FLOT_ACTUALNO")+",";//实际批次号
			n1 += rs.getString("FITEM_EXTERIOR")+",";//外观
			n2 += rs.getString("FCHECK_RESULT")+",";//检验结果
			
			n3 += rs.getString("ITEM_NAME")+",";
			n4 += rs.getString("ITEM_MODEL")+",";
			n5 += rs.getString("ITEM_SERIAL")+",";
			
			n6 += rs.getString("FNOTE")+",";
			n7 += rs.getString("FFINAL_RESULT")+",";
			n8 += rs.getString("FSUBMIT")+",";
			 }
		result.add(ids);
		result.add(n);
		result.add(n1);
		result.add(n2);
		
		result.add(n3);
		result.add(n4);
		result.add(n5);
		
		result.add(n6);
		result.add(n7);
		result.add(n8);
		//20191224-fyx
		/*ResultSet rs1 = (ResultSet)cs.getObject(8);
		String n6 = "";String n7="";
		while(rs1.next()){
			n6 += rs1.getString("ID")+",";
			n7 += rs1.getString("FCHECK_VALUES")+",";
			 }
		result.add(n6);
		result.add(n7);*/
		
		return result;
		}
		});
		return resultList;

		}
	 public List<String> getRfRealLot(String factory,String company, String flotId) {
	        List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_exemption_sjpc(?,?,?,?,?,?)}";// 调用的sql
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
	public ApiResponseResult saveCheckResult(String factory, String company, String username, String pi_pid,
			String pi_values, String pi_name) throws Exception {
		// TODO Auto-generated method stub
		List<String> a = this.saveCheckResultRf(factory, company, username, pi_pid, pi_values, pi_name);

		if(a.get(0).equals("0")){
		    return ApiResponseResult.success("操作成功!");
		}else{
			return ApiResponseResult.failure(a.get(1));
		}
	}
	
	public List<String> saveCheckResultRf(String factory, String company, String username, String pi_itemid,String pi_values,
			String pi_name) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call appqms_iqc_exemption_fieldval(?,?,?,?,?,?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, factory);
		cs.setString(2, company);
		cs.setString(3, username);
		cs.setString(4, pi_itemid);
		cs.setString(5, pi_name);
		cs.setString(6, pi_values);
		cs.registerOutParameter(7,java.sql.Types.INTEGER);// 注册输出参数 返回类型
		cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 注册输出参数 返回类型
		return cs;
		}
		}, new CallableStatementCallback<Object>() {
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
	public ApiResponseResult doFreeItemField(String factory, String company, String username, String pid,
			String fielname, String fielval) throws Exception {
		// TODO Auto-generated method stub
		 List<String> a = this.doFreeItemFieldRf(company,factory,username,pid,fielname,fielval);
		 System.out.println(a);
	        if(a.get(0).equals("0")){
	            return ApiResponseResult.success(a.get(1));
	        }else {
	            return ApiResponseResult.failure(a.get(1));
	        }
	}
	private  List<String> doFreeItemFieldRf(String company, String factory, String username, String pid, 
			String fielname, String fielval){
		 List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call appqms_iqc_exemption_fieldval(?,?,?,?,?,?,?,?)}";// 调用的sql
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
	                String storedProc = "{call appqms_iqc_exemption_submit(?,?,?,?,?,?,?)}";// 调用的sql
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

	
}
