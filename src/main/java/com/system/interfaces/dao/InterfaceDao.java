package com.system.interfaces.dao;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.system.user.entity.SysUser;

public interface InterfaceDao extends JpaRepository<SysUser, Long> {
    @Query(value = "select s.fcode,s.fname,s.fpassword,s.fcompany,s.ffactory from sys_user s where  upper(s.fcode) =?1 ", nativeQuery = true)
    public List<Map<String, Object>> findByUserCode(String usercode);
    
    @Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='AppVersion' ", nativeQuery = true)
    public List<Map<String, Object>> queryAppVersion();
    
    @Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='AppUrl' ", nativeQuery = true)
    public List<Map<String, Object>> queryApkUrl();
    
    @Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='AppSize' ", nativeQuery = true)
    public List<Map<String, Object>> queryAppSize();
    
    @Modifying
    @Transactional
	@Query(value = "update sys_user i set i.fpassword=?2 where upper(i.fcode) =?1 ", nativeQuery = true)
	public void updatePwsByUserCode(String usercode,String pwd);
    
    
    @Query(value = "select s.PARAM_VALUE from mes_sys_params s where s.PARAM_NAME='RF版本'", nativeQuery = true)
    public List<Map<String, Object>> queryVersion();
    @Query(value = "select s.PARAM_VALUE from mes_sys_params s where s.PARAM_NAME='运行环境'", nativeQuery = true)
    public List<Map<String, Object>> queryRunEnv();

}
