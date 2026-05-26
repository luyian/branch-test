package com.example.batch;

import com.example.demo.User;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.ClassPathResource;

/**
 * Spring Batch 批处理测试类
 *
 * <p>脱离 Web 容器，单独测试各组件功能。</p>
 *
 * @author claude
 */
public class BatchTest {

    public static void main(String[] args) throws Exception {
        System.out.println("===== 1. 测试 ItemProcessor =====");
        testProcessor();

        System.out.println();
        System.out.println("===== 2. 测试 ItemReader =====");
        testReader();

        System.out.println();
        System.out.println("===== 3. 测试 ForkJoin =====");
        testForkJoin();
    }

    /**
     * 测试 Processor：验证姓名转大写和无效数据过滤
     */
    private static void testProcessor() throws Exception {
        UserItemProcessor processor = new UserItemProcessor();

        User normal = new User("张三", 25);
        normal.setType("1");
        User result = processor.process(normal);
        System.out.println("处理结果：" + result.getName() + ", " + result.getAge());

        User invalid = new User("无效用户", -1);
        invalid.setType("1");
        User filtered = processor.process(invalid);
        System.out.println("无效数据返回：" + filtered);
    }

    /**
     * 测试 Reader：验证 CSV 文件读取
     */
    private static void testReader() throws Exception {
        FlatFileItemReader<User> reader = new FlatFileItemReaderBuilder<User>()
                .name("userItemReader")
                .resource(new ClassPathResource("users2.csv"))
                .delimited()
                .names("name", "age", "type")
                .fieldSetMapper(new BeanWrapperFieldSetMapper<User>() {{
                    setTargetType(User.class);
                }})
                .build();
        reader.open(new org.springframework.batch.item.ExecutionContext());

        User user;
        while ((user = reader.read()) != null) {
            System.out.println("读取：" + user.getName() + ", " + user.getAge() + ", " + user.getType());
        }
        reader.close();
    }

    /**
     * 测试 ForkJoin：小数组验证正确性
     */
    private static void testForkJoin() {
        int size = 1000;
        int[] array = new int[size];
        long expected = 0;
        for (int i = 0; i < size; i++) {
            array[i] = i + 1;
            expected += array[i];
        }

        java.util.concurrent.ForkJoinPool pool = new java.util.concurrent.ForkJoinPool();
        Long result = pool.invoke(new ForkJoinDemo.SumTask(array, 0, array.length));
        pool.shutdown();

        System.out.println("预期结果：" + expected);
        System.out.println("ForkJoin结果：" + result);
        System.out.println("结果一致：" + (expected == result));
    }
}
