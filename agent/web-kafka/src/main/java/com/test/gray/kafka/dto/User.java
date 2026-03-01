package com.test.gray.kafka.dto;


import java.io.Serializable;

public class User implements Serializable {

    private String name;
    private int age;
    private long time;


    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
