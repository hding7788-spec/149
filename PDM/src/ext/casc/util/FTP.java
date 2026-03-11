package ext.casc.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTP {

	private FTPClient ftp;

	/**
	 *
	 * @param path
	 *            上传到ftp服务器哪个路径下
	 * @param addr
	 *            地址
	 * @param port
	 *            端口号
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 * @throws Exception
	 */
	public boolean connect(String path, String addr, int port, String username, String password) throws Exception {
		boolean result = false;
		ftp = new FTPClient();
		ftp.setControlEncoding("gb2312");
		int reply;
		ftp.connect(addr, port);
		ftp.login(username, password);
		ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			return result;
		}
		//先创建文件夹
		ftp.makeDirectory(path);
		ftp.changeWorkingDirectory(path);
		result = true;
		return result;
	}

	/**
	 *
	 * @param file
	 *            上传的文件或文件夹
	 * @throws Exception
	 */
	public void upload(File file) throws Exception {
		if (file.isDirectory()) {
			ftp.makeDirectory(file.getName());
			ftp.changeWorkingDirectory(file.getName());
			String[] files = file.list();
			for (int i = 0; i < files.length; i++) {
				File file1 = new File(file.getPath() + "/" + files[i]);
				if (file1.isDirectory()) {
					upload(file1);
					ftp.changeToParentDirectory();
				} else {
					File file2 = new File(file.getPath() + "/" + files[i]);
					FileInputStream input = new FileInputStream(file2);
					ftp.storeFile(file2.getName(), input);
					input.close();
				}
			}
		} else {
			File file2 = new File(file.getPath());
			FileInputStream input = new FileInputStream(file2);
			ftp.storeFile(file2.getName(), input);
			input.close();
		}
	}

	public void List(String pathName) throws IOException {
		String directory = pathName;
		// 更换目录到当前目录
		ftp.changeWorkingDirectory(directory);
		FTPFile[] files = ftp.listFiles();
		System.out.println(directory);
		for (FTPFile file : files) {
			System.out.println(11);// ///////这个11总是输出不了
			if (file.isFile()) {
				System.out.println(file.getName());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		FTP t = new FTP();
		t.connect("/test", "10.125.192.32", 21, "root", "Server@149");
		File file = new File("e:\\uploadify");
		t.upload(file);
	}
}