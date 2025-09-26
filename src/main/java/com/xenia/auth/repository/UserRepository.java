package com.xenia.auth.repository;

import com.xenia.auth.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByOfflineXuid(String offlineXuid);
    Optional<User> findByOnlineXuid(String onlineXuid);
    Optional<User> findByUsername(String username);
}