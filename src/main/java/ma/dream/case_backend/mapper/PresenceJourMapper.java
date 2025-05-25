package ma.dream.case_backend.mapper;


import ma.dream.case_backend.dto.PresenceJourDto;
import ma.dream.case_backend.model.PresenceJour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PresenceJourMapper {

    @Mapping(source = "employe.employeId", target = "employeId")
    PresenceJourDto toPresenceJourDto(PresenceJour presenceJour);

    @Mapping(source = "employeId", target = "employe.employeId")
    PresenceJour toPresenceJour(PresenceJourDto presenceJourDto);

    List<PresenceJourDto> toPresenceJourDtos(List<PresenceJour> presenceJours);

    List<PresenceJour> toPresenceJours(List<PresenceJourDto> presenceJourDtos);


}
