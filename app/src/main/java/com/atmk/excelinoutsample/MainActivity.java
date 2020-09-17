package com.atmk.excelinoutsample;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.atmk.excelinoutsample.util.FileUtils1;
import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv_file_out)
    Button tvFileOut;
    @BindView(R.id.tv_file_in)
    Button tvFileIn;

    private static final int STORAGE_PERMISSION = 3;
    private static final int REQUESTCODE_FROM_FRAGMENT = 1001;
    private int flag;//0 导出文件  1导入文件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.tv_file_out, R.id.tv_file_in})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_file_out:
                flag = 0;
                PermissionGen.with(this)
                        .addRequestCode(STORAGE_PERMISSION)
                        .permissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();
                break;
            case R.id.tv_file_in:
                flag = 1;
                PermissionGen.with(this)
                        .addRequestCode(STORAGE_PERMISSION)
                        .permissions(
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .request();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = STORAGE_PERMISSION)
    public void requestPermissionSuccess() {
        if (flag == 0) {//导出文件
            FileUtils1.getInstance(this).copyAssetsToSD("config", "test/BleConfig").setFileOperateCallback(new FileUtils1.FileOperateCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this,"模板已成功导出到sd卡根目录test/BleConfig下",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(String error) {
                    Toast.makeText(MainActivity.this,"模板导出失败",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this,path,Toast.LENGTH_SHORT).show();
            }
        }
    }

}