package ma.dream.case_backend.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.dream.case_backend.config.Messages;
import ma.dream.case_backend.dto.PresenceJourDto;
import ma.dream.case_backend.dto.PresenceStatutCountDto;
import ma.dream.case_backend.enums.StatutPresence;
import ma.dream.case_backend.exceptions.TechnicalException;
import ma.dream.case_backend.mapper.PresenceJourMapper;
import ma.dream.case_backend.model.PresenceJour;
import ma.dream.case_backend.repository.PresenceJourRepository;
import ma.dream.case_backend.util.constants.GlobalConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PresenceJourService {

    private final PresenceJourRepository presenceJourRepository;
    private final PresenceJourMapper presenceJourMapper;
    private final EntityManager entityManager;
    private final Messages messages;


    public PresenceJour createPresenceJour(PresenceJourDto presenceJourDto) {
        PresenceJour presenceJour = presenceJourMapper.toPresenceJour(presenceJourDto);

        if (presenceJour.getFirstIn() != null && presenceJour.getLastOut() != null) {
            Duration dureeTravail = Duration.between(presenceJour.getFirstIn(), presenceJour.getLastOut());

            Duration pause = presenceJour.getBreakTime() != null ? Duration.ofHours(1) : Duration.ZERO;

            Duration total = dureeTravail.minus(pause);
            presenceJour.setTotalHeures(total);
        } else {
            presenceJour.setTotalHeures(Duration.ZERO);
        }

        presenceJour.setCreationDate(LocalDateTime.now());
        presenceJour.setLastUpdateDate(LocalDateTime.now());

        return presenceJourRepository.save(presenceJour);
    }

    public Page<PresenceJourDto> getAllPresenceJour(int page, int size, String searchByNom) {
        log.debug("Start service Get Presences page: {} size: {} searchByNom: {} ", page, size, searchByNom);
        Pageable pageable = PageRequest.of(page, size);
        Page<PresenceJour> presenceJours;

        if (searchByNom != null ) {
            presenceJours = filterPresenceJours(searchByNom, pageable);
        } else {
            presenceJours = presenceJourRepository.findAll(pageable);
        }

        List<PresenceJourDto> presenceJourDtos = presenceJours.getContent().stream()
                .map(presenceJourMapper::toPresenceJourDto)
                .toList();
        log.debug("End service getPresenceJourByCriteria ");
        return new PageImpl<>(presenceJourDtos, pageable, presenceJours.getTotalElements());
    }

    private Page<PresenceJour> filterPresenceJours(String searchByNom, Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PresenceJour> criteriaQuery = criteriaBuilder.createQuery(PresenceJour.class);
        Root<PresenceJour> root = criteriaQuery.from(PresenceJour.class);

        Predicate predicate = buildPredicate(criteriaBuilder, root, searchByNom);
        criteriaQuery.where(predicate);

        TypedQuery<PresenceJour> typedQuery = entityManager.createQuery(criteriaQuery);
        long totalCount = typedQuery.getResultList().size();
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<PresenceJour> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, totalCount);
    }

    private Predicate buildPredicate(CriteriaBuilder criteriaBuilder, Root<PresenceJour> root, String searchByNom) {
        Predicate predicate = criteriaBuilder.conjunction();

        if (searchByNom != null && !searchByNom.isEmpty()) {
            Join<Object, Object> employeeJoin = root.join("employee");
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.like(
                            criteriaBuilder.lower(employeeJoin.get("nom")),
                            "%" + searchByNom.toLowerCase() + "%"
                    )
            );
        }

        return predicate;
    }

    public PresenceJourDto updatePresenceJour(Long id, PresenceJourDto presenceJourDto) throws TechnicalException {
        log.debug("Start service update PresenceJour id {}", id);
        PresenceJour presenceJour = presenceJourRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));

        presenceJour.setBreakTime(presenceJourDto.getBreakTime());
        presenceJour.setLastOut(presenceJourDto.getLastOut());
        presenceJour.setFirstIn(presenceJourDto.getFirstIn());
        presenceJour.setNote(presenceJourDto.getNote());
        presenceJour.setShift(presenceJourDto.getShift());
        presenceJour.setStatut(presenceJourDto.getStatut());

        if (presenceJour.getFirstIn() != null && presenceJour.getLastOut() != null) {
            Duration dureeTravail = Duration.between(presenceJour.getFirstIn(), presenceJour.getLastOut());

            Duration pause = presenceJour.getBreakTime() != null ? Duration.ofHours(1) : Duration.ZERO;

            Duration total = dureeTravail.minus(pause);
            presenceJour.setTotalHeures(total);
        } else {
            presenceJour.setTotalHeures(Duration.ZERO);
        }

        presenceJour.setLastUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
        log.debug("End service update employee  with id {}, employee {}", id, presenceJourDto);
        return presenceJourMapper.toPresenceJourDto(presenceJourRepository.save(presenceJour));
    }

    public PresenceJourDto getPresenceJourById(Long id) throws TechnicalException {
        log.debug("Start service get PresenceJour By Id {}", id);
        return presenceJourRepository.findById(id)
                .map(presenceJourMapper::toPresenceJourDto)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));
    }

    public void deletePresenceJour(Long id) throws TechnicalException {
        log.debug("Start service delete PresenceJour By Id {}", id);
        if (id == null) {
            throw new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND));
        }
        PresenceJour presenceJour = presenceJourRepository.findById(id)
                .orElseThrow(() -> new TechnicalException(messages.get(GlobalConstants.CASE_NOT_FOUND)));
        presenceJourRepository.delete(presenceJour);
        log.debug("End service delete PresenceJour By Id {}", id);
    }


    public List<PresenceStatutCountDto> countPresenceStatutsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        List<Object[]> results = presenceJourRepository.countByStatutBetweenDates(startOfDay, endOfDay);

        return results.stream()
                .map(row -> new PresenceStatutCountDto((StatutPresence) row[0], (Long) row[1]))
                .collect(Collectors.toList());
    }


}
