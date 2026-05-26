package com.example.batch;

import com.example.demo.User;
import org.springframework.batch.item.ItemProcessor;

/**
 * 用户数据处理器
 *
 * <p>演示 Processor 环节：将用户姓名转为大写，并对年龄做校验过滤。</p>
 *
 * @author claude
 */
public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(final User user) throws Exception {
        // 过滤年龄小于 0 的无效数据（返回 null 表示跳过该记录）
        if (user.getAge() != null && user.getAge() < 0) {
            System.out.println("过滤无效数据：" + user.getName() + "，年龄=" + user.getAge());
            return null;
        }

        String upperName = user.getName().toUpperCase();
        User transformedUser = new User();
        transformedUser.setName(upperName);
        transformedUser.setAge(user.getAge());
        transformedUser.setType(user.getType());

        System.out.println("转换 (" + user.getName() + ", " + user.getAge() + ") → (" + transformedUser.getName() + ", " + transformedUser.getAge() + ")");
        return transformedUser;
    }
}
