package com.goti.produto.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goti.produto.model.LiveStream;
import com.goti.produto.service.LiveStreamService;

@RestController
@RequestMapping("/lives")
public class LiveStreamController {

    private final LiveStreamService liveStreamService;

    public LiveStreamController(LiveStreamService liveStreamService) {
        this.liveStreamService = liveStreamService;
    }

    @PostMapping
    public ResponseEntity<LiveStream> cadastrar(@RequestBody LiveStream live) {
        return ResponseEntity.status(HttpStatus.CREATED).body(liveStreamService.salvar(live));
    }

    @GetMapping
    public ResponseEntity<List<LiveStream>> listar() {
        return ResponseEntity.ok(liveStreamService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LiveStream> buscarPorId(@PathVariable String id) {
        return liveStreamService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}