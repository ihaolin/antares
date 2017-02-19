package me.hao0.antares.common;

import com.google.common.collect.Lists;
import me.hao0.antares.common.balance.LoadBalance;
import me.hao0.antares.common.balance.RandomLoadBalance;
import org.junit.Test;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class LoadBalanceTest {

    @Test
    public void testRandomLoadBalance() throws InterruptedException {
        LoadBalance<String> b = new RandomLoadBalance<>();

        List<String> datas = Lists.newArrayList("123", "456", "789");

        for (;;){
            System.out.println(b.balance(datas));
            Thread.sleep(1000L);
        }
    }
}
