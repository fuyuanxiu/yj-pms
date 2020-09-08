package com.system.role.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.app.base.entity.BaseEntity;

import javax.persistence.*;

@Entity(name = "userRolesMap")
@Table(name= UserRolesMap.TABLE_NAME)
@DynamicUpdate
public class UserRolesMap  extends BaseEntity{
	private static final long serialVersionUID = -5951531333314901264L;
	public static final String TABLE_NAME = "t_user_roles_map";

	/**
	 * 用户ID
	 */
	protected Long userId;
	
	/**
	 * 角色编码
	 */
	protected String roleCode;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	

}
