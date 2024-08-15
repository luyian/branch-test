package com.example.demo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
//@EqualsAndHashCode
public class User {
    private Integer id;
    private String name;
    private String code;
    private Integer age;
    private String type;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name) && Objects.equals(code, user.code) && Objects.equals(age, user.age) && Objects.equals(type, user.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, code, age, type);
    }
}
