package ma.dream.case_backend.controller;

import ma.dream.case_backend.dto.DossierDto;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.model.Dossier;
import ma.dream.case_backend.service.DossierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CaseControllerTest {

    @Mock
    private DossierService dossierService;

    @InjectMocks
    private DossierController dossierController;

    private DossierDto dossierDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dossierDto = new DossierDto();
        dossierDto.setCaseId(1L);
        dossierDto.setTitle("Case Test");
        dossierDto.setDescription("desc Test");
    }

    @Test
    void addCase_ShouldReturnCreatedCase() throws IOException {
        Dossier dossier = new Dossier();
        dossier.setCaseId(1L);
        dossier.setTitle("Case Test");
        dossier.setDescription("desc Test");
        dossier.setCreationDate(LocalDateTime.now());
        dossier.setLastUpdateDate(LocalDateTime.now());

        when(dossierService.createCase(any(DossierDto.class))).thenReturn(dossier);

        ResponseEntity<Dossier> response = dossierController.createCase(dossierDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dossier, response.getBody());
    }

    @Test
    void getAllCases_ShouldReturnCasePage() {
        Page<DossierDto> casePage = new PageImpl<>(Collections.singletonList(dossierDto));
        when(dossierService.getAllCases(0, 5, null, null)).thenReturn(casePage);

        ResponseEntity<Page<DossierDto>> response = dossierController.getAllCases(0, 5, null, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(casePage, response.getBody());
    }

    @Test
    void updateCase_ShouldReturnUpdatedCase() throws TechnicalException {
        when(dossierService.updateCase(anyLong(), any(DossierDto.class))).thenReturn(dossierDto);

        ResponseEntity<DossierDto> response = dossierController.updateCase(1L, dossierDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dossierDto, response.getBody());
    }

    @Test
    void getCaseById_ShouldReturnCase() throws TechnicalException {
        when(dossierService.getCaseById(1L)).thenReturn(dossierDto);

        ResponseEntity<DossierDto> response = dossierController.getCaseById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dossierDto, response.getBody());
    }

    @Test
    void deleteCasePhysical_ShouldReturnNoContent() throws TechnicalException {
        doNothing().when(dossierService).deleteCase(1L);

        ResponseEntity<Void> response = dossierController.deleteCase(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


}
