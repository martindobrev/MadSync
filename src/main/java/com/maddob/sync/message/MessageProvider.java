package com.maddob.sync.message;

import com.maddob.sync.protocol.RequestData;
import io.vertx.core.eventbus.Message;

import java.util.Map;

/**
 * Created by martindobrev on 15/03/16.
 */
public interface MessageProvider {

    public Map<Long, Message> getMessages(RequestData data);

}
