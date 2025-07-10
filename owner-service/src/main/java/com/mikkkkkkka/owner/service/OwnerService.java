package com.mikkkkkkka.owner.service;

import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.OwnerDto;
import com.mikkkkkkka.common.model.filter.OwnerFilter;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OwnerService {

    OwnerDto createOwner(OwnerDto owner);

    OwnerDto getOwner(long id) throws ResourceNotFoundException;

    OwnerDto updateOwner(long id, OwnerDto owner) throws ResourceNotFoundException, ImproperUpdateException;

    void deleteOwnerByDto(OwnerDto owner);

    void deleteOwnerById(long id);

    List<OwnerDto> getAllOwners(Pageable pageable);

    List<OwnerDto> getAllOwnersFiltered(OwnerFilter filter, Pageable pageable);
}