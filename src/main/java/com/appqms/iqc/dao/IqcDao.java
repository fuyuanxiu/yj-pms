package com.appqms.iqc.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.system.user.entity.SysUser;

public interface IqcDao extends JpaRepository<SysUser, Long> {
	
/*	@Modifying
	@Query(value = "select d.fname,d.fsample_qty,d.fis_quan,d.flower,d.fupper,v.id,v.fcheck_result,v.fcheck_values,d.funit from  QMS_TESTITEM_VALUES v left join  QMS_TESTITEM_DETAIL d on d.id = v.mid where d.id=?1")
	public List<Object[]> getCheckResult(String did);*/
	
  @Query(value = "select v.id,v.fcheck_result,v.fcheck_values,v.memo,v.forder from  QMS_TESTITEM_VALUES v where v.mid=?1 ", nativeQuery = true)
  public List<Map<String, Object>> getCheckResult(String did);
  
  @Query(value = "select d.fname,d.frequ,d.id,d.mid,d.fst,d.flower,d.fupper,d.funit,d.forder,d.fsample_qty,d.fis_quan,d.fcheck_result from QMS_TESTITEM_DETAIL d where d.id=?1 order by d.forder ", nativeQuery = true)
  public List<Map<String, Object>> getItem (String mid);
   
  @Query(value = "select flot_no,max(q.id)qid from QMS_INCOMING_INTERFACE Q where Q.fcheck_result is null and q.ftype=?1 group by flot_no order by qid desc", nativeQuery = true)
  public List<Map<String, Object>> getFlotNoByType(String type);
  
  @Query(value = "select q.company,q.factory,q.fitem_no,q.flot_no,q.fstate  from QMS_INCOMING_INTERFACE Q  where   q.flot_no= :floatNo and q.ftype=:type ", nativeQuery = true)
  public List<Map<String, Object>> getInfoByFlotNoAndType(@Param("floatNo")String floatNo,@Param("type")String type);
  
  @Query(value = "select d.fname,d.frequ,d.id,d.mid,d.fst,d.flower,d.fupper,d.funit,d.forder,d.fsample_qty,d.fis_quan,d.fcheck_result from QMS_TESTITEM_DETAIL d where d.mid=?1  and d.ftype = ?2 order by d.forder ", nativeQuery = true)
  public List<Map<String, Object>> getCheckitem(String mid,String ftype);

    @Query(value = "select q.id from QMS_INCOMING_INTERFACE q where q.flot_no = ?1", nativeQuery = true)
  public List<Map<String, Object>> getFlotIdByFlotNo(String flot_no);
}
