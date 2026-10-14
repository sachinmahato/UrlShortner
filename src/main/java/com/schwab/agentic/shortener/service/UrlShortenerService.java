package com.schwab.agentic.shortener.service;

import com.schwab.agentic.shortener.model.UrlMapping;
import com.schwab.agentic.shortener.repository.UrlRepository;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class UrlShortenerService {
    private final UrlRepository urlRepository;

    public UrlShortenerService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlMapping shortenUrl(String originalUrl) {
        String shortCode = UUID.randomUUID().toString().substring(0, 6);
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortCode(shortCode);
        return urlRepository.save(mapping);
    }

    public String resolveUrl(String shortCode) {
        UrlMapping mapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
        mapping.setClickCount(mapping.getClickCount() + 1);
        urlRepository.save(mapping);
        return mapping.getOriginalUrl();
    }
}