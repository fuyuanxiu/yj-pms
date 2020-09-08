package com.appqms.pointcheck.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.user.entity.SysUser;

public interface PointCheckDao extends JpaRepository<SysUser, Long> {
	
   
  @Query(value = " select  a.fname,a.fcode  from qms_stat a where  Factory =?2 And Company =?1 ", nativeQuery = true)
  public List<Map<String, Object>> getStatList(String company,String factory);
  
  @Query(value = " select fline fname ,fline_no fcode from qms_line where fstate=?3 and  Factory =?2 And Company =?1 ", nativeQuery = true)
  public List<Map<String, Object>> getLineList(String company,String factory,String fstate);
  
  @Query(value = " Select a.fproject fname, a.fproject fcode  From Qms_Base_Testitemset a Left Join Qms_Line c On c.Fline_No = a.Fline_No Left Join Qms_Stat b On a.Fstate = b.Fcode WHERE A.FTYPE=2001  and  a.fline_no = ?4 and a.fstate=?3 and  a.Factory =?2 And a.Company =?1 ", nativeQuery = true)
  public List<Map<String, Object>> getProjectList(String company,String factory,String fstate,String fline);

  @Query(value = "select d.fname,d.frequ,d.id,d.mid,d.forder,d.fcheck_result,to_char(d.create_date,'yyyy-mm-dd HH:SS:MM') cd from QMS_TESTITEM_DETAIL d where d.mid=?1 and d.ftype=?2 order by d.forder ", nativeQuery = true)
  public List<Map<String, Object>> getCheckitem(String mid,String typ);
  
  @Query(value = "select d.fname,d.frequ,d.id,d.mid,d.forder,d.fcheck_result,d.FNOTE,to_char(d.create_date,'yyyy-mm-dd HH:SS:MM') cd from QMS_TESTITEM_DETAIL d where d.mid=?1 and d.id=?2 order by d.forder ", nativeQuery = true)
  public List<Map<String, Object>> getCheckitemById(String mid,String id);
  
  @Query(value = " SELECT m.id ,m.fbill_no fname,m.fbill_no fcode, to_char(m.ftest_date,'yyyy-mm-dd HH:SS:MM')ftest_date,m.fcheck_result  FROM Qms_Test_Main m WHERE m.fstate=?1 and m.fline_no=?2 and m.fcheck_proj=?3 and  (m.ftest_date   BETWEEN SYSDATE-1 AND SYSDATE)", nativeQuery = true)
  public List<Map<String, Object>> getBillNoList(String fstate,String fline_no,String fcheck_proj);
  
}
