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

    public boolean sequenceAvailable();
}
