package com.maddob.test.sync;

import com.maddob.sync.InMemoryMadSyncMessageProvider;
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

    private InMemoryMadSyncMessageProvider provider;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        provider = new InMemoryMadSyncMessageProvider();
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


    /**
     * Test that requesting general messages with no user id set
     * returns all available messages
     */
    public void testGetSingleMessageNoSender() {
        RequestData data = new RequestData();
        data.maxSequenceNumber = 0;
        data.sequence = new SimpleItemSequence();

        Map<Long, Message> messages = provider.getMessages(data);

        assertNotNull("Returned message list shall not be null", messages.values());
        assertEquals("There shall be 2 messages available", 2, messages.values().size());
    }

    /**
     * Test that requesting general messages with some messages available returns only the
     * newest messages
     */
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

    /**
     * Test that messages are also stored in the receiver's storage
     * and identified by the next sequence id number
     */
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

    /**
     * Test that the receiver gets only when requesting data with some messages
     * in its storage
     */
    public void testReceiverCorrectlyGetsNewMessagesWhenSomeMessagesAlreadyReceived() {
        String testSenderId = "TEST_SENDER";
        String testReceiverId = "TEST_RECEIVER";

        List<Long> availableItems = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            if (i < 6) {
                availableItems.add((long) i);
            }
            provider.processMessage(new TestMessage("message " + i, testSenderId, testReceiverId));
        }

        SimpleItemSequence alternatingSequence= new SimpleItemSequence(true, availableItems);
        RequestData data = new RequestData();
        data.maxSequenceNumber = 5;
        data.userId = testReceiverId;
        data.sequence = alternatingSequence;

        Map<Long, Message> receivedMessages = provider.getMessages(data);

        for (int i = 1; i < 11; i++) {
            if (i < 6) {
                assertFalse("Received messages shall NOT contain the message with id: " + i, receivedMessages.containsKey(new Long(i)));
            } else {
                assertTrue("Received messages shall contain the message with id: " + i, receivedMessages.containsKey(new Long(i)));
            }
        }
    }

    /**
     * Test that a user receives only the messages he does not have when requesting
     * data with some older messages not available
     */
    public void testReceiverCorrectlyGetsMixedOldAndNewMessagesHeDoesNotHave() {
        String testSenderId = "TEST_SENDER";
        String testReceiverId = "TEST_RECEIVER";

        List<Long> availableItems = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            if (i != 4) {
                availableItems.add((long) i);
            }
            provider.processMessage(new TestMessage("message " + i, testSenderId, testReceiverId));
        }

        SimpleItemSequence alternatingSequence= new SimpleItemSequence(true, availableItems);
        RequestData data = new RequestData();
        data.maxSequenceNumber = 10;
        data.userId = testReceiverId;
        data.sequence = alternatingSequence;

        Map<Long, Message> receivedMessages = provider.getMessages(data);

        for (int i = 1; i < 11; i++) {
            if (i == 4) {
                assertTrue("Received messages shall contain the message with id: " + i, receivedMessages.containsKey(new Long(i)));
            } else {
                assertFalse("Received messages shall NOT contain the message with id: " + i, receivedMessages.containsKey(new Long(i)));
            }
        }
    }

    /**
     * Test that the sequencing mechanism works correctly for multiple senders and a single receiver
     *
     * When multiple senders send messages to a single receiver, the sequences of the messages
     * between sender and receiver get mixed
     *
     * 1. SENDER 1 -> {seq: 1, message 1 } ----> {seq: 1, message 1} RECEIVER
     * 2. SENDER 2 -> {seq: 1, message 2 } ----> {seq: 2, message 2} RECEIVER
     * 3. SENDER 1 -> {seq: 2, message 3 } ----> {seq: 3, message 3} RECEIVER
     * 4. SENDER 2 -> {seq: 2, message 4 } ----> {seq: 4, message 4} RECEIVER
     */
    public void testCorrectMessageSequenceIdentificationForMultipleSendersAndSingleReceiver() {
        String senderOne = "TEST_SENDER_1";
        String senderTwo = "TEST_SENDER_2";
        String receiver  = "TEST_RECEIVER";

        provider.processMessage(new TestMessage("message 1-1", senderOne, receiver));
        provider.processMessage(new TestMessage("message 2-1", senderTwo, receiver));
        provider.processMessage(new TestMessage("message 1-2", senderOne, receiver));
        provider.processMessage(new TestMessage("message 2-2", senderTwo, receiver));

        SimpleItemSequence seq = new SimpleItemSequence();
        RequestData data = new RequestData();
        data.maxSequenceNumber = 0;
        data.userId = receiver;
        data.sequence = seq;

        Map<Long, Message> messages = provider.getMessages(data);

        assertEquals("4 messages shall be returned when an empty receiver is requesting data", 4, messages.size());
        assertEquals("Message with sequence id 1 shall be 'message 1-1', but was: " + messages.get(1l).body(), "message 1-1", messages.get(1l).body());
        assertEquals("Message with sequence id 2 shall be 'message 2-1', but was: " + messages.get(2l).body(), "message 2-1", messages.get(2l).body());
        assertEquals("Message with sequence id 3 shall be 'message 1-2', but was: " + messages.get(3l).body(), "message 1-2", messages.get(3l).body());
        assertEquals("Message with sequence id 4 shall be 'message 2-2', but was: " + messages.get(4l).body(), "message 2-2", messages.get(4l).body());



    }
}
