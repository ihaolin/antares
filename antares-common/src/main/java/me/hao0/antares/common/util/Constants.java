package me.hao0.antares.common.util;

/**
 * Author: haolin
 * Email:  haolin.h0@gmail.com
 */
public interface Constants {

    String APP_NAME_HEADER = "appName";

    String APP_KEY_HEADER = "appSecret";

    String CLIENT_VERSION_HEADER = "Client-Ver";

    String CLIENT_LANG_HEADER = "Client-Lang";

    String HTTP_PREFIX = "http://";

    String JOB_SHARD_PARAMS_DELIMITER = ";";

    String JOB_SHARD_PARAMS_KV_DELIMITER = "=";

    Integer DEFAULT_LIST_BATCH_SIZE = 100;

    String SCRIPT_JOB_ENV_SHARD_ITEM = "JOB_SHARD_ITEM";

    String SCRIPT_JOB_ENV_SHARD_PARAM = "JOB_SHARD_PARAM";

    Integer MAX_ERROR_LENGTH = 2048;
}
