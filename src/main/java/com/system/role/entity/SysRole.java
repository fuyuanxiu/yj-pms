package com.system.role.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.app.base.entity.BaseEntity;

/**
 * 角色基础信息表
 *
 */
@Entity
@Table(name= SysRole.TABLE_NAME)
@DynamicUpdate
public class SysRole extends BaseEntity {
	private static final long serialVersionUID = -5951531333314901264L;
	public static final String TABLE_NAME = "sys_role";


	/**
	 * 备注
	 */
	@Column(length=255)
	protected String roleComment;
	
	/**
	 * 角色名
	 */
	@Column(length=255)
	protected String roleName;
	
	/**
	 * 角色代码
	 */
	@Column(length=255)
	protected String roleCode;
	

	public String getRoleComment() {
		return roleComment;
	}

	public void setRoleComment(String roleComment) {
		this.roleComment = roleComment;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	

}
