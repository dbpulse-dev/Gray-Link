package com.dbcat.gray.admin.dto.rabbit;

import java.util.List;

public class RabbitUserV2 extends RabbitUser {

    private String name;

    private List<String> tags;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
