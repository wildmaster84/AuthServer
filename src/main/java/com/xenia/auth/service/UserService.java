package com.xenia.auth.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xenia.auth.model.Response;
import com.xenia.auth.model.User;
import com.xenia.auth.repository.UserRepository;
import com.xenia.auth.util.XuidUtil;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User register(String username, String password, boolean assignOnlineXuid) throws Exception {
        if (userRepo.findByUsername(username).isPresent())
            throw new Exception("Username already exists");

        String offlineXuid;
        do {
            offlineXuid = XuidUtil.generateOfflineXuid();
        } while (userRepo.findByOfflineXuid(offlineXuid).isPresent());

        String onlineXuid = null;
        if (assignOnlineXuid) {
            String oxuid;
            do {
                oxuid = XuidUtil.generateOnlineXuid();
            } while (userRepo.findByOnlineXuid(oxuid).isPresent());
            onlineXuid = oxuid;
        }

        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        u.setOfflineXuid(offlineXuid);
        u.setOnlineXuid(onlineXuid);
        return userRepo.save(u);
    }

    public User authenticate(String username, String password) throws Exception {
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));        
        if (!BCrypt.checkpw(password, user.get("passwordHash").get("passwordHash").asText()))
            throw new Exception("Invalid password");
        return user;
    }

    public User getUserByOfflineXuid(String xuid) throws Exception {
    	String rawXuid = xuid.startsWith("0x") ? xuid : "0x" + xuid;
        return userRepo.findByOfflineXuid(rawXuid)
            .orElseThrow(() -> new Exception("User not found"));
    }

    public User getUserByOnlineXuid(String xuid) throws Exception {
    	String rawXuid = xuid.startsWith("0x") ? xuid : "0x" + xuid;
        return userRepo.findByOnlineXuid(rawXuid)
            .orElseThrow(() -> new Exception("User not found"));
    }
    
    public Response<ObjectNode> getFriendsList(String onlineXuid, String requesterUsername) {
        try {
        	User user = userRepo.findByOnlineXuid(onlineXuid).orElse(userRepo.findByOfflineXuid(onlineXuid).orElseThrow(() -> new Exception("User not found")));
            // If private and not owner, deny
            if (user.get("friendsPrivate").get("friendsPrivate").asBoolean() && !user.get("username").get("username").toString().equals(requesterUsername)) {
                return Response.error("Friends list is private");
            }
            return Response.success(user.get("friends"));
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
}