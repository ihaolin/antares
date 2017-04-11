package me.hao0.antares.store.service.impl;

import com.google.common.base.Throwables;
import me.hao0.antares.common.log.Logs;
import me.hao0.antares.common.model.AlarmEvent;
import me.hao0.antares.common.util.CollectionUtil;
import me.hao0.antares.store.dao.AlarmEventDao;
import me.hao0.antares.store.service.AlarmService;
import me.hao0.antares.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

/**
 * The alarm service implementation
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    private AlarmEventDao alarmEventDao;

    @Override
    public Response<Boolean> push(AlarmEvent event) {
        try {

            if (alarmEventDao.save(event)){
                // add to pull queue
                alarmEventDao.push(event.getId());
            }

            return Response.ok(true);
        } catch (Exception e){
            Logs.error("failed to push alarm event({}), cause: {}", event, Throwables.getStackTraceAsString(e));
            return Response.notOk("alarm.event.push.failed");
        }
    }

    @Override
    public Response<List<AlarmEvent>> pull(Integer size) {
        try {

            List<Long> eventIds = alarmEventDao.pull(size);
            if (CollectionUtil.isNullOrEmpty(eventIds)){
                return Response.ok(Collections.<AlarmEvent>emptyList());
            }

            return Response.ok(alarmEventDao.findByIds(eventIds));

        } catch (Exception e){
            Logs.error("failed to pull alarm event(size={}), cause: {}", size, Throwables.getStackTraceAsString(e));
            return Response.notOk("alarm.event.pull.failed");
        }
    }
}
