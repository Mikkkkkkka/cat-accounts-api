package com.mikkkkkkka.gateway.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikkkkkkka.common.model.CatColor;
import com.mikkkkkkka.common.model.dto.CatDto;
import com.mikkkkkkka.common.model.filter.CatFilter;
import com.mikkkkkkka.gateway.service.CatClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cats")
public class CatController {

    private final CatClientService catService;

    @Autowired
    public CatController(CatClientService catService) {
        this.catService = catService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCats(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) List<CatColor> colors,
            @RequestParam(required = false) LocalDate birthdayBefore,
            @RequestParam(required = false) LocalDate birthdayAfter
    ) throws JsonProcessingException {
        var catFilter = new CatFilter(
                ownerId,
                colors,
                birthdayAfter,
                birthdayBefore
        );
        return ResponseEntity.ok(catService.getAllCatsFiltered(catFilter, page, size));
    }

    @PostMapping
    public ResponseEntity<?> createCat(@RequestBody CatDto cat) throws JsonProcessingException {
        return ResponseEntity.ok(catService.createCat(cat));
    }

    @GetMapping("/{id}")
    @PostAuthorize("hasAuthority('ROLE_ADMIN') ||" +
            "hasAuthority('ROLE_USER') &&" +
            "@catClientService.ownerOwnsCat(authentication.principal.ownerId, #id)")
    public ResponseEntity<?> getCat(@PathVariable long id) throws JsonProcessingException {
        return ResponseEntity.ok(catService.getCat(id));
    }

    @PutMapping("/{id}")
    @PostAuthorize("hasAuthority('ROLE_ADMIN') ||" +
            "hasAuthority('ROLE_USER') &&" +
            "@catClientService.ownerOwnsCat(authentication.principal.ownerId, #id)")
    public ResponseEntity<?> updateCat(@PathVariable long id, @RequestBody CatDto cat) throws JsonProcessingException {
        return ResponseEntity.ok(catService.updateCat(id, cat));
    }

    @DeleteMapping("/{id}")
    @PostAuthorize("hasAuthority('ROLE_ADMIN') ||" +
            "hasAuthority('ROLE_USER') &&" +
            "@catClientService.ownerOwnsCat(authentication.principal.ownerId, #id)")
    public ResponseEntity<?> deleteCat(@PathVariable long id) throws JsonProcessingException {
        return ResponseEntity.ok(catService.deleteCat(id));
    }

    @PostMapping("/friendships")
    @PostAuthorize("hasAuthority('ROLE_ADMIN') ||" +
            "(hasAuthority('ROLE_USER') &&" +
            "(@catClientService.ownerOwnsCat(authentication.principal.ownerId, #cat1Id)) ||" +
            "@catClientService.ownerOwnsCat(authentication.principal.ownerId, #cat2Id))")
    public ResponseEntity<?> befriend(
            @RequestParam long cat1Id,
            @RequestParam long cat2Id
    ) throws JsonProcessingException {
        return ResponseEntity.ok(catService.befriendCats(cat1Id, cat2Id));
    }

    @DeleteMapping("/friendships")
    @PostAuthorize("hasAuthority('ROLE_ADMIN') ||" +
            "(hasAuthority('ROLE_USER') &&" +
            "(@catClientService.ownerOwnsCat(authentication.principal.ownerId, #cat1Id)) ||" +
            "@catClientService.ownerOwnsCat(authentication.principal.ownerId, #cat2Id))")
    public ResponseEntity<?> unfriend(
            @RequestParam long cat1Id,
            @RequestParam long cat2Id
    ) throws JsonProcessingException {
        return ResponseEntity.ok(catService.unfriendCats(cat1Id, cat2Id));
    }
}