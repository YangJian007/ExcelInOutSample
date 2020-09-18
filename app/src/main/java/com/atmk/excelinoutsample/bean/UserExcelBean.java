package com.atmk.excelinoutsample.bean;

import top.eg100.code.excel.jxlhelper.annotations.ExcelContent;
import top.eg100.code.excel.jxlhelper.annotations.ExcelSheet;

/**
 * @author 杨剑
 * @fileName
 * @date 2020/8/25
 * @describe
 * @changeUser
 * @changTime
 */
@ExcelSheet(sheetName = "用户表")
public class UserExcelBean {
    //注意： 属性类型必须是String ，否则会解析异常
    @ExcelContent(titleName = "姓名")
    private String name;
    @ExcelContent(titleName = "年龄")
    private String age;
    @ExcelContent(titleName = "性别")
    private String gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
