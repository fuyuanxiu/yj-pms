package com.system.role.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.system.role.entity.SysRole;

public interface SysRoleDao extends JpaRepository<SysRole, Long> {

    public int countByIsDelAndRoleCode(Integer isDel, String roleCode);

    public SysRole findById(long id);

    public List<SysRole> findByIsDel(Integer isDel);

    public List<SysRole> findByIsDelAndRoleCode(Integer isDel, String roleCode);
    
    @Query(value = "select roleCode from userRolesMap where userId= ?1")
	public List<String> getRolesByUserId(long uid);
    
    @Transactional
    @Modifying
	@Query(value = "delete userRolesMap where userId = ?1")
	public void deleteRolesByUserId(long userId);

}
