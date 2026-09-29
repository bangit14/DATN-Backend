package com.backend.candidateservice.matching.controller;

import com.backend.candidateservice.matching.dto.ingest.CvNormUpdatedEvent;
import com.backend.candidateservice.matching.dto.ingest.JobNormUpdatedEvent;
import com.backend.candidateservice.matching.service.NormSnapshotIngestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/events")
@RequiredArgsConstructor
public class InternalEventController {

    private final NormSnapshotIngestService ingest;

    @PostMapping("/cv-norm-updated")
    public ResponseEntity<Void> onCvNormUpdated(@RequestBody CvNormUpdatedEvent event) {
        ingest.upsertCv(event);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/job-norm-updated")
    public ResponseEntity<Void> onJobNormUpdated(@RequestBody JobNormUpdatedEvent event) {
        ingest.upsertJob(event);
        return ResponseEntity.ok().build();
    }
}
