package com.maddob.sync.message;

import com.maddob.sync.protocol.ItemSequence;
import io.vertx.core.eventbus.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by martindobrev on 05/09/16.
 */
public class InMemorySequentialMessageStorage implements SequentialItemStorage<Message> {

    private List<Message> messages;

    public InMemorySequentialMessageStorage() {
        messages = new ArrayList<>();
    }

    @Override
    public long getStorageItemsCount() {
        return messages.size();
    }

    @Override
    public long getCurrentItemIndex() {
        return messages.size() + 1;
    }

    @Override
    public List<Message> getCompleteStorage() {
        return messages;
    }

    @Override
    public long addItem(Message item) {
        long nextItem = getCurrentItemIndex();
        messages.add(item);
        return nextItem;
    }

    @Override
    public Message getStorageItemByIndex(long index) {
        return messages.get((int) index - 1);
    }

    @Override
    public Map<Long, Message> getItemsForRequestedItemSequence(ItemSequence sequence) {

        Map<Long, Message> items = new HashMap<>();
        if (null != sequence) {
            List<Long> presentIds = sequence.getSequence();

            if (null != presentIds) {

                for (int i = 0; i < messages.size(); i++) {
                    if (false == presentIds.contains(new Long(i + 1))) {
                        items.put((long) (i + 1), messages.get(i));
                    }
                }
            }
        }

        return items;
    }
}
