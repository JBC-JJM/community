package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HostHolder {

    //虽然是bean了，不会有新建HostHolder的事情导致ThreadLocal也新建，导致一个线程每次访问ThreadLocal都不是同一个
    //这里保险的使用了static，不会出现上面的数据不完整
    private static ThreadLocal local = new ThreadLocal<User>();

    public void setUser(User user) {
        local.set(user);
    }

    public User getUser() {
        return (User) local.get();
    }

    public void clean() {
        local.remove();
    }
}
