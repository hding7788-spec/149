package com.glaway.mpm.license;

import com.javaedu.LicenseServer;
import com.javaedu.LicenseServerService;

/**
 * Created with IntelliJ IDEA.
 * User: zhangdong
 * Date: 13-5-29
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class LicenseHelper {
    /**
     *
     * @return
     */
    public boolean verifyLicense(){
        LicenseServerService licenseServerService = new LicenseServerService();
        LicenseServer licenseServer = licenseServerService.getLicenseServerPort();
//        String result = licenseServer.hello("zhangdong");
        String sysInfor = SystemInfor.getMACAddress() + ";" + SystemInfor.getCPUSerial() + ";" + SystemInfor.getHDSerial();
        boolean rntValue = false;
        try{
            rntValue = licenseServer.licenseRequest(sysInfor);
            System.out.println("验证结果：" + rntValue);
        }catch (Exception e){
            e.printStackTrace();
            rntValue = false;
        }
        return  rntValue;
    }

    public boolean dischargeLicense(){
        LicenseServerService licenseServerService = new LicenseServerService();
        LicenseServer licenseServer = licenseServerService.getLicenseServerPort();
//        String result = licenseServer.hello("zhangdong");
        String sysInfor = SystemInfor.getMACAddress() + ";" + SystemInfor.getCPUSerial() + ";" + SystemInfor.getHDSerial();
        boolean rntValue = false;
        try{
            rntValue = licenseServer.licenseDischarge(sysInfor);
            System.out.println("注销结果：" + rntValue);
        }catch (Exception e){
            e.printStackTrace();
            rntValue = false;
        }
        return  rntValue;
    }

//    Timer timer = null;
//    LicenseTask licenseTask = null;
//
//    public void startLicenseSchedule(int delay, int internal){
//        timer = new Timer(true);
//        //设置任务计划，启动和间隔时间
//        timer.schedule(new LicenseTask(), delay * 1000, internal * 1000);//毫秒
//    }
//
//    public void stopLicenseSchedule(){
//        timer.cancel();
//    }

    public static void main(String args[]){
        LicenseHelper helper = new LicenseHelper();
//        helper.startLicenseSchedule(0, 1);

    }



}
