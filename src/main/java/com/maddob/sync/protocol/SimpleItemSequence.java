package com.maddob.sync.protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * SimpleItemSequence
 *
 * Stores the available item ids in a list,
 * the available flag just
 *
 * Created by martindobrev on 04/03/16.
 */
public class SimpleItemSequence implements ItemSequence {
    private List<Long> sequence;
    private boolean available;

    public SimpleItemSequence(boolean available, List<Long> sequence) {
        this.available = available;
        this.sequence = sequence;
    }

    public SimpleItemSequence() {
        this(true, new ArrayList<>());
    }

    public List<Long> getSequence() {
        return sequence;
    }

    @Override
    public boolean sequenceAvailable() {
        return false;
    }
}
