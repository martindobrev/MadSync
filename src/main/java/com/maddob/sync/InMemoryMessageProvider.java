package com.maddob.sync;

import com.maddob.sync.protocol.RequestData;
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
public class InMemoryMessageProvider implements MessageProvider, MessageConsumer {


    /** storage of messages without sender */
    private Map<Long, Message> generalMessages;

    private Long nextGeneralIndex = 1l;

    /** storage of messages with available sender id */
    private Map<String, Map<Long, Message>> identifiableMessages;


    private Map<String, Long> nextUserIndexes;

    /**
     * Constructor
     *
     * just initialize the storage data structures
     *
     */
    public InMemoryMessageProvider() {
        generalMessages = new HashMap<>();
        identifiableMessages = new HashMap<>();
        nextUserIndexes = new HashMap<>();
    }

    public Map<Long, Message> getMessages(RequestData data) {

        Map<Long, Message> messages;

        if (data.userId == null) {
            messages = generalMessages;
        } else {
            messages = identifiableMessages.get(data.userId);
        }

        if (null != data.sequence) {
            for (Long sequenceNumber : data.sequence.getSequence()) {
                if (messages.containsKey(sequenceNumber)) {
                    messages.remove(sequenceNumber);
                }
            }
        }

        return messages;
    }

    public long processMessage(Message message) {
        String sender = null;
        if (null != message.headers()) {
            sender = message.headers().get("Sender");

            if (message.headers().contains("Receiver")) {
                // identifiableMessage
                // TODO: CONTINUE WORK HERE
                // - add messages to all receivers also
                // - maybe write a separate functions for handling message addition to a user
            }
        }

        if (null == sender) {
            long newIndex = nextGeneralIndex++;
            generalMessages.put(newIndex, message);
            return newIndex;
        } else {
            if (false == identifiableMessages.containsKey(sender)) {
                identifiableMessages.put(sender, new HashMap<>());
                nextUserIndexes.put(sender, 1l);
            }

            long nextIndex = nextUserIndexes.get(sender);
            identifiableMessages.get(sender).put(nextUserIndexes.get(sender), message);
            nextUserIndexes.put(sender, nextIndex + 1);
            return nextIndex;
        }
    }
}