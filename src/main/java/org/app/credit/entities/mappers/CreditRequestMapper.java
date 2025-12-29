package org.app.credit.entities.mappers;

import org.app.credit.entities.CreditRequest;
import org.app.credit.entities.dtos.CreditRequestCreatedDto;
import org.app.credit.entities.dtos.CreditRequestResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CreditRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "user", ignore = true)
    CreditRequest toEntity(CreditRequestCreatedDto dto);

    CreditRequestResponseDto toDto(CreditRequest entity);

    List<CreditRequestResponseDto> toDtoList(List<CreditRequest> list);
}
