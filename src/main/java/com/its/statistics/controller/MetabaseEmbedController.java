package com.its.statistics.controller;

import com.its.statistics.service.MetabaseEmbedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/embed")
public class MetabaseEmbedController {

    @Autowired
    private MetabaseEmbedService embedService;

    @GetMapping("/dashboard/{id}")
    public ResponseEntity<Map<String, String>> getEmbedUrl(@PathVariable long id) {
        try {
            String url = embedService.generateDashboardEmbedUrl(id, Map.of(), 30);
            return ResponseEntity.ok(Map.of("url", url));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}
