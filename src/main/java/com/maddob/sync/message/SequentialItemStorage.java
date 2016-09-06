package com.maddob.sync.message;

import com.maddob.sync.protocol.ItemSequence;

import java.util.List;
import java.util.Map;

/**
 * Created by martindobrev on 05/09/16.
 */
public interface SequentialItemStorage<T> {
    public long getStorageItemsCount();
    public long getCurrentItemIndex();
    public List<T> getCompleteStorage();
    public long addItem(T item);
    public T getStorageItemByIndex(long index);
    public Map<Long, T> getItemsForRequestedItemSequence(ItemSequence sequence);
}
