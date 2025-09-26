package com.xenia.auth.controller;

import com.xenia.auth.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xenia.auth.model.Response;
import com.xenia.auth.service.UserService;
import com.xenia.auth.util.XuidUtil;

import jakarta.servlet.http.HttpServletRequest;

import com.xenia.auth.security.JwtUtil;
import com.xenia.auth.security.KeyStore;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final KeyStore keyStore;

    public AuthController(UserService userService, JwtUtil jwtUtil, KeyStore keyStore) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.keyStore = keyStore;
    }

    @PostMapping("/register")
    public Response<User> register(
        @RequestParam("username") String username,
        @RequestParam("password") String password
    ) {
    	if (username == null || username.isEmpty() || username.isBlank() || password == null || password.isBlank() || password.isEmpty()) {
    		return Response.error("Missing required fields.");
    	}
    	if (username.length() > 25 || username.length() < 4) {
    		return Response.error("Username must be 4 characters min and 25 max");
    	}
    	if (password.length() < 4 || password.length() > 100) {
    		return Response.error("Password must be 4 characters min and 100 max");
    	}
    	
        try {
            User user = userService.register(username, password, true);
            return Response.success(user);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Response<Map<String, String>> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) {
    	if (username == null || username.isEmpty() || username.isBlank() || password == null || password.isBlank() || password.isEmpty()) {
    		return Response.error("Missing required fields.");
    	}
    	if (username.length() > 25 || username.length() < 4) {
    		return Response.error("Username must be 4 characters min and 25 max");
    	}
    	if (password.length() < 4 || password.length() > 100) {
    		return Response.error("Password must be 4 characters min and 100 max: " + password.length());
    	}
    	
        try {
            User user = userService.authenticate(username, password);
            String token = jwtUtil.generateToken(user, request.getRemoteAddr().toString());
            return Response.success(Map.of("token", token, "username", user.get("username").get("username").asText()));
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
    
    @PostMapping("/session")
    public Response<Map<String, String>> tokenLogin(@RequestParam("token") String hash, HttpServletRequest request) {
        if (hash == null || hash.isBlank() || hash.isEmpty()) {
            return Response.error("Missing required fields.");
        }
        try {
        	String token = keyStore.retrieve(hash);
            // Validate token and extract username
            String xuid = jwtUtil.extractClaims(token).get("offlineXuid").toString();
            String ip = jwtUtil.extractClaims(token).get("ip").toString();
            String username = jwtUtil.extractClaims(token).getSubject();
            if (xuid == null || xuid.isBlank() || username == null || username.isBlank()) {
                return Response.error("Invalid token.");
            }
            User user = userService.getUserByOfflineXuid(xuid);
            if (user == null) {
                return Response.error("User not found.");
            }
            if (!request.getRemoteAddr().toString().equalsIgnoreCase(ip) || !username.equalsIgnoreCase(user.get("username").get("username").asText())) {
            	// Revoke leaked session token
            	keyStore.remove(token);
            	return Response.error("Unauthorized");
            }
            return Response.success(Map.of("token", hash, "username", username));
        } catch (Exception e) {
            return Response.error("Invalid or expired token.");
        }
    }

    @GetMapping("/user/{xuid}")
    public Response<ObjectNode> userByOnlineXuid(@PathVariable("xuid") String xuid) {
    	if (xuid == null || xuid.isEmpty() || xuid.isBlank()) {
    		return Response.error("Missing required fields.");
    	}
    	if (!XuidUtil.validXuid(xuid)) return Response.error("Invalid Xuid");
    	
        try {
            User user = XuidUtil.isOnlineXuid(xuid) ? userService.getUserByOnlineXuid(xuid) : userService.getUserByOfflineXuid(xuid);
            return Response.success(user.get("username", "friends", "offline_xuid", "friendsPrivate"));
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }
    
    @GetMapping("/user/{xuid}/friends")
    public Response<ObjectNode> getFriends(
        @PathVariable("xuid") String xuid,
        @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader
    ) {
    	if (xuid == null || xuid.isEmpty() || xuid.isBlank()) {
    		return Response.error("Missing required fields.");
    	}
        String requesterUsername = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = keyStore.retrieve(authHeader.substring(7));
            try {
                requesterUsername = jwtUtil.extractClaims(token).getSubject();
            } catch (Exception ignored) {}
        }
        return userService.getFriendsList(xuid, requesterUsername);
    }
}