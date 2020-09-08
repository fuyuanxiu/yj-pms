package com.system.user.entity;

import javax.persistence.Entity;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.StoredProcedureParameter;


//在包“com.kxh.example.demo.domain”下的“Contact”实体上编写存储过程的映射。
//
//@NamedStoredProcedureQueries注解表示可以包含多个存储过程的映射。
//
//@NamedStoredProcedureQuery注解就是对一个存储过程的映射。
//
//参数name，给这次映射取一个名字，后续调用时使用。
//
//参数procedureName，是数据库中真实的存储过程的名字。
//
//参数parameters，是对存储过程输入或输出参数的映射定义。

@Entity
@NamedStoredProcedureQueries({
    @NamedStoredProcedureQuery(
            name = "getContactsLikeName", 
            procedureName = "Prc_rf_j1_user_login", 
            resultClasses = { Contact.class },
            parameters = {
                    @StoredProcedureParameter(
                            mode = ParameterMode.IN, 
                            name = "name", 
                            type = String.class),
                    @StoredProcedureParameter(
                            mode = ParameterMode.IN, 
                            name = "c_MachType", 
                            type = String.class),
                    @StoredProcedureParameter(
                            mode = ParameterMode.OUT, 
                            name = "c_Result", 
                            type = String.class)
            }
        )
})
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String name;
    
    private String phone;
    
    private String mail;
    
    public Contact() {
        super();
    }
    
    public Contact(String name, String phone, String mail) {
        super();
        
        this.name = name;
        this.phone = phone;
        this.mail = mail;
    }
    
    public long getId() {
        return this.id;
    }
    
    public void setId(long value) {
        this.id = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String value) {
        this.name = value;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String value) {
        this.phone = value;
    }
    
    public String getMail() {
        return this.mail;
    }
    
    public void setMail(String value) {
        this.mail = value;
    }
}
