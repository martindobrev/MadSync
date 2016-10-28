package com.maddob.sync.message;

import io.vertx.core.eventbus.Message;

/**
 * Basic interface for each message
 *
 * Created by martindobrev on 15/03/16.
 */
public interface MadSyncMessageConsumer {
    long processMessage(Message message);

}
