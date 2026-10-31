package com.Projects.AirBnbApp.util;

import com.Projects.AirBnbApp.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class AppUtils {
    public static User getCurrUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}
