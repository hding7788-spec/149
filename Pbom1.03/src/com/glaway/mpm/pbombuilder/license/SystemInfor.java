package com.glaway.mpm.pbombuilder.license;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

//import org.hyperic.sigar.Sigar;

//import org.hyperic.sigar.Sigar;

public class SystemInfor {
	public static void main(String[] arguments) throws Exception {
		InetAddress ia = InetAddress.getLocalHost();
		System.out.println("MAC ......... " + getMACAddress());

		System.out.println("CPU SNO......." + getCPUSerial());
		System.out.println("HD SNO........" + getHDSerial());
	}

	public static String getMACAddress() throws Exception {
		InetAddress ia = InetAddress.getLocalHost();
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		return sb.toString().toUpperCase();
	}

	/**
	 *
	 * @param drive
	 *            硬盘驱动器分区 如C,D
	 * @return 该分区的卷标
	 */
	public static String getHDSerial() {

//
//		String result = "";
//		try {
//			Sigar sigar = new Sigar();
//			org.hyperic.sigar.FileSystem[] filesystems = sigar.getFileSystemList();
//
//			File file = File.createTempFile("tmp", ".vbs");
//			file.deleteOnExit();
//			FileWriter fw = new java.io.FileWriter(file);
//
//			String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
//					+ "Set colDrives = objFSO.Drives\n"
//					+ "Set objDrive = colDrives.item(\""
//					+ filesystems[0].getDevName()
//					+ "\")\n"
//					+ "Wscript.Echo objDrive.SerialNumber";
//			fw.write(vbs);
//			fw.close();
//			Process p = Runtime.getRuntime().exec(
//					"cscript //NoLogo " + file.getPath());
//			BufferedReader input = new BufferedReader(new InputStreamReader(
//					p.getInputStream()));
//			String line;
//			while ((line = input.readLine()) != null) {
//				result += line;
//			}
//			input.close();
//			file.delete();
//		} catch (Exception e) {
//
//		}
//		if (result.trim().length() < 1 || result == null) {
//			result = "无磁盘ID被读取";
//
//		}
//
//		return result.trim();
		return null;
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
			FileWriter fw = new java.io.FileWriter(file);

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
