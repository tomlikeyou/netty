package com.herry.server.service;

public interface UserService {
    /**
     * 登录接口，成功返回true,失败返回false
     * @param userName
     * @param password
     * @return
     */
    boolean login(String userName,String password);
}
