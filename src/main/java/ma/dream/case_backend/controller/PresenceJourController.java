package ma.dream.case_backend.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.dream.case_backend.dto.PresenceJourDto;
import ma.dream.case_backend.dto.PresenceStatutCountDto;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.model.PresenceJour;
import ma.dream.case_backend.service.PresenceJourService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/presences")
@AllArgsConstructor
@Slf4j
public class PresenceJourController {

    private final PresenceJourService presenceJourService;

    @PostMapping("/")
    public ResponseEntity<PresenceJour> createPresenceJour(@RequestBody PresenceJourDto presenceJourDto) {
        try {
            PresenceJour createPresenceJour = presenceJourService.createPresenceJour(presenceJourDto);
            return new ResponseEntity<>(createPresenceJour, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Retrieve all PresenceJour", description = "Récupère la liste de tous les PresenceJour")
    @GetMapping("/")
    public ResponseEntity<Page<PresenceJourDto>> getAllPresenceJour(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) final String searchByNom,
            @RequestParam(required = false) final String searchByStatus,
            @RequestParam(required = false) final String searchByShift,
            @RequestParam(defaultValue = "lastUpdateDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Page<PresenceJourDto> presenceJours = presenceJourService.getAllPresenceJour(page, size, searchByNom, searchByStatus, searchByShift, sortBy, direction);
        return ResponseEntity.ok(presenceJours);
    }

    @Operation(summary = "Update PresenceJour details", description = "Récupère les détails d'un PresenceJour par ID")
    @PutMapping("/{id}")
    public ResponseEntity<PresenceJourDto> updatePresenceJour(@PathVariable Long id, @RequestBody PresenceJourDto presenceJourDto) throws TechnicalException {
        log.info("Update PresenceJour: {}", id);
        PresenceJourDto updatedPresenceJour = presenceJourService.updatePresenceJour(id, presenceJourDto);
        return ResponseEntity.ok(updatedPresenceJour);
    }

    @Operation(summary = "Retrieve PresenceJour by ID", description = "Récupère les détails d'un PresenceJour par ID")
    @GetMapping("/{id}")
    public ResponseEntity<PresenceJourDto> getPresenceJourById(@PathVariable Long id) throws TechnicalException {
        log.info("get PresenceJour by id: {}", id);
        return ResponseEntity.ok(presenceJourService.getPresenceJourById(id));
    }

    @Operation(summary = "Delete a PresenceJour", description = "Supprime un PresenceJour par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePresenceJour(@PathVariable Long id) {
        log.info("delete PresenceJour by id: {}", id);
        try {
            presenceJourService.deletePresenceJour(id);
            return ResponseEntity.noContent().build();
        } catch (TechnicalException e) {
            log.error("Error deleting PresenceJour: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/statuts/today")
    public ResponseEntity<List<PresenceStatutCountDto>> getPresenceStatutsCountToday() {
        List<PresenceStatutCountDto> result = presenceJourService.countPresenceStatutsToday();
        return ResponseEntity.ok(result);
    }

}
