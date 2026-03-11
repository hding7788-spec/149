package com.glaway.mpm.license;

import com.glaway.mpm.util.SwingUtil;
import com.glaway.mpm.view.NewTechnicsPart;

import javax.swing.*;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: zhangdong
 * Date: 13-5-29
 * Time: 下午1:21
 * To change this template use File | Settings | File Templates.
 */
public class LicenseTask extends TimerTask {
    @Override
    public void run() {
        //To change body of implemented methods use File | Settings | File Templates.
        System.out.println("task running ... ");
        LicenseHelper helper = new LicenseHelper();
        boolean isPass = helper.verifyLicense();
        if(isPass){
            System.out.println(" passed ");
        }else
        {
            System.out.print(" not passed ");
            int result = SwingUtil.showConfirmDialog("License 验证失败，是否重新验证？", "License 验证", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION){
                System.out.print("重试？");
                NewTechnicsPart.startLicenseTask();

            }else{
                System.out.println("推出。");
                System.exit(0);
            }

        }
    }
}
