package com.schwab.agentic.shortener.controller;

import com.schwab.agentic.shortener.model.UrlMapping;
import com.schwab.agentic.shortener.service.UrlShortenerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UrlController {
    private final UrlShortenerService service;

    public UrlController(UrlShortenerService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlMapping> shorten(@RequestBody Map<String, String> request) {
        String originalUrl = request.get("originalUrl");
        UrlMapping mapping = service.shortenUrl(originalUrl);
        return ResponseEntity.ok(mapping);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        String originalUrl = service.resolveUrl(shortCode);
        return ResponseEntity.status(302).location(URI.create(originalUrl)).build();
    }
}