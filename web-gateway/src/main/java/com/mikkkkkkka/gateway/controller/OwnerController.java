package com.mikkkkkkka.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikkkkkkka.common.model.dto.OwnerDto;
import com.mikkkkkkka.common.model.filter.OwnerFilter;
import com.mikkkkkkka.gateway.service.OwnerClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/owners")
public class OwnerController {

    private final OwnerClientService ownerService;

    @Autowired
    public OwnerController(OwnerClientService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllOwners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate birthdayAfter,
            @RequestParam(required = false) LocalDate birthdayBefore
    ) throws JsonProcessingException {
        var ownerFilter = new OwnerFilter(
                birthdayAfter,
                birthdayBefore
        );
        return ResponseEntity.ok(ownerService.getAllOwnersFiltered(ownerFilter, page, size));
    }

    @PostMapping
    public ResponseEntity<?> createOwner(@RequestBody OwnerDto owner) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.createOwner(owner));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'OWNER_' + #id)")
    public ResponseEntity<?> getOwner(@PathVariable long id) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.getOwner(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'OWNER_' + #id)")
    public ResponseEntity<?> updateOwner(@PathVariable long id, @RequestBody OwnerDto owner) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.updateOwner(id, owner));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'OWNER_' + #id)")
    public ResponseEntity<?> deleteOwner(@PathVariable long id) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.deleteOwner(id));
    }

    @PostMapping("/ownerships")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'OWNER_' + #ownerId)")
    public ResponseEntity<?> addCatToOwner(
            @RequestParam long ownerId,
            @RequestParam long catId
    ) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.addCatToOwner(ownerId, catId));
    }

    @DeleteMapping("/ownerships")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'OWNER_' + #ownerId)")
    public ResponseEntity<?> removeCatFromOwner(
            @RequestParam long ownerId,
            @RequestParam long catId
    ) throws JsonProcessingException {
        return ResponseEntity.ok(ownerService.removeCatFromOwner(ownerId, catId));
    }
}