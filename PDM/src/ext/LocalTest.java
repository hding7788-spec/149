package ext;

import com.singularsys.jep.JepException;
import org.nfunk.jep.JEP;

public class LocalTest {
    public static void main(String[] args) {
        JEP jep = new JEP();
        try {
            String parser = "120 + 1.5 *【有效厚度】";
            jep.addVariable("有效厚度", 4); // 假设有效厚度为5，可替换为实际值
            jep.parseExpression(parser.replaceAll("【","").replaceAll("】",""));
            double result = jep.getValue();
            System.out.println("计算结果: " + result);

            // 支持开根号示例
            jep.parseExpression("sqrt(有效厚度)");
            double sqrtResult = jep.getValue();
            System.out.println("开根号结果: " + sqrtResult);



            jep.addStandardConstants();
            jep.addStandardFunctions();

            // 1. 使用 ^ 符号进行幂运算
            jep.parseExpression("2^3"); // 2的3次方
            System.out.println("2^3 = " + jep.getValue()); // 输出：8.0

            // 2. 使用 pow() 函数进行幂运算
            jep.parseExpression("pow(2, 3)"); // 等同于2^3
            System.out.println("pow(2, 3) = " + jep.getValue()); // 输出：8.0

            // 3. 结合变量的幂运算（以【有效厚度】为例）
            jep.addVariable("有效厚度", 4.0);
            jep.parseExpression("有效厚度^2 + pow(有效厚度, 0.5)"); // 平方 + 开根号（0.5次方）
            System.out.println("4^2 + sqrt(4) = " + jep.getValue()); // 输出：16 + 2 = 18.0

            // 4. 复杂表达式中的幂运算
            jep.parseExpression("10 + 2^3 * 有效厚度"); // 10 + (8 * 4)
            System.out.println("10 + 2^3 * 4 = " + jep.getValue()); // 输出：42.0
        } catch (JepException e) {
            throw new RuntimeException(e);
        }

    }
}
