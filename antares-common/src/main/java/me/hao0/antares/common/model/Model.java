package me.hao0.antares.common.model;

import java.io.Serializable;
import java.util.Date;

/**
 * The class implements this interface will be persist to the storage
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Model<K> extends Serializable {

    K getId();

    void setId(K id);

    void setCtime(Date ctime);

    void setUtime(Date utime);
}
