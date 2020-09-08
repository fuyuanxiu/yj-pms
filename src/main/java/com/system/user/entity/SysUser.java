package com.system.user.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 用户基础信息表
 *
 */
@Entity(name = "SysUser")
@Table(name = SysUser.TABLE_NAME)
@DynamicUpdate
@ApiModel


//@NamedStoredProcedureQuery(name = "User.plusl",procedureName = "PRC_RF_J1_USER_LOGIN",parameters = {
//        @StoredProcedureParameter(mode = ParameterMode.IN,name = "C_USER_NO",type=String.class),
//        @StoredProcedureParameter(mode = ParameterMode.IN,name = "c_MachType",type=String.class),
//        @StoredProcedureParameter(mode = ParameterMode.OUT,name="c_Result",type = String.class)
//})
//pluslinout--mysql就可以了
//@NamedStoredProcedureQuery(name = "User.plusl",procedureName = "PLUSLINOUT",parameters = {
//        @StoredProcedureParameter(mode = ParameterMode.IN,name = "arg",type=String.class),
//        @StoredProcedureParameter(mode = ParameterMode.OUT,name="res",type = Integer.class)
//})

@NamedStoredProcedureQuery(name="test", procedureName = "PT",parameters= {
        @StoredProcedureParameter(mode=ParameterMode.IN,type=int.class, name="a"),
        @StoredProcedureParameter(mode=ParameterMode.OUT,type=int.class, name="b")
})

public class SysUser extends com.app.base.entity.BaseEntity {
    private static final long serialVersionUID = 4625660587007894370L;
    public static final String TABLE_NAME = "sys_user";

    /**
     * 编码（用户名）
     */
    @ApiModelProperty(name = "userCode", value = "编码（用户名）")
    @Column(length = 100)
    @NotNull
    protected String userCode;

    /**
     * 名称
     */
    @ApiModelProperty(name = "userName", value = "名称")
    @Column(length = 100)
    @NotNull
    protected String userName;

    /**
     * 密码
     */
    @ApiModelProperty(name = "userPassword", value = "密码")
    @Column(length = 255)
    @NotNull
    protected String userPassword;

    /**
     * 类型（0：系统用户 / 1：供应商用户）
     */
    @ApiModelProperty(name = "userType", value = "类型（0：系统用户 / 1：供应商用户）")
    @Column
    protected int userType;

    /**
     * 状态（0:启用 / 1:禁用）
     */
    @ApiModelProperty(name = "userStatus", value = "状态（0:启用 / 1:禁用）")
    @Column
    protected int userStatus;

    /**
     * 注释
     */
    @ApiModelProperty(name = "userComment", value = "注释")
    @Column(length = 255)
    protected String userComment;

    /**
     * 邮箱
     */
    @ApiModelProperty(name = "userEmail", value = "邮箱")
    @Column(length = 100)
    protected String userEmail;

    /**
     * 手机号
     */
    @ApiModelProperty(name = "userMobile", value = "手机号")
    @Column(length = 100)
    protected String userMobile;

    /**
     * 是否为超级管理员（0：普通用户，1：超级）
     */
    @ApiModelProperty(name = "userIsSuper", value = "是否为超级管理员")
    @Column
    protected int userIsSuper;

    /**
     * 创建人
     */
    @ApiModelProperty(name = "pkCreatedBy", value = "创建人")
    @Column
    protected Long pkCreatedBy;

    /**
     * 修改人
     */
    @ApiModelProperty(name = "pkModifiedBy", value = "修改人")
    @Column
    protected Long pkModifiedBy;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public int getUserIsSuper() {
        return userIsSuper;
    }

    public void setUserIsSuper(int userIsSuper) {
        this.userIsSuper = userIsSuper;
    }

    public Long getPkCreatedBy() {
        return pkCreatedBy;
    }

    public void setPkCreatedBy(Long pkCreatedBy) {
        this.pkCreatedBy = pkCreatedBy;
    }

    public Long getPkModifiedBy() {
        return pkModifiedBy;
    }

    public void setPkModifiedBy(Long pkModifiedBy) {
        this.pkModifiedBy = pkModifiedBy;
    }
}
