package me.hao0.antares.store;

import me.hao0.antares.store.util.Dates;
import org.junit.Test;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class DatesTest {

    @Test
    public void testTimeInterval(){

        String start = "2016-10-12 11:22:49";
        String end = "2016-10-12 11:32:33";

        System.out.println(Dates.timeIntervalStr(Dates.toDate(start), Dates.toDate(end)));

        start = "2016-10-12 11:22:49";
        end = "2016-10-13 13:32:33";
        System.out.println(Dates.timeIntervalStr(Dates.toDate(start), Dates.toDate(end)));

        start = "2016-10-12 13:22:49";
        end = "2016-10-15 13:32:00";
        System.out.println(Dates.timeIntervalStr(Dates.toDate(start), Dates.toDate(end)));

        start = "2016-10-12 13:22:49";
        end = "2017-12-15 13:32:00";
        System.out.println(Dates.timeIntervalStr(Dates.toDate(start), Dates.toDate(end)));
    }
}
