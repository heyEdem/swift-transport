package com.example.swifttransport.controller;

import com.example.swifttransport.dto.request.AssignVehicleRequest;
import com.example.swifttransport.dto.response.AssignmentListResponse;
import com.example.swifttransport.dto.response.AssignmentResponse;
import com.example.swifttransport.service.VehicleAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/assignments")
@RequiredArgsConstructor
public class VehicleAssignmentController {

    private final VehicleAssignmentService assignmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ResponseEntity<AssignmentResponse> assignVehicle(@Valid @RequestBody AssignVehicleRequest request) {
        AssignmentResponse created = assignmentService.assignVehicle(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/driver/{driverId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ResponseEntity<AssignmentResponse> unassignVehicle(@PathVariable Long driverId) {
        return ResponseEntity.ok(assignmentService.unassignVehicle(driverId));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATIONS')")
    public ResponseEntity<AssignmentListResponse> getAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam(required = false) Long driverId,
            @RequestParam(required = false) Long vehicleId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(assignmentService.getAssignments(pageable, activeOnly, driverId, vehicleId));
    }
}
