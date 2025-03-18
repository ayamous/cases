package ma.dream.case_backend.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.dream.case_backend.config.Messages;
import ma.dream.case_backend.dto.DossierDto;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.mapper.DossierMapper;
import ma.dream.case_backend.model.Dossier;
import ma.dream.case_backend.repository.DossierRepository;
import ma.dream.case_backend.util.constants.GlobalConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class DossierService {

    private final DossierRepository dossierRepository;
    private final DossierMapper dossierMapper;
    private final EntityManager entityManager;
    private final Messages messages;


    public Dossier createCase(DossierDto dossierDto) {
        Dossier caseEntity = dossierMapper.toDossier(dossierDto);

        caseEntity.setCreationDate(LocalDateTime.now());
        caseEntity.setLastUpdateDate(LocalDateTime.now());

        return dossierRepository.save(caseEntity);
    }

    public Page<DossierDto> getAllCases(int page, int size, String searchByDescription, String searchTitle) {
        log.debug("Start service Get Cases page: {} size: {} searchByDescription: {} searchTitle: {} ", page, size, searchByDescription, searchTitle);
        Pageable pageable = PageRequest.of(page, size);
        Page<Dossier> cases;

        if (searchByDescription != null || searchTitle != null ) {
            cases = filterCases(searchByDescription, searchTitle, pageable);
        } else {
            cases = dossierRepository.findAll(pageable);
        }

        List<DossierDto> casesDtos = cases.getContent().stream()
                .map(dossierMapper::toDossierDto)
                .toList();
        log.debug("End service getProductsByCriteria ");
        return new PageImpl<>(casesDtos, pageable, cases.getTotalElements());
    }

    private Page<Dossier> filterCases(String searchByDescription, String searchTitle, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Dossier> criteriaQuery = criteriaBuilder.createQuery(Dossier.class);
        Root<Dossier> root = criteriaQuery.from(Dossier.class);

        Predicate predicate = buildPredicate(criteriaBuilder, root, searchByDescription, searchTitle);
        criteriaQuery.where(predicate);

        TypedQuery<Dossier> typedQuery = entityManager.createQuery(criteriaQuery);
        long totalCount = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Dossier> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<Dossier> root,
                                     String searchByDescription, String searchTitle) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (searchByDescription != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + searchByDescription.toLowerCase() + "%"));
        }

        if (searchTitle != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
                    "%" + searchTitle.toLowerCase() + "%"));
        }

        return predicate;
    }

    public DossierDto updateCase(Long id, DossierDto dossierDto) throws TechnicalException {
        log.debug("Start service update case id {}", id);
        Dossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));

        dossier.setDescription(dossierDto.getDescription());
        dossier.setTitle(dossierDto.getTitle());
        dossier.setLastUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
        log.debug("End service update product  with id {}, product {}", id, dossierDto);
        return dossierMapper.toDossierDto(dossierRepository.save(dossier));
    }

    public DossierDto getCaseById(Long id) throws TechnicalException {
        log.debug("Start service get case By Id {}", id);
        return dossierRepository.findById(id)
                .map(dossierMapper::toDossierDto)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));
    }

     public void deleteCase(Long id) throws TechnicalException {
        log.debug("Start service delete case By Id {}", id);
        if (id == null) {
            throw new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND));
        }
        Dossier dossier = dossierRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));
        dossierRepository.delete(dossier);
        log.debug("End service delete case By Id {}", id);
    }

}
