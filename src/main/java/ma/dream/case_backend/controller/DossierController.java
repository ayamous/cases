package ma.dream.case_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.dream.case_backend.dto.DossierDto;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.model.Dossier;
import ma.dream.case_backend.service.DossierService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cases")
@AllArgsConstructor
@Slf4j
public class DossierController {

    private final DossierService dossierService;

    @PostMapping("/")
    public ResponseEntity<Dossier> createCase(@RequestBody DossierDto dossierDto) {
        try {
            Dossier createdCase = dossierService.createCase(dossierDto);
            return new ResponseEntity<>(createdCase, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Retrieve all cases", description = "Récupère la liste de tous les cases")
    @GetMapping("/")
    public ResponseEntity<Page<DossierDto>> getAllCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) final String searchByDescription,
            @RequestParam(required = false) final String searchTitle
    ) {
        Page<DossierDto> dossiers = dossierService.getAllCases(page, size, searchByDescription, searchTitle);
        return ResponseEntity.ok(dossiers);
    }

    @Operation(summary = "Update case details", description = "Récupère les détails d'un case par ID")
    @PutMapping("/{id}")
    public ResponseEntity<DossierDto> updateCase(@PathVariable Long id, @RequestBody DossierDto dossierDto) throws TechnicalException {
        log.info("Update case: {}", id);
        DossierDto updatedCase = dossierService.updateCase(id, dossierDto);
        return ResponseEntity.ok(updatedCase);
    }

    @Operation(summary = "Retrieve case by ID", description = "Récupère les détails d'un case par ID")
    @GetMapping("/{id}")
    public ResponseEntity<DossierDto> getCaseById(@PathVariable Long id) throws TechnicalException {
        log.info("get case by id: {}", id);
        return ResponseEntity.ok(dossierService.getCaseById(id));
    }

    @Operation(summary = "Delete a case", description = "Supprime un case par ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        log.info("delete case by id: {}", id);
        try {
            dossierService.deleteCase(id);
            return ResponseEntity.noContent().build();
        } catch (TechnicalException e) {
            log.error("Error deleting case: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
