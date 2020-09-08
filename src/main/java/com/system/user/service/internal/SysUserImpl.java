package com.system.user.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
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
import com.app.query.dao.Parameter;
import com.app.query.dao.SQLParameter;
import com.auth0.jwt.JWT;
import com.system.user.dao.SysUserDao;
import com.system.user.entity.SysUser;
import com.system.user.service.SysUserService;
import com.utils.UserUtil;
import com.utils.enumeration.BasicStateEnum;

import oracle.jdbc.internal.OracleTypes;

@Service(value = "sysUserService")
@Transactional(propagation = Propagation.REQUIRED)
public class SysUserImpl implements SysUserService {

    @Autowired
    private SysUserDao sysUserDao;

    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 注册新用户
     * @param sysUser
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ApiResponseResult add(SysUser sysUser) throws Exception{
        if(sysUser == null){
            return ApiResponseResult.failure("用户不能为空！");
        }
        if(StringUtils.isEmpty(sysUser.getUserCode()) || StringUtils.isEmpty(sysUser.getUserName())){
            return ApiResponseResult.failure("用户名或名称不能为空！");
        }
        //admin和super为管理员用户，无法操作
        if("admin".equals(sysUser.getUserCode().trim()) || "super".equals(sysUser.getUserCode().trim())){
            return ApiResponseResult.failure("此用户名为管理员用户，无法添加！");
        }
        int count = sysUserDao.countByIsDelAndUserCode(0, sysUser.getUserCode());
        if(count > 0){
            return ApiResponseResult.failure("用户名已存在，请填写其他用户名！");
        }

        sysUser.setUserCode(sysUser.getUserCode().trim());
        sysUser.setUserName(sysUser.getUserName().trim());
        //密码为空时默认为“123456”
        if(StringUtils.isEmpty(sysUser.getUserPassword())){
            sysUser.setUserPassword(MD5Util.MD5("123456"));
        }else{
            sysUser.setUserPassword(MD5Util.MD5(sysUser.getUserPassword()));
        }
        SysUser currUser = UserUtil.getCurrUser();  //获取当前用户
        sysUser.setCreatedTime(new Date());
        sysUser.setPkCreatedBy((currUser!=null) ? (currUser.getId()) : null);
        sysUserDao.save(sysUser);
        return ApiResponseResult.success("用户添加成功！");
    }


    /**
     * 修改密码
     * @param loginName
     * @param password
     * @param rePassword
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ApiResponseResult changePassword(String loginName, String oldPassword, String password, String rePassword) throws Exception{
        //验证，原密码和新密码都不能为空，并且原密码要求输入正确，新密码和确认密码一致
        if(StringUtils.isEmpty(loginName)){
            return ApiResponseResult.failure("登录名不能为空！");
        }
        if(StringUtils.isEmpty(oldPassword)){
            return ApiResponseResult.failure("原密码不能为空！");
        }
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(rePassword)){
            return ApiResponseResult.failure("密码不能为空！");
        }
        SysUser o = sysUserDao.findByIsDelAndUserCode(BasicStateEnum.FALSE.intValue(), loginName);
        if(o == null){
            return ApiResponseResult.failure("该用户不存在或已删除！");
        }
        if(!o.getUserPassword().equals(MD5Util.MD5(oldPassword))){
            return ApiResponseResult.failure("原密码输入错误！");
        }
        if(!password.equals(rePassword)){
            return ApiResponseResult.failure("密码不一致，请确认！");
        }

        //1.修改用户密码
        //admin和super为管理员用户，无法操作
        if("admin".equals(o.getUserCode().trim()) || "super".equals(o.getUserCode().trim())){
            return ApiResponseResult.failure("此用户名为管理员用户，无法修改密码！");
        }

        SysUser currUser = UserUtil.getCurrUser();  //获取当前用户
        o.setUserPassword(MD5Util.MD5(password));
        o.setModifiedTime(new Date());
        o.setPkModifiedBy((currUser!=null) ? (currUser.getId()) : null);
        sysUserDao.save(o);


        return ApiResponseResult.success("密码修改成功！");
    }

    /**
     * 重置密码（管理员修改密码使用）
     * @param id
     * @param password
     * @param rePassword
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ApiResponseResult resetPassword(Long id, String password, String rePassword) throws Exception{
        //验证，新密码不能为空，并且要求新密码和确认密码一致
        if(id == null){
            return ApiResponseResult.failure("记录ID不能为空！");
        }
        if(StringUtils.isEmpty(password) || StringUtils.isEmpty(rePassword)){
            return ApiResponseResult.failure("密码不能为空！");
        }
        if(!password.equals(rePassword)){
            return ApiResponseResult.failure("密码不一致，请确认！");
        }
        List<SysUser> sysUserList = sysUserDao.findById((long) id);
        if(sysUserList == null || sysUserList.size() == 0){
            return ApiResponseResult.failure("该用户不存在或已删除！");
        }
        SysUser o = sysUserList.get(0);
        if(o == null){
            return ApiResponseResult.failure("该用户不存在或已删除！");
        }

        //1.修改用户密码
        //admin和super为管理员用户，无法操作
        if("admin".equals(o.getUserCode().trim()) || "super".equals(o.getUserCode().trim())){
            return ApiResponseResult.failure("此用户名为管理员用户，无法修改密码！");
        }

        SysUser currUser = UserUtil.getCurrUser();  //获取当前用户
        o.setUserPassword(MD5Util.MD5(password));
        o.setModifiedTime(new Date());
        o.setPkModifiedBy((currUser!=null) ? (currUser.getId()) : null);
        sysUserDao.save(o);


        return ApiResponseResult.success("密码重置成功！");
    }

	@Override
	public ApiResponseResult getUserInfo(String token) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
		String userCode = JWT.decode(token).getAudience().get(0);
		SysUser userForBase= sysUserDao.findByIsDelAndUserCode(0, userCode);
		map.put("userCode", userForBase.getUserCode());
        map.put("userName", userForBase.getUserName());
        map.put("userComment", userForBase.getUserComment());
        map.put("userEmail", userForBase.getUserEmail());
        map.put("userType", userForBase.getUserType());
        return ApiResponseResult.success("登录成功！").data(map);
	}

	@Override
	public List<Map<String, Object>> findByUserCode(String userCode) throws Exception {
		// TODO Auto-generated method stub
		String sql = "select s.fcode,s.fname,s.fpassword,s.fcompany,s.ffactory from sys_user s where  upper(s.fcode) ='"
				+ userCode.toUpperCase() +  "'";
        List<Map<String, Object>> countList = sysUserDao.findByUserCode(userCode.toUpperCase());//this.findBySql(sql, SQLParameter.newInstance(), null);
		return countList;
       //return sysUserDao.findByIsDelAndUserCode(0, userCode);
	}

    /**
     * 获取用户列表
     * @param usercode
     * @param username
     * @param pageRequest
     * @return
     * @throws Exception
     */
	@Override
    @Transactional
	public ApiResponseResult getlist(String usercode,String username,PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		/*Sort sort = new Sort(Sort.Direction.DESC, "id");
        List<SysUser> list = sysUserDao.findAll(sort);*/
        
		SysUser demoBean = new SysUser();
		demoBean.setUserIsSuper(0);   //查询条件（0为普通用户，1为超级管理员，只显示普通用户）
		if(!StringUtils.isEmpty(usercode)){
			demoBean.setUserCode(usercode); //查询条件
		}
		if(!StringUtils.isEmpty(username)){
			demoBean.setUserName(username); //查询条件
		}

		//创建匹配器
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("userCode", ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("userName", ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnorePaths("userType", "userStatus");
        //创建查询参数
        Example<SysUser> example = Example.of(demoBean, matcher);
		/*//获取排序对象
		Sort sort = new Sort(Sort.Direction.DESC, "id");
		//创建分页对象
		PageRequest pageRequest = new PageRequest(0, 10, sort);*/
		//分页查询
		//List<SysUser> list  = sysUserDao.findAll(example, pageRequest).getContent();
		Page<SysUser> page = sysUserDao.findAll(example, pageRequest);
 
        return ApiResponseResult.success().data(DataGrid.create(page.getContent(), (int) page.getTotalElements(), pageRequest.getPageNumber() + 1, pageRequest.getPageSize()));
	}


	@Override
	public List<Map<String, Object>> queryVersion() throws Exception {
		// TODO Auto-generated method stub
		/*String sql = "select s.PARAM_VALUE from mes_sys_params s where s.PARAM_NAME='RF版本'";
        List<Map<String, Object>> countList = this.findBySql(sql, SQLParameter.newInstance(), null);
		return countList;*/
		return null;
	}
	@Override
	public List<Map<String, Object>> queryRunEnv() throws Exception {
		// TODO Auto-generated method stub
		/*String sql = "select s.PARAM_VALUE from mes_sys_params s where s.PARAM_NAME='运行环境'";
        List<Map<String, Object>> countList = this.findBySql(sql, SQLParameter.newInstance(), null);
		return countList;*/
		return null;
	}
	@Override
	public List queryPurview(String userno) throws Exception {
		// TODO Auto-generated method stub
		
//		 StoredProcedureQuery storedProcedureQuery = this.entityManager.createNamedStoredProcedureQuery("getContactsLikeName");
//	       storedProcedureQuery.setParameter("c_User_No", "ADMIN");
//	       storedProcedureQuery.setParameter("c_MachType", "WCE");
//	       storedProcedureQuery.execute();
//	       System.out.println(storedProcedureQuery.execute());
	       
//		Integer a = sysUserDao.createPolicy(1);
//		System.out.println(a);
		
//		Query query = entityManager.createNativeQuery("{call Prc_rf_j1_user_login(?,?,?)}");
//		query.setParameter(1, "ADMIN");
//		query.setParameter(2, "WCE");
//		query.setParameter(3, "");
//		String result = query.getSingleResult().toString();
		
		List<String> a = this.doPurview(userno);
		List arr = new ArrayList<>();
		if(a.size() > 0){
			String str = a.get(0);
			String[] strs = str.split("\\[");
			if(strs.length > 2){
				for(int i=2;i<strs.length;i++){
					String[] s = strs[i].split("#");
					arr.add(s);
				}
			}
		}
		String[] tc = "退出#tuichu".split("#");
		arr.add(tc);
		return arr;
	}

	public List<String> doPurview(String userno) {
		List<String> resultList = (List<String>) jdbcTemplate.execute(new CallableStatementCreator() {
		@Override
		public CallableStatement createCallableStatement(Connection con) throws SQLException {
		String storedProc = "{call Prc_rf_j1_user_login(?,?,?)}";// 调用的sql
		CallableStatement cs = con.prepareCall(storedProc);
		cs.setString(1, userno.toUpperCase());
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
		 List<Map<String, Object>> l = sysUserDao.queryAppVersion();
		 if(l.size() > 0){
			 m.put("Version", l.get(0).get("PV"));
		 }else{
			 return ApiResponseResult.failure("未设置更新版本");
		 }
		 
		 l = sysUserDao.queryApkUrl();
		 if(l.size() > 0){
			 m.put("Url", l.get(0).get("PV"));
		 }else{
			 return ApiResponseResult.failure("未设置更新版本的下载地址");
		 }
		 
		 l = sysUserDao.queryAppSize();
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
		sysUserDao.updatePwsByUserCode(userCode, newp);
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
					String[] res = strs[1].split(",", -1);
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

	
	
}
