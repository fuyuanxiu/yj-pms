package com.system.role.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.system.role.entity.UserRolesMap;

import java.util.List;

public interface UserRolesMapDao extends JpaRepository<UserRolesMap, Long> {

    public List<UserRolesMap> findByIsDelAndRoleCodeAndUserId(Integer isDel, String roleCode, Long userId);

    public List<UserRolesMap> findByIsDelAndUserId(Integer isDel, Long userId);

}
