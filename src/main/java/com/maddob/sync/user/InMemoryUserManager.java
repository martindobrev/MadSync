package com.maddob.sync.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in memory user manager
 *
 * Stores the users of the specified type (must im
 *
 * Created by martindobrev on 05/09/16.
 */
public class InMemoryUserManager implements UserManager<MinimalisticUser> {

    private Map<String, MinimalisticUser> users;

    public InMemoryUserManager() {
        users = new HashMap<String, MinimalisticUser>();
    }

    @Override
    public MinimalisticUser getUserById(String id) {
        return users.get(id);
    }

    @Override
    public boolean editUser(MinimalisticUser user) {
        // Useless because each minimalistic user has only an id here
        return false;
    }

    @Override
    public boolean deleteUser(MinimalisticUser user) {
        if (null == user || null == user.getId() || null == users) {
            return false;
        }
        return null == users.remove(user.getId());
    }

    @Override
    public boolean addUser(MinimalisticUser user) {
        if (null == user || null == user.getId() || null == users) {
            return false;
        }
        if (null == getUserById(user.getId())) {
            return false;
        }
        return null == users.put(user.getId(), user);
    }

    public MinimalisticUser createUserWithId(String id) {
        MinimalisticUser user = new MinimalisticUser();
        user.id = id;
        return user;
    }
}
