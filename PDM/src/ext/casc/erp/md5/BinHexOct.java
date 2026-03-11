package ext.casc.erp.md5;

public class BinHexOct {
	  /**
	    * @description 将字节数组转换为16进制字符串
	    * @param byteArray
	    * @return
	    * @throws
	    * @author SHSASC
	    * @date 2014-4-10
	    */
	    public static String byteArrayToHex(byte[] byteArray) {
	        try {
	            // 初始化16进制字符串数组
	            char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	            // 一个byte是八位二进制，也就是2位十六进制字符
	            char[] resultCharArray = new char[byteArray.length * 2];

	            // 遍历字节数组,通过位运算转换成字符放到字符数组中去
	            int index = 0;

	            for (byte b: byteArray) {
	                resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
	                resultCharArray[index++] = hexDigits[b & 0xf];
	            }

	            // 字符数组组合成字符串返回
	            return new String(resultCharArray);

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            return "";
	        }

	    }
}
