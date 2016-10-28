package com.maddob.sync;

import com.maddob.sync.message.InMemorySequentialMessageStorage;
import com.maddob.sync.message.MadSyncMessageConsumer;
import com.maddob.sync.message.MessageProvider;
import com.maddob.sync.protocol.RequestData;
import com.maddob.sync.user.InMemoryUserManager;
import io.vertx.core.eventbus.Message;

import java.util.HashMap;
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
public class InMemoryMadSyncMessageProvider implements MessageProvider, MadSyncMessageConsumer {

    private HashMap<String, InMemorySequentialMessageStorage> messageStorage;

    private InMemoryUserManager userManager;

    /**
     * Constructor
     *
     * just initialize the storage data structures
     *
     */
    public InMemoryMadSyncMessageProvider() {

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
        String receiver = null;


        if (null != message.headers()) {
            sender = message.headers().get("sender");
            receiver = message.headers().get("receiver");
        }

        InMemorySequentialMessageStorage senderStorage = messageStorage.get(sender);
        if (null == senderStorage) {
            messageStorage.put(sender, new InMemorySequentialMessageStorage());
        }

        if (null != receiver) {
            InMemorySequentialMessageStorage receiverStorage = messageStorage.get(receiver);
            if (null == receiverStorage) {
                messageStorage.put(receiver, new InMemorySequentialMessageStorage());
            }
            messageStorage.get(receiver).addItem(message);
        }



        return messageStorage.get(sender).addItem(message);
    }
}