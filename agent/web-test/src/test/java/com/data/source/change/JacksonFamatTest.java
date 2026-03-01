package com.data.source.change;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class JacksonFamatTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        //取消时间的转化格式，默认是时间戳,同时需要设置要表现的时间格式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//        JavaTimeModule javaTimeModule = new JavaTimeModule();   // 默认序列化没有实现，反序列化有实现
//        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
//        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
//        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
//        objectMapper.registerModule(javaTimeModule);
    }


    public static void main(String[] args) {
        String oriTags = "tag1tag2";
        String[] tags = oriTags.split("\\|\\|");
        assert tags.length == 2;
    }

    @Test
    public void test() throws IOException {
        User user = new User();
        user.setName("xieyang");
        user.setDate(new Date());
        String json = objectMapper.writeValueAsString(user);
        User user1 = objectMapper.readValue(json, User.class);
        assert user1 != null;
    }


    static class User {
        private String name;
        private Date date;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
