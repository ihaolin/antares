package me.hao0.antares.common;

import me.hao0.antares.common.dto.ShardFinishDto;
import me.hao0.antares.common.util.Jacksons;
import org.junit.Test;
import java.util.Date;

public class JacksonsTest {

    @Test
    public void testObj2Map(){

        ShardFinishDto shardFinishDto = new ShardFinishDto();
        shardFinishDto.setInstanceId(1L);
        shardFinishDto.setShardId(2L);
        shardFinishDto.setSuccess(Boolean.TRUE);
        shardFinishDto.setStartTime(new Date());
        shardFinishDto.setEndTime(null);

        System.out.println(Jacksons.toMap(shardFinishDto));
    }
}
