package com.system.log.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.system.log.entity.SysLog;

public interface SysLogDao extends JpaRepository<SysLog, Long> {


}
