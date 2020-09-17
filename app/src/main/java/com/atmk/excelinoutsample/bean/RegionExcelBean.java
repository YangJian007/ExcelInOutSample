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
@ExcelSheet(sheetName = "地区表")
public class RegionExcelBean {

    @ExcelContent(titleName = "省")
    private String province;
    @ExcelContent(titleName = "市")
    private String cityListStr;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCityListStr() {
        return cityListStr;
    }

    public void setCityListStr(String cityListStr) {
        this.cityListStr = cityListStr;
    }
}
