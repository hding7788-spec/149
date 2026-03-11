package ext.casc.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SuppressWarnings("InnerClassMayBeStatic")
public class ParameterSplitter {
    private static final Pattern DELIMITER_PATTERN = Pattern.compile("(\\|\\||&)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)");
    public static String  extractNumber(String input) {
        if (input == null || input.isEmpty()) {
            throw new IllegalArgumentException("输入字符串不能为空");
        }

        Matcher matcher = NUMBER_PATTERN.matcher(input);
        if (matcher.find()) {
            String numberStr = matcher.group(1);
            return numberStr;
        }
        return input;
    }


    public static SplitResult split(String input) {
        if (input == null || input.isEmpty()) {
            return new SplitResult(new String[0], null);
        }

        Matcher matcher = DELIMITER_PATTERN.matcher(input);
        String delimiter = null;

        if (matcher.find()) {
            delimiter = matcher.group(1);
            String[] parameters = input.split(Pattern.quote(delimiter));
            return new SplitResult(parameters, delimiter);
        }

        // 如果没有找到分隔符，返回原始字符串作为唯一参数
        return new SplitResult(new String[]{input}, null);
    }



    public static void main(String[] args) {
        String example1 = "基础参数1||基础参数2||基础参数3";
        SplitResult result1 = split(example1);
        System.out.println("样例1解析结果: " + result1);
        System.out.println("样例1解析结果: " + result1.getDelimiter());
        System.out.println("样例1解析结果: " + result1.getParameters());
        System.out.println("样例2解析结果: " + result1.getDelimiter().equals("&"));



        String example2 = "基础参数1&基础参数2&基础参数3&基础参数4";
        SplitResult result2 = split(example2);
        System.out.println("样例2解析结果: " + result2);
        System.out.println("样例2解析结果: " + result2.getDelimiter().equals("||"));





        String example3 = "单独参数";
        SplitResult result3 = split(example3);
        System.out.println("样例3解析结果: " + result3);
        System.out.println("样例3解析结果: " + result3.getParameters());
        System.out.println("样例3解析结果: " + result3.getDelimiter());


        System.out.println(extractNumber("0pa"));
        System.out.println(extractNumber("2.1pa"));
        System.out.println(extractNumber("xxxpa"));
        System.out.println(extractNumber("0  g"));
        System.out.println(extractNumber(" 0 "));
        System.out.println(extractNumber("0"));
    }
}
