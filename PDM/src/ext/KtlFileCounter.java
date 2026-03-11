package ext;

import java.io.File;

public class KtlFileCounter {
    public static void main(String[] args) {
        // 请替换为你要统计的文件夹路径
        String folderPath = "D:\\04_Glaway\\01_code\\数据迁移脚本代码20240531\\代码导出20250531\\01_ETL脚本\\基础数据迁移\\基础模块";
        File folder = new File(folderPath);

        if (!folder.exists()) {
            System.out.println("错误：指定的文件夹不存在");
            return;
        }

        if (!folder.isDirectory()) {
            System.out.println("错误：指定的路径不是一个文件夹");
            return;
        }

        int count = countKtlFiles(folder);
        System.out.println("在文件夹 " + folderPath + " 中，共有 " + count + " 个 .ktl 后缀的文件");
    }

    /**
     * 递归统计指定文件夹中.ktl后缀文件的数量
     * @param folder 要统计的文件夹
     * @return .ktl后缀文件的数量
     */
    private static int countKtlFiles(File folder) {
        int count = 0;
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归统计子文件夹中的.ktl文件
                    count += countKtlFiles(file);
                } else if (file.isFile() && file.getName().endsWith("ktr")) {
                    // 是文件且后缀名为.ktl
                    count++;
                }
            }
        }

        return count;
    }
}
