package com.dbcat.gray.admin.event;

import com.dbcat.gray.admin.dto.AppInstanceReport;
import org.springframework.context.ApplicationEvent;

public class InstanceReportEvent extends ApplicationEvent {
    private AppInstanceReport data;

    public InstanceReportEvent(AppInstanceReport data) {
        super(data);
        this.data = data;
    }

    public AppInstanceReport getData() {
        return data;
    }

    public void setData(AppInstanceReport data) {
        this.data = data;
    }
}
