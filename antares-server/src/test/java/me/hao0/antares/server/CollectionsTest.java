package me.hao0.antares.server;

import com.google.common.collect.Lists;
import org.junit.Test;
import java.util.Collections;
import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class CollectionsTest {

    @Test
    public void testSort(){
        List<String> data = Lists.newArrayList("abbc", "123", "xxx", "bcd");
        Collections.sort(data);
        System.out.println(data);
    }
}
