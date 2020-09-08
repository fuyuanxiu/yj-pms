package com.appqms.ngcheck.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.user.entity.SysUser;

public interface NgCheckDao extends JpaRepository<SysUser, Long> {
	
   
  @Query(value = " select  a.fname,a.fcode  from qms_stat a where  Factory =?2 And Company =?1 ", nativeQuery = true)
  public List<Map<String, Object>> getStatList(String company,String factory);
  
  @Query(value = " select t.fname,t.fname fcode from QMS_NGTYPE t where t.fstate=?1 ", nativeQuery = true)
  public List<Map<String, Object>> getNgtypeList(String fstate);
  
  @Query(value = " select t1.fname,t1.fcode fcode from QMS_NGTYPE t join QMS_NGTYPE_DETAIL t1 on t1.mid=t.id where t.fstate=?1 and t.fname=?2 ", nativeQuery = true)
  public List<Map<String, Object>> getNgcodeList(String fstate,String ngtype);
  
  @Query(value = " SELECT *FROM (SELECT tt.*, ROWNUM AS rowno "+
   " FROM (select q.id,q.fid,q.fng_type,q.fng_code,to_char(q.lastupdate_date,'yyyy-mm-dd HH:SS:MM')lastupdate_date,q.fbar_code,q.fbar_lot,q.fng_qty,q.fdeal_type,s.fname,t1.fname fng_code_name,tt.STAT_NAME  from Qms_Ng_Info q  left join QMS_NGTYPE_DETAIL t1 on q.fng_code = t1.fcode left join qms_stat s on s.fcode = q.fstate"
   + " left join ( SELECT T.STAT_NAME ,T.STAT_CODE Fcode,T.LOT_NO  FROM QMS_LOTWIPINFO T)TT on tt.LOT_NO =  q.fbar_code and q.fstate = tt.fcode   order by q.create_date desc) tt"+
 " WHERE ROWNUM < ?2) table_alias WHERE table_alias.rowno >= ?1 ", nativeQuery = true)
  public List<Map<String, Object>> getList(int start,int end);
  
  
  @Query(value = " select count(id)cou from Qms_Ng_Info ", nativeQuery = true)
  public List<Map<String, Object>> getCount();
  
}
