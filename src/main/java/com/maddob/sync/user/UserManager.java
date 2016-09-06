package com.maddob.sync.user;

/**
 * Created by martindobrev on 05/09/16.
 */
public interface UserManager<T extends User> {
    public T getUserById(String userId);
    public boolean editUser(T user);
    public boolean deleteUser(T user);
    public boolean addUser(T user);
}
