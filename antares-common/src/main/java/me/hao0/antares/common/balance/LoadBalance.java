package me.hao0.antares.common.balance;

import java.util.List;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface LoadBalance<T> {

    /**
     * Balance a resource
     * @param resources the resource lists
     * @return a resource
     */
    T balance(List<T> resources);

    /**
     * Balance a resource
     * @param resources the resource lists
     * @param exclude exclude the resource
     * @return a resource
     */
    T balance(List<T> resources, T exclude);
}
