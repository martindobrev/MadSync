package com.maddob.sync;

import com.maddob.sync.codec.TestMessageCodec;
import com.maddob.sync.codec.TestMessageMapCodec;
import com.maddob.sync.message.InMemorySequentialMessageStorage;
import com.maddob.sync.message.MadSyncMessageConsumer;
import com.maddob.sync.message.MessageProvider;
import com.maddob.sync.message.TestMessage;
import com.maddob.sync.protocol.RequestData;
import com.maddob.sync.protocol.SimpleItemSequence;
import com.maddob.sync.user.InMemoryUserManager;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

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
public class InMemoryMadSyncMessageProvider extends AbstractVerticle implements MessageProvider, MadSyncMessageConsumer {

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

    @Override
    public void start() throws Exception {
        super.start();
        MessageConsumer<JsonObject> jsonMessageConsumer = getVertx().eventBus().consumer("message");
        jsonMessageConsumer.handler(jsonMessage -> {
            String text = jsonMessage.body().getString("text");
            String sender = jsonMessage.body().getString("sender");
            String receiver = jsonMessage.body().getString("receiver");

            TestMessage msg = new TestMessage(text, sender, receiver);
            long messageSequenceNumber = processMessage(msg);

            JsonObject obj = new JsonObject();
            obj.put("messageSequenceNumber", messageSequenceNumber);
            getVertx().eventBus().publish("message.send." + sender, obj);

            System.out.println("I have received a message from: " + jsonMessage.toString());
        });

        MessageConsumer<JsonObject>  jsonDataRequestConsumer = getVertx().eventBus().consumer("data");
        jsonDataRequestConsumer.handler(jsonMessage -> {
            System.out.println("Data sync was requested -> " + jsonMessage.toString());
            RequestData data = new RequestData();
            data.userId = jsonMessage.body().getString("userId");
            data.maxSequenceNumber = jsonMessage.body().getInteger("maxSequenceNumber");
            data.sequence = new SimpleItemSequence();       //TODO: parse the actual instance
            Map<Long, Message> newMessages = getMessages(data);

            System.out.println("Messages to be returned! -> " + newMessages.keySet().toString());

            DeliveryOptions options = new DeliveryOptions();
            options.setCodecName(TestMessageCodec.class.getSimpleName());


            JsonObject json = new JsonObject();
            if (false == newMessages.isEmpty()) {
                newMessages.forEach((aLong, testMessage) -> {
                    JsonObject message = new JsonObject();
                    message.put("text", testMessage.body());
                    testMessage.headers().forEach(stringStringEntry -> {
                        message.put(stringStringEntry.getKey(), stringStringEntry.getValue());
                    });
                    json.put(aLong.toString(), message);
                });
            }
            getVertx().eventBus().publish("data." + data.userId, json);
        });
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

            JsonObject obj = new JsonObject();
            obj.put("text", message.body());
            obj.put("sender", sender);
            obj.put("receiver", receiver);

            getVertx().eventBus().publish("message.receive." + receiver, obj);
        }

        return messageStorage.get(sender).addItem(message);
    }
}