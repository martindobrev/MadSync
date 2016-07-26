package com.maddob.test.sync;

import com.maddob.sync.InMemoryMessageProvider;
import com.maddob.sync.protocol.RequestData;
import com.maddob.sync.protocol.SimpleItemSequence;
import io.vertx.core.eventbus.Message;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for the simple in-memory MessageProvider
 *
 *
 * The in-memory message provider's purpose is to
 * just test the concept
 *
 *
 * Created by martindobrev on 15/03/16.
 */
public class MessageProviderTest extends TestCase {

    private InMemoryMessageProvider provider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        provider = new InMemoryMessageProvider();
        provider.processMessage(new TestMessage());
        provider.processMessage(new TestMessage());
    }

    /**
     * Test that sending a third general message is stored with the sequence number 3
     * of all general messages
     *
     */
    public void testSingleMessageProcessing() {
        assertEquals("MessageProvider shall return the correct sequence number for the new message", 3, provider.processMessage(new TestMessage()));
    }

    /**
     * Test that sending a new message with some user id is stored
     * with the sequence number of 1 for this user
     */
    public void testSingleMessageNewSender() {
        TestMessage msg = new TestMessage();
        msg.add("sender", "USER");
        assertEquals("MessageProvider shall return the correct sequence number for a new user's message", 1, provider.processMessage(msg));
    }


    public void testGetSingleMessageNoSender() {
        RequestData data = new RequestData();
        data.maxSequenceNumber = 0;
        data.sequence = new SimpleItemSequence();

        Map<Long, Message> messages = provider.getMessages(data);

        assertNotNull("Returned message list shall not be null", messages.values());
        assertEquals("There shall be 2 messages available", 2, messages.values().size());
    }


    public void testGetOnlyNewestMessages() {
        RequestData data = new RequestData();
        data.maxSequenceNumber = 1;
        List<Long> sequence = new ArrayList<>();
        sequence.add(1l);
        data.sequence = new SimpleItemSequence(true, sequence);

        Map<Long, Message> messages = provider.getMessages(data);
        assertNotNull("Returned message list shall not be null", messages.values());
        assertEquals("There shall be 1 message available only", 1, messages.values().size());
    }

    public void testReceiverCorrectlyGetsNewMessage() {
        TestMessage msg = new TestMessage();
        msg.add("sender", "USER");
        msg.add("receiver", "SOME_RECEIVER");
        provider.processMessage(msg);

        RequestData data = new RequestData();
        data.maxSequenceNumber = 0;
        data.sequence = new SimpleItemSequence();
        data.userId = "SOME_RECEIVER";

        Map<Long, Message> messages = provider.getMessages(data);

        assertNotNull("Returned message list shall not be null", messages);
        assertEquals("There shall be 1 message available only", 1, messages.values().size());

    }
}
