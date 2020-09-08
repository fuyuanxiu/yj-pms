package com.system.user.dao;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.system.user.entity.SysUser;

public interface SysUserDao extends JpaRepository<SysUser, Long> {

    public int countByIsDelAndUserCode(Integer isDel, String userCode);

    public SysUser findByIsDelAndUserCode(Integer isDel, String userCode);
    
    public List<SysUser> findById(long id);
//    
//    @Query(value = " call p_production_plan_check(:inParam1,:inParam2,:inParam3,:inParam4)", nativeQuery = true)
//    List<Map<String, Object>> pPlanCheck(@Param("inParam1") String calStart,@Param("inParam2") String calEnd,@Param("inParam3") String workshopcode,@Param("inParam4") String orderno);

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

}
