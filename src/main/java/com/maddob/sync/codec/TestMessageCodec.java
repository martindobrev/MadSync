package com.maddob.sync.codec;

import com.maddob.sync.message.TestMessage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.JsonObject;

/**
 * Created by martindobrev on 12/30/16.
 */
public class TestMessageCodec implements MessageCodec<TestMessage, TestMessage> {

    @Override
    public void encodeToWire(Buffer buffer, TestMessage testMessage) {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.put("body", testMessage.body());

        testMessage.headers().forEach(stringStringEntry -> {
            jsonMessage.put(stringStringEntry.getKey(), stringStringEntry.getValue());
        });

        String jsonToEncode = jsonMessage.encode();
        int length = jsonToEncode.getBytes().length;

        buffer.appendInt(length);
        buffer.appendString(jsonToEncode);
    }

    @Override
    public TestMessage decodeFromWire(int position, Buffer buffer) {
        int _pos = position;

        // Length of JSON
        int length = buffer.getInt(_pos);

        // Get JSON string by it`s length
        // Jump 4 because getInt() == 4 bytes
        String jsonStr = buffer.getString(_pos+=4, _pos+=length);
        JsonObject jsonMessage = new JsonObject(jsonStr);
        String body = jsonMessage.getString("body");
        String receiver = jsonMessage.getString("receiver");
        String sender = jsonMessage.getString("sender");
        return new TestMessage(body, sender, receiver);
    }

    @Override
    public TestMessage transform(TestMessage testMessage) {
        return testMessage;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        // Always -1
        return -1;
    }
}
