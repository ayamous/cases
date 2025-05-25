package ma.dream.case_backend.mapper;


import ma.dream.case_backend.dto.EmployeeDto;
import ma.dream.case_backend.model.Employee;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeMapper {

    EmployeeDto toEmployeDto(Employee employe);

    Employee toEmploye(EmployeeDto employeDto);

    List<EmployeeDto> toEmployeDtos(List<Employee> employes);

    List<Employee> toEmployes(List<EmployeeDto> employeDtos);
}
