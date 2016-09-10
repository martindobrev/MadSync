package com.maddob.sync.protocol;

import java.util.List;

/**
 * Created by martindobrev on 04/03/16.
 */
public interface ItemSequence {

    /**
     * Get sequence ids of items that
     * the requester has or does not have
     *
     * Meaning depends on the sequenceAvailable method
     *
     *
     * @return List sequence numbers that are either present
     *         or currently being requested
     */
    public List<Long> getSequence();

    /**
     * Flag to notify the message provider about the presence of messages
     *
     * If the method returns true, the provided messages are available, otherwise
     * the provided messages are missing
     *
     * @return
     */
    public boolean sequenceAvailable();
}
