package com.maddob.sync.user;

/**
 * Simple interface for menagement of users
 * Just for testing purposes
 * TODO: Look at some existing solutions in the future - shiro for example
 *
 * Created by martindobrev on 05/09/16.
 */
interface UserManager<T extends User> {
    public T getUserById(String userId);
    public boolean editUser(T user);
    public boolean deleteUser(T user);
    public boolean addUser(T user);
}
