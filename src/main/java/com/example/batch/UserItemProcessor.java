package com.example.batch;

import com.example.demo.User;
import org.springframework.batch.item.ItemProcessor;

public class UserItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(final User user) throws Exception {
        final String name = user.getName().toUpperCase();
        final Integer age = user.getAge();

        final User transformedUser = new User(name, age);
        System.out.println("转换 (" + user.getName() + ") 成 (" + transformedUser.getName() + ")");

        return transformedUser;
    }
}
