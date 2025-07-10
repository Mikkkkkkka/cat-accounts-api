package com.mikkkkkkka.owner.service;

import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.OwnerDto;
import com.mikkkkkkka.common.model.filter.OwnerFilter;
import com.mikkkkkkka.owner.dao.OwnerRepository;
import com.mikkkkkkka.owner.model.entity.Owner;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepo;

    @Autowired
    public OwnerServiceImpl(OwnerRepository ownerRepo) {
        this.ownerRepo = ownerRepo;
    }

    @Override
    public OwnerDto createOwner(OwnerDto owner) {
        Owner ownerEntity = Owner.builder()
                .id(null)
                .name(owner.name())
                .birthday(owner.birthday())
                .build();
        ownerRepo.save(ownerEntity);
        return ownerEntity.toDto();
    }

    @Override
    public OwnerDto getOwner(long id) throws ResourceNotFoundException {
        Owner owner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        return owner.toDto();
    }

    @Override
    public OwnerDto updateOwner(long id, OwnerDto owner) throws ResourceNotFoundException, ImproperUpdateException {
        final Owner originalOwner = ownerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        final boolean idModified = owner.id() != null &&
                !originalOwner.getId().equals(owner.id());
        if (idModified)
            throw new ImproperUpdateException(
                    "Cannot update owner Id");

        Owner ownerEntity = Owner.builder()
                .id(id)
                .name(owner.name())
                .birthday(owner.birthday())
                .build();
        return ownerRepo.save(ownerEntity).toDto();
    }

    @Override
    public void deleteOwnerByDto(OwnerDto owner) {
        ownerRepo.deleteById(owner.id());
    }

    @Override
    public void deleteOwnerById(long id) {
        ownerRepo.deleteById(id);
    }

    @Override
    public List<OwnerDto> getAllOwners(Pageable pageable) {
        return ownerRepo.findAll(pageable)
                .stream()
                .map(Owner::toDto)
                .toList();
    }

    @Override
    public List<OwnerDto> getAllOwnersFiltered(OwnerFilter filter, Pageable pageable) {
        Specification<Owner> spec = buildSpecification(filter);
        return ownerRepo.findAll(spec, pageable)
                .stream()
                .map(Owner::toDto)
                .toList();
    }

    private Specification<Owner> buildSpecification(OwnerFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.birthdayAfter() != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("birthday"), filter.birthdayAfter()));

            if (filter.birthdayBefore() != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("birthday"), filter.birthdayBefore()));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}