package ma.dream.case_backend.mapper;


import ma.dream.case_backend.dto.DossierDto;
import ma.dream.case_backend.model.Dossier;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DossierMapper {

    DossierDto toDossierDto(Dossier dossier);

    Dossier toDossier(DossierDto dossierDto);

    List<DossierDto> toDossierDtos(List<Dossier> dossiers);

    List<Dossier> toDossiers(List<DossierDto> dossierDtos);

}
