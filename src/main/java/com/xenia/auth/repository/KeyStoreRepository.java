package com.xenia.auth.repository;

import com.xenia.auth.model.KeyEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface KeyStoreRepository extends MongoRepository<KeyEntry, String> {
    Optional<KeyEntry> findByKey(String key);
}