package com.utils.enumeration;

/**
 * 配置枚举类
 * @author Shen
 */
public enum SettingsStateEnum {

    /**
     * 匹配率编码 customer_bom_check
     */
    CUSTOMER_BOM_CHECK(SettingsEnumConstants.CUSTOMER_BOM_CHECK);

    private String value;

    SettingsStateEnum(String value){ this.value = value; }

    public String stringValue() {
        return this.value;
    }
}
