package me.hao0.antares.common.balance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class RoundLoadBalance<T> extends AbstractLoadBalance<T> implements LoadBalance<T> {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    protected T doBalance(List<T> resources) {
        return resources.get(counter.getAndIncrement() % resources.size());
    }
}
