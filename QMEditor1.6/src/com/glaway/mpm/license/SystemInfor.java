package com.glaway.mpm.license;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

public class SystemInfor {
	public static void main(String[] arguments) throws Exception {

//        Enumeration<NetworkInterface> e=NetworkInterface.getNetworkInterfaces();
//        while(e.hasMoreElements())
//        {
//            System.out.println("+++++++++++" + e.nextElement());
//        }


        try {
            InetAddress   test   =   InetAddress.getByName("localhost")   ;
            System.out.println("++++++" + test.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }




        System.out.println(System.getProperty("java.library.path"));

		System.out.println("MAC ......... " + getMACAddress());

		System.out.println("CPU SNO......." + getCPUSerial());
		System.out.println("HD SNO........" + getHDSerial());

	}

    /**
     * 获取盘符
     * @return
     */
    private static String getFirstDriverName(){
        String driverName = "C";
        File[] roots = File.listRoots();
        if(roots.length > 0){
            driverName = roots[0].getAbsolutePath().substring(0, 1);
        }
        return driverName;
    }

	public static String getMACAddress(){
        StringBuffer sb = new StringBuffer();
        try {
            InetAddress ia = InetAddress.getLocalHost();
            System.out.println("ip is = " + ia.getHostAddress());
            byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

            for (int i = 0; i < mac.length; i++) {
                if (i != 0) {
                    sb.append("-");
                }
                String s = Integer.toHexString(mac[i] & 0xFF);
                sb.append(s.length() == 1 ? 0 + s : s);
            }
            return sb.toString().toUpperCase();
        }catch (Exception e){
            e.printStackTrace();
            return "InvalidMacAdd";
        }
	}

	/**
	 *
	 * @return 该分区的卷标
	 */
	public static String getHDSerial() {


		String result = "";
		try {
//			Sigar sigar = new Sigar();
//			org.hyperic.sigar.FileSystem[] filesystems = sigar.getFileSystemList();

			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);

			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
					+ "Set colDrives = objFSO.Drives\n"
					+ "Set objDrive = colDrives.item(\""
					+    SystemInfor.getFirstDriverName()     //filesystems[0].getDevName()
					+ "\")\n"
					+ "Wscript.Echo objDrive.SerialNumber";
			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			file.delete();
		} catch (Exception e) {

		}
		if (result.trim().length() < 1 || result == null) {
			result = "无磁盘ID被读取";

		}

		return result.trim();
	}

	/**
	 * 获取CPU号,多CPU时,只取第一个
	 *
	 * @return
	 */
	public static String getCPUSerial() {
		String result = "";
		try {
			File file = File.createTempFile("tmp", ".vbs");
			file.deleteOnExit();
			FileWriter fw = new FileWriter(file);

			String vbs = "On Error Resume Next \r\n\r\n"
					+ "strComputer = \".\"  \r\n"
					+ "Set objWMIService = GetObject(\"winmgmts:\" _ \r\n"
					+ "    & \"{impersonationLevel=impersonate}!\\\\\" & strComputer & \"\\root\\cimv2\") \r\n"
					+ "Set colItems = objWMIService.ExecQuery(\"Select * from Win32_Processor\")  \r\n "
					+ "For Each objItem in colItems\r\n "
					+ "    Wscript.Echo objItem.ProcessorId  \r\n "
					+ "    exit for  ' do the first cpu only! \r\n"
					+ "Next                    ";

			fw.write(vbs);
			fw.close();
			Process p = Runtime.getRuntime().exec(
					"cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				result += line;
			}
			input.close();
			file.delete();
		} catch (Exception e) {
			e.fillInStackTrace();
		}
		if (result.trim().length() < 1 || result == null) {
			result = "无CPU_ID被读取";
		}
		return result.trim();
	}

}
