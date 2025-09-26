package com.xenia.auth.security;

import com.xenia.auth.model.KeyEntry;
import com.xenia.auth.repository.KeyStoreRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class KeyStore {
    private final KeyStoreRepository keyStoreRepository;
    private static final SecureRandom random = new SecureRandom();
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public KeyStore(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
    }

    private String generateKey(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    public String store(String value) {
        String key;
        do {
            key = generateKey(16); // fixed 16-char key
        } while (keyStoreRepository.findByKey(key).isPresent());
        
        KeyEntry keyEntry = new KeyEntry(key, value);
        keyStoreRepository.save(keyEntry);
        return key;
    }

    public String retrieve(String key) {
        return keyStoreRepository.findByKey(key)
                .map(KeyEntry::getValue)
                .orElse(null);
    }

    public void remove(String key) {
        keyStoreRepository.findByKey(key).ifPresent(keyStoreRepository::delete);
    }
}
