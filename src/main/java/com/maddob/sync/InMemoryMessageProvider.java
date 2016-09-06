package com.maddob.sync;

import com.maddob.sync.message.InMemorySequentialMessageStorage;
import com.maddob.sync.message.MessageConsumer;
import com.maddob.sync.message.MessageProvider;
import com.maddob.sync.protocol.RequestData;
import com.maddob.sync.user.InMemoryUserManager;
import io.vertx.core.eventbus.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Simple in-memory provider for messages
 *
 * This class'es purpose is just to test
 * the idea behind the sync concept
 *
 * Created by martindobrev on 15/03/16.
 */
public class InMemoryMessageProvider implements MessageProvider, MessageConsumer {

    private HashMap<String, InMemorySequentialMessageStorage> messageStorage;

    private InMemoryUserManager userManager;

    /**
     * Constructor
     *
     * just initialize the storage data structures
     *
     */
    public InMemoryMessageProvider() {

        messageStorage = new HashMap<>();
        userManager = new InMemoryUserManager();
    }

    public Map<Long, Message> getMessages(RequestData data) {

        Map<Long, Message> messages = new HashMap<>();

        if (null == messageStorage.get(data.userId)) {
            messageStorage.put(data.userId, new InMemorySequentialMessageStorage());
        } else {

            InMemorySequentialMessageStorage storage = messageStorage.get(data.userId);
            messages = storage.getItemsForRequestedItemSequence(data.sequence);
        }

        return messages;
    }

    public long processMessage(Message message) {
        String sender = null;

        if (null != message.headers()) {
            sender = message.headers().get("Sender");
        }

        InMemorySequentialMessageStorage senderStorage = messageStorage.get(sender);

        if (null == senderStorage) {
            messageStorage.put(sender, new InMemorySequentialMessageStorage());
        }

        return messageStorage.get(sender).addItem(message);
    }
}