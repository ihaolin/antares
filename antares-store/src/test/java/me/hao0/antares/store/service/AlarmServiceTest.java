package me.hao0.antares.store.service;

import me.hao0.antares.common.model.AlarmEvent;
import me.hao0.antares.common.model.enums.AlarmEventType;
import me.hao0.antares.store.BaseTest;
import me.hao0.antares.common.util.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class AlarmServiceTest extends BaseTest {

    @Autowired
    private AlarmService alarmService;

    @Test
    public void testPush(){

        for (int i=0; i<1; i++){

            AlarmEvent event = new AlarmEvent();
            event.setJobId(1L);
            event.setDetail("xxxx" + i);
            event.setType(AlarmEventType.JOB_TIMEOUT.value());

            Response<Boolean> pushResp = alarmService.push(event);
            assertTrue(pushResp.isOk());
            assertTrue(pushResp.getData());
        }

    }

    @Test
    public void testPull(){

        int pullSize = 10;

        Response<List<AlarmEvent>> pullResp = alarmService.pull(pullSize);
        assertTrue(pullResp.isOk());
        assertNotNull(pullResp.getData());
        assertEquals(0, pullResp.getData().size());
    }
}
