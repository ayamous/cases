package ma.dream.case_backend.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierDto implements Serializable {

    private Long caseId;
    private String title;
    private String description;

}
