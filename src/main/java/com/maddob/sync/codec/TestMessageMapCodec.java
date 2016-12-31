package com.maddob.sync.codec;

import com.maddob.sync.message.TestMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Codec for encoding/decoding a map of TestMessage objects identified by their sequence numbers
 *
 * Created by martindobrev on 12/30/16.
 */
public class TestMessageMapCodec implements MessageCodec<HashMap<Long, TestMessage>, HashMap<Long, TestMessage>> {

    @Override
    public void encodeToWire(Buffer buffer, HashMap<Long, TestMessage> longTestMessageMap) {
        JsonObject json = new JsonObject();
        longTestMessageMap.forEach((aLong, testMessage) -> {
            JsonObject jsonMessage = new JsonObject();
            jsonMessage.put("body", testMessage.body());
            testMessage.headers().forEach(stringStringEntry -> {
                jsonMessage.put(stringStringEntry.getKey(), stringStringEntry.getValue());
            });
            json.put(aLong.toString(), jsonMessage);
        });

        // Encode json to string and calculate its length
        String jsonToEncode = json.encode();
        int length =  jsonToEncode.getBytes().length;

        // Write data to the buffer
        buffer.appendInt(length);
        buffer.appendString(jsonToEncode);
    }

    @Override
    public HashMap<Long, TestMessage> decodeFromWire(int position, Buffer buffer) {
        // My custom message starting from this *position* of buffer
        int _pos = position;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);
        JsonObject contentJson = new JsonObject(jsonStr);

        // Decode TestMessage map from the JsonObject
        HashMap<Long, TestMessage> messageMap = new HashMap<>();
        contentJson.fieldNames().forEach(field -> {
            JsonObject jsonMessage = contentJson.getJsonObject(field);
            String body = jsonMessage.getString("body");
            String receiver = jsonMessage.getString("receiver");
            String sender = jsonMessage.getString("sender");
            TestMessage message = new TestMessage(body, sender, receiver);
            Long messageId = Long.valueOf(field);
            messageMap.put(messageId, message);
        });

        return messageMap;
    }

    @Override
    public HashMap<Long, TestMessage> transform(HashMap<Long, TestMessage> longTestMessageMap) {
        return longTestMessageMap;
    }

    @Override
    public String name() {
        // Each codec must have a unique name.
        // This is used to identify a codec when sending a message and for unregistering codecs
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        // Always -1
        return -1;
    }
}
