package me.ifmo.backend.controllers;

import lombok.RequiredArgsConstructor;
import me.ifmo.backend.outbox.OutboxProcessor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/outbox")
public class OutboxController {

    private final OutboxProcessor outboxProcessor;

    @PostMapping("/process")
    public OutboxProcessor.ProcessResult processOnce() {
        return outboxProcessor.processOnce();
    }
}