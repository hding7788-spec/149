package ext.casc.erp.md5;

import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

 public class Md5{

/**

    * @description 计算文件MD5值

    * @param fileName 文件全路径名

    * @return 文件MD5值(32位)

    * @throws

    * @author machongqi

    * @date 2015-8-13

    */

    public static String fileMD5(String fileName) {

        //缓冲区大小

        int bufferSize = 256 * 1024;



        FileInputStream fileInputStream = null;

        DigestInputStream digestInputStream = null;



        try {

            //MD5转换器

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            fileInputStream = new FileInputStream(fileName);

            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);



            // read的过程中进行MD5处理，直到读完文件

            byte[] buffer =new byte[bufferSize];

            while (digestInputStream.read(buffer) > 0);



            //获取最终的MessageDigest

            messageDigest= digestInputStream.getMessageDigest();



            //获取MD5字节数组，包含16个元素

            byte[] resultByteArray = messageDigest.digest();



            //把字节数组转换为字符串

            return Md5.byteArrayToHex(resultByteArray);



        } catch (Exception ex) {

            ex.printStackTrace();

            return "";

        } finally {

            try {

                digestInputStream.close();



             } catch (Exception ex) {

             }



             try {

                 fileInputStream.close();

             } catch (Exception ex) {

             }

        }

    }

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



