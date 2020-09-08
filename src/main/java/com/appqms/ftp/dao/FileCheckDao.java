package com.appqms.ftp.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.system.user.entity.SysUser;

public interface FileCheckDao extends JpaRepository<SysUser, Long> {
	/*@Query(value = "select q.fnote, wm_concat(q.fattach)fattach,max(q.ftype)ftype,max(q.mid)mid,to_char(max(q.create_date),'yyyy-mm-dd hh24:mi:ss')cd, max(q.ftype)||'/'||max(q.mid)furl from QMS_ACCESSORIES q where q.mid=?1  group by q.fnote ", nativeQuery = true)
	  public List<Map<String, Object>> getFileList(String mid);*/
	@Query(value = "select q.fnote, wm_concat(q.fattach)fattach,max(q.ftype)ftype,max(q.mid)mid,to_char(max(q.create_date),'yyyy-mm-dd hh24:mi:ss')cd  from QMS_ACCESSORIES q where q.mid=?1  group by q.fnote ", nativeQuery = true)
	  public List<Map<String, Object>> getFileList(String mid);
	
	@Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='FtpServerIP' ", nativeQuery = true)
    public List<Map<String, Object>> queryFtpServerIP();
	
	@Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='FtpUser' ", nativeQuery = true)
    public List<Map<String, Object>> queryFtpUser();
	
	@Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='FtpPsw' ", nativeQuery = true)
    public List<Map<String, Object>> queryFtpPsw();
	
	@Query(value = "select m.param_value pv from mes_sys_params m where m.param_code='FtpPortNum' ", nativeQuery = true)
    public List<Map<String, Object>> queryFtpPortNum();

}
