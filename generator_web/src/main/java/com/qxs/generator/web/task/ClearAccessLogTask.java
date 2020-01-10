package com.qxs.generator.web.task;

import com.qxs.generator.web.model.config.SystemParameter;
import com.qxs.generator.web.service.config.ISystemParameterService;
import com.qxs.generator.web.service.log.IAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 清理用户访问日志定时任务
 * @author qixingshen
 * @date 2019-03-22
 * **/
@Component
public class ClearAccessLogTask {

    @Autowired
    private ISystemParameterService systemParameterService;

    @Autowired
    private IAccessService accessService;

    @Scheduled(cron = "${task.clearAccessLogTask.cron:0 0 3 * * ?}")
    public void clear(){
        SystemParameter systemParameter = systemParameterService.findSystemParameter();
        int accessLogRemainDays = systemParameter.getAccessLogRemainDays();
        LocalDate localDate = LocalDate.now();
        //减去保留天数
        localDate = localDate.minusDays(accessLogRemainDays);

        accessService.clear(String.format("%s 00:00:00", localDate.toString()));
    }
}
