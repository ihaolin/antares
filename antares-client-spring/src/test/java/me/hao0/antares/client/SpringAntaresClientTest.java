package me.hao0.antares.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:antares-context.xml")
public class SpringAntaresClientTest {

    @Test
    public void testStart() throws InterruptedException {

        Thread.sleep(Integer.MAX_VALUE);

    }
}
