package me.hao0.antares.client;

import me.hao0.antares.client.util.MapUtil;
import me.hao0.antares.common.dto.ShardFinishDto;
import org.junit.Test;
import java.util.Date;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class MapUtilTest {

    @Test
    public void testObj2Map(){

        ShardFinishDto shardFinishDto = new ShardFinishDto();
        shardFinishDto.setInstanceId(1L);
        shardFinishDto.setShardId(2L);
        shardFinishDto.setSuccess(Boolean.TRUE);
        shardFinishDto.setStartTime(new Date());
        shardFinishDto.setEndTime(new Date());

        System.out.println(MapUtil.toMap(shardFinishDto));
    }
}
