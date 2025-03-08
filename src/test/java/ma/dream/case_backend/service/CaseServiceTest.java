package ma.dream.case_backend.service;

import ma.dream.case_backend.dto.DossierDto;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.mapper.DossierMapper;
import ma.dream.case_backend.model.Dossier;
import ma.dream.case_backend.repository.DossierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CaseServiceTest {

    @InjectMocks
    private DossierService dossierService;

    @Mock
    private DossierRepository dossierRepository;

    @Mock
    private DossierMapper dossierMapper;

    private DossierDto dossierDto;
    private Dossier dossier;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        dossierDto = new DossierDto();
        dossierDto.setCaseId(1L);
        dossierDto.setTitle("Case Test");
        dossierDto.setDescription("Case Test");

        dossier = new Dossier();
        dossier.setCaseId(1L);
        dossier.setTitle("Case Test");
        dossier.setDescription("Case Test");
    }

    @Test
    void addCase_ShouldReturnCaseDto() {
        when(dossierMapper.toDossier(dossierDto)).thenReturn(dossier);

        when(dossierRepository.save(dossier)).thenReturn(dossier);

        when(dossierMapper.toDossierDto(dossier)).thenReturn(dossierDto);

        Dossier response = dossierService.createCase(dossierDto);

        assertEquals(dossier, response);
        verify(dossierRepository).save(dossier);
    }

    @Test
    void getCaseById_ShouldReturnCaseDto() throws TechnicalException {
        when(dossierRepository.findById(1L)).thenReturn(Optional.of(dossier));
        when(dossierMapper.toDossierDto(dossier)).thenReturn(dossierDto);

        DossierDto result = dossierService.getCaseById(1L);

        assertEquals(dossierDto, result);
    }

    @Test
    void updateCase_ShouldReturnUpdatedCaseDto() throws TechnicalException {
        when(dossierRepository.findById(1L)).thenReturn(Optional.of(dossier));

        when(dossierRepository.save(any(Dossier.class))).thenReturn(dossier);


        when(dossierMapper.toDossierDto(dossier)).thenReturn(dossierDto);

        DossierDto result = dossierService.updateCase(1L, dossierDto);

        assertEquals(dossierDto, result);
        verify(dossierRepository).save(dossier);
    }


    @Test
    void deleteCase_ShouldNotThrowException() throws TechnicalException {
        doNothing().when(dossierRepository).deleteById(1L);

        assertDoesNotThrow(() -> dossierService.deleteCase(1L));
    }


}
