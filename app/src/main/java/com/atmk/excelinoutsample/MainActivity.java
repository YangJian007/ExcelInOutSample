package com.atmk.excelinoutsample;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atmk.excelinoutsample.bean.RegionExcelBean;
import com.atmk.excelinoutsample.bean.UserExcelBean;
import com.atmk.excelinoutsample.util.FileUtils1;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import top.eg100.code.excel.jxlhelper.ExcelManager;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION = 3;
    private static final int REQUESTCODE_FROM_FRAGMENT = 1001;
    private int flag;//0 导出文件  1导入文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_file_out_1, R.id.tv_file_out_2, R.id.tv_file_out_3, R.id.tv_file_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_file_out_1://asset复制excel到手机存储
                flag = 0;
                break;
            case R.id.tv_file_in://从excel表读数据并转成List
                flag = 1;
                break;
            case R.id.tv_file_out_2://直接从变量List导出到手机内存excel中（单sheet）
                flag = 2;
                break;
            case R.id.tv_file_out_3://直接从变量List<List<T>>导出到手机内存excel中（多sheet）
                flag = 3;
                break;
        }
        PermissionGen.with(this)
                .addRequestCode(STORAGE_PERMISSION)
                .permissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = STORAGE_PERMISSION)
    public void requestPermissionSuccess() {
        if (flag == 0) {//导出模板
            FileUtils1.getInstance(this).copyAssetsToSD("config", "test/BleConfig").setFileOperateCallback(new FileUtils1.FileOperateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "模板已成功导出到sd卡根目录test/BleConfig下", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(MainActivity.this, "模板导出失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (flag == 1) {//导入文件
            new LFilePicker()
                    .withActivity(MainActivity.this)
                    .withRequestCode(REQUESTCODE_FROM_FRAGMENT)
                    .withStartPath(Environment.getExternalStorageDirectory().getAbsolutePath())//指定初始显示路径
                    .withMutilyMode(false)
                    .withChooseMode(true)//文件夹选择模式
                    .withFileFilter(new String[]{"xls"})
                    .start();
        } else if (flag == 2) {//直接从变量List导出到手机内存excel中（单sheet）
            List<UserExcelBean> users = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                UserExcelBean userExcelBean = new UserExcelBean();
                userExcelBean.setName("张三" + i);
                userExcelBean.setAge("" + i);
                userExcelBean.setGender(i % 2 == 0 ? "男" : "女");
                users.add(userExcelBean);
            }
            ExcelManager excelManager = new ExcelManager();
            try {
                boolean isSuccess = excelManager.toExcel(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/FileOut/singleSheet.xls", users);
                Log.i("excel_out", isSuccess ? "导出成功" : "导出失败");
            } catch (Exception e) {
                Log.i("excel_out", "导出失败");
                e.printStackTrace();
            }
        } else if (flag == 3) {//直接从变量List<List<T>>导出到手机内存excel中（多sheet）
            List<UserExcelBean> users = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                UserExcelBean userExcelBean = new UserExcelBean();
                userExcelBean.setName("张三" + i);
                userExcelBean.setAge("" + i);
                userExcelBean.setGender(i % 2 == 0 ? "男" : "女");
                users.add(userExcelBean);
            }
             List<RegionExcelBean> regions = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                RegionExcelBean bean = new RegionExcelBean();
                bean.setProvince("省" + i);
                bean.setCityListStr("市" + i);
                regions.add(bean);
            }
            List<List<?>> lists=new ArrayList<>();
            lists.add(users);
            lists.add(regions);
            ExcelManager excelManager = new ExcelManager();
            try {
                boolean isSuccess = excelManager.toExcel2(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/FileOut/multiSheet.xls", lists);
                Log.i("excel_out", isSuccess ? "导出成功" : "导出失败");
            } catch (Exception e) {
                Log.i("excel_out", "导出失败");
                e.printStackTrace();
            }
        }
    }


    @PermissionFail(requestCode = STORAGE_PERMISSION)
    public void requestPermissionFail() {
        //失败之后的处理，我一般是跳到设置界面
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            if (requestCode == REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//文件路径
                String path = list.get(0);//文件路径
                Log.i("excel_in", "path:" + path);
                try {
                    //方式1 从asset目录读取
                   /* AssetManager asset = getAssets();
                    InputStream excelStream = asset.open("config/region.xls");*/
                    //方式2 从手机文件路径读取
                 /*   InputStream excelStream= new FileInputStream(path);
                    ExcelManager excelManager = new ExcelManager();
                    List<RegionExcelBean> excelBeans = excelManager.fromExcel(excelStream, RegionExcelBean.class); */
                    //方式3 读取表中另一sheet页
                    InputStream excelStream = new FileInputStream(path);
                    ExcelManager excelManager = new ExcelManager();
                    List<UserExcelBean> excelBeans = excelManager.fromExcel(excelStream, UserExcelBean.class);
                    Log.i("excel_in", "" + excelBeans.size());
                    Toast.makeText(this, "" + excelBeans.size(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.i("excel_in", "读取异常");
                    e.printStackTrace();
                }

            }
        }
    }

}