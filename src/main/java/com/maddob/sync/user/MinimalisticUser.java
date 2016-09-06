package com.maddob.sync.user;

/**
 * Created by martindobrev on 05/09/16.
 */
public class MinimalisticUser implements User {

    public String id;

    @Override
    public String getId() {
        return id;
    }
}
