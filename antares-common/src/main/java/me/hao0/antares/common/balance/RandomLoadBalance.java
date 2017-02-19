package me.hao0.antares.common.balance;

import java.util.List;
import java.util.Random;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public class RandomLoadBalance<T> extends AbstractLoadBalance<T> implements LoadBalance<T>  {

    private final Random random = new Random();

    @Override
    protected T doBalance(List<T> resources) {
        return resources.get(random.nextInt(resources.size()));
    }
}
