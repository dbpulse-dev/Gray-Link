package com.dbcat.gray.admin.dto.rabbit;

public class RabbitUserV1 extends RabbitUser {

    private String name;

    private String tags;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
