package com.maddob.sync;

import io.vertx.core.eventbus.Message;

/**
 * Created by martindobrev on 15/03/16.
 */
public interface MessageConsumer {
    public long processMessage(Message message);

}
