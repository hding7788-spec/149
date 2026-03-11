package ext.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CPUBenchmark {
    private static final int WARMUP_ITERATIONS = 3;
    private static final int TEST_ITERATIONS = 10;
    private static final int PRIME_MAX = 1_000_000;
    private static final int MATRIX_SIZE = 2048;
    private static final int FIBONACCI_DEPTH = 42;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        printSystemInfo();

        // 预热JVM
        warmup();

        // 执行各项测试
        System.out.println("\n=== 开始性能测试 ===");

        double primeScore = runPrimeTest();
        double matrixScore = runMatrixTest();
        double fibonacciScore = runFibonacciTest();
        double concurrencyScore = runConcurrencyTest();

        // 计算综合分数
        double totalScore = (primeScore + matrixScore + fibonacciScore + concurrencyScore) / 4;

        // 输出最终结果
        System.out.println("\n=== 测试结果 ===");
        System.out.printf("整数运算性能: %.2f\n", primeScore);
        System.out.printf("浮点运算性能: %.2f\n", matrixScore);
        System.out.printf("递归计算性能: %.2f\n", fibonacciScore);
        System.out.printf("并发处理性能: %.2f\n", concurrencyScore);
        System.out.printf("综合性能分数: %.2f\n", totalScore);
    }

    private static void printSystemInfo() {
        System.out.println("=== 系统信息 ===");
        System.out.printf("操作系统: %s %s\n", System.getProperty("os.name"), System.getProperty("os.version"));
        System.out.printf("Java版本: %s\n", System.getProperty("java.version"));
        System.out.printf("可用处理器核心数: %d\n", THREAD_COUNT);
        System.out.printf("JVM最大内存: %.2f GB\n", Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0 * 1024.0));

        try {
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo | grep 'model name' | head -1");
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line = reader.readLine();
                    if (line != null) {
                        System.out.println("CPU型号: " + line.split(":")[1].trim());
                    }
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取CPU型号失败: " + e.getMessage());
        }
    }

    private static void warmup() {
        System.out.println("\n=== JVM预热 ===");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            System.out.printf("预热迭代 %d/%d...\n", i + 1, WARMUP_ITERATIONS);
            runPrimeTest();
            runMatrixTest();
            runFibonacciTest();
            runConcurrencyTest();
        }
        System.out.println("预热完成");
    }

    // 素数生成测试 (整数运算密集型)
    private static double runPrimeTest() {
        System.out.println("\n=== 素数生成测试 ===");
        long totalTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            int count = 0;

            for (int j = 2; j <= PRIME_MAX; j++) {
                boolean isPrime = true;
                for (int k = 2; k <= Math.sqrt(j); k++) {
                    if (j % k == 0) {
                        isPrime = false;
                        break;
                    }
                }
                if (isPrime) count++;
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            totalTime += duration;

            System.out.printf("迭代 %d/%d: 找到 %d 个素数，耗时 %d ms\n",
                    i + 1, TEST_ITERATIONS, count, duration);
        }

        double avgTime = totalTime / (double) TEST_ITERATIONS;
        // 分数越高性能越好
        return 1000 / avgTime;
    }

    // 矩阵乘法测试 (浮点运算密集型)
    private static double runMatrixTest() {
        System.out.println("\n=== 矩阵乘法测试 ===");
        long totalTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            long startTime = System.nanoTime();

            // 初始化矩阵
            double[][] a = new double[MATRIX_SIZE][MATRIX_SIZE];
            double[][] b = new double[MATRIX_SIZE][MATRIX_SIZE];
            double[][] c = new double[MATRIX_SIZE][MATRIX_SIZE];

            // 填充随机数据
            for (int j = 0; j < MATRIX_SIZE; j++) {
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    a[j][k] = Math.random();
                    b[j][k] = Math.random();
                }
            }

            // 执行矩阵乘法
            for (int j = 0; j < MATRIX_SIZE; j++) {
                for (int k = 0; k < MATRIX_SIZE; k++) {
                    for (int l = 0; l < MATRIX_SIZE; l++) {
                        c[j][k] += a[j][l] * b[l][k];
                    }
                }
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            totalTime += duration;

            System.out.printf("迭代 %d/%d: 完成 %dx%d 矩阵乘法，耗时 %d ms\n",
                    i + 1, TEST_ITERATIONS, MATRIX_SIZE, MATRIX_SIZE, duration);
        }

        double avgTime = totalTime / (double) TEST_ITERATIONS;
        // 分数越高性能越好
        return 1000 / avgTime;
    }

    // 斐波那契递归测试 (分支预测密集型)
    private static double runFibonacciTest() {
        System.out.println("\n=== 斐波那契递归测试 ===");
        long totalTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            long startTime = System.nanoTime();

            // 使用多线程计算多个斐波那契数
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            Future[] futures = new Future[THREAD_COUNT];

            for (int j = 0; j < THREAD_COUNT; j++) {
                final int depth = FIBONACCI_DEPTH - (j % 5); // 稍微变化深度
                futures[j] = executor.submit(new Callable<Long>() {
                    @Override
                    public Long call() throws Exception {
                        return fibonacci(depth);
                    }
                });
            }

            // 获取结果
            for (Future future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            totalTime += duration;

            System.out.printf("迭代 %d/%d: 计算 %d 个斐波那契数，耗时 %d ms\n",
                    i + 1, TEST_ITERATIONS, THREAD_COUNT, duration);
        }

        double avgTime = totalTime / (double) TEST_ITERATIONS;
        // 分数越高性能越好
        return 1000 / avgTime;
    }

    private static long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    // 并发测试 (多线程协调能力)
    private static double runConcurrencyTest() {
        System.out.println("\n=== 并发处理测试 ===");
        long totalTime = 0;

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            long startTime = System.nanoTime();

            AtomicInteger counter = new AtomicInteger(0);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            // 提交大量小任务
            for (int j = 0; j < 1000 * THREAD_COUNT; j++) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        // 模拟一些计算
                        for (int k = 0; k < 10000; k++) {
                            counter.incrementAndGet();
                            counter.decrementAndGet();
                        }
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            totalTime += duration;

            System.out.printf("迭代 %d/%d: 执行 %d 个并发任务，耗时 %d ms\n",
                    i + 1, TEST_ITERATIONS, 1000 * THREAD_COUNT, duration);
        }

        double avgTime = totalTime / (double) TEST_ITERATIONS;
        // 分数越高性能越好
        return 1000 / avgTime;
    }
}    