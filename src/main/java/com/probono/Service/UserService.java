package com.probono.Service;

import com.probono.Mapper.UserMapper;
import com.probono.entity.User;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {
    @Autowired
    UserMapper userMapper;

    public void joinUser(User user){

    }
}
