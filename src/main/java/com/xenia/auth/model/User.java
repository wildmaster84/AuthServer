package com.xenia.auth.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String offlineXuid;
    private String onlineXuid; // Optional
    private String username;
    private String passwordHash;
    private List<String> friends; // online XUIDs
    private boolean friendsPrivate; // if true, only owner can view

    // Getters and Setters
    public void setOfflineXuid(String offlineXuid) { this.offlineXuid = offlineXuid; }
    public void setOnlineXuid(String onlineXuid) { this.onlineXuid = onlineXuid; }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public List<String> getFriends() { return friends; }
    public void setFriends(List<String> friends) { this.friends = friends; }
    public void setFriendsPrivate(boolean friendsPrivate) { this.friendsPrivate = friendsPrivate; }
    
    public ObjectNode get(String... fields) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        for (String field : fields) {
            switch (field) {
                case "offline_xuid":
                    node.put("offline_xuid", this.offlineXuid);
                    break;
                case "online_xuid":
                    node.put("online_xuid", this.onlineXuid);
                    break;
                case "username":
                    node.put("username", this.username);
                    break;
                case "friends":
                    node.putPOJO("friends", this.friends);
                    break;
                case "friendsPrivate":
                    node.put("friendsPrivate", this.friendsPrivate);
                    break;
                case "passwordHash":
                    node.put("passwordHash", this.passwordHash);
                    break;
                // ... add other fields as needed
            }
        }

        return node;
    }
}