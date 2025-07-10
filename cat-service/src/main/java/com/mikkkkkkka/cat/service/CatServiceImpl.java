package com.mikkkkkkka.cat.service;

import com.mikkkkkkka.cat.dao.CatRepository;
import com.mikkkkkkka.cat.model.entity.Cat;
import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.CatDto;
import com.mikkkkkkka.common.model.filter.CatFilter;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatServiceImpl implements CatService {

    private final CatRepository catRepo;

    @Autowired
    public CatServiceImpl(CatRepository catRepo) {
        this.catRepo = catRepo;
    }

    @Override
    public CatDto createCat(CatDto cat) {
        Cat catEntity = Cat.builder()
                .id(null)
                .name(cat.name())
                .birthday(cat.birthday())
                .breed(cat.breed())
                .color(cat.color())
                .ownerId(null)
                .friends(new ArrayList<>())
                .build();
        return catRepo.save(catEntity)
                .toDto();
    }

    @Override
    public CatDto getCat(long id) throws ResourceNotFoundException {
        Cat cat = catRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));
        return cat.toDto();
    }

    @Override
    public CatDto updateCat(long id, CatDto cat) throws ResourceNotFoundException, ImproperUpdateException {
        final Cat originalCat = catRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

        final boolean idModified = cat.id() != null &&
                !originalCat.getId().equals(cat.id());
        final boolean ownerModified = cat.ownerId() != null &&
                !cat.ownerId().equals(originalCat.getOwnerId());
        final boolean friendListModified = cat.friends() != null &&
                !originalCat.getFriends()
                        .stream()
                        .map(Cat::getId)
                        .sorted()
                        .toList()
                        .equals(cat.friends()
                                .stream()
                                .sorted()
                                .toList());

        if (idModified)
            throw new ImproperUpdateException(
                    "Cannot update Cat id");
        if (ownerModified)
            throw new ImproperUpdateException(
                    "Do not use CatService.updateCat() method for updating the owner! Use OwnerService.addCat() for that");
        if (friendListModified)
            throw new ImproperUpdateException(
                    "Do not use CatService.updateCat() method for updating the friend list! Use CatService.befriendCats() or CatService.unfriendCats()");

        Cat catEntity = Cat.builder()
                .id(id)
                .name(cat.name())
                .birthday(cat.birthday())
                .breed(cat.breed())
                .color(cat.color())
                .ownerId(cat.ownerId())
                .friends(originalCat.getFriends())
                .build();
        return catRepo.save(catEntity)
                .toDto();
    }

    @Override
    public void deleteCatByDto(CatDto cat) {
        catRepo.deleteById(cat.id());
    }

    @Override
    public void deleteCatById(long id) {
        catRepo.deleteById(id);
    }

    @Transactional
    @Override
    public void befriendCats(long cat1Id, long cat2Id) throws ResourceNotFoundException {
        Cat catEntity1 = catRepo.findById(cat1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat-1 not found"));
        Cat catEntity2 = catRepo.findById(cat2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat-2 not found"));
        catEntity1.getFriends().add(catEntity2);
        catEntity2.getFriends().add(catEntity1);
        catRepo.save(catEntity1);
        catRepo.save(catEntity2);
    }

    @Transactional
    @Override
    public void unfriendCats(long cat1Id, long cat2Id) throws ResourceNotFoundException {
        Cat catEntity1 = catRepo.findById(cat1Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat-1 not found"));
        Cat catEntity2 = catRepo.findById(cat2Id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat-2 not found"));
        catEntity1.getFriends().remove(catEntity2);
        catEntity2.getFriends().remove(catEntity1);
        catRepo.save(catEntity1);
        catRepo.save(catEntity2);
    }

    @Override
    public List<CatDto> getAllCats(Pageable pageable) {
        return catRepo.findAll(pageable)
                .stream()
                .map(Cat::toDto)
                .toList();
    }

    @Override
    public List<CatDto> getAllCatsFiltered(CatFilter filter, Pageable pageable) {
        Specification<Cat> spec = buildSpecification(filter);
        return catRepo.findAll(spec, pageable)
                .stream()
                .map(Cat::toDto)
                .toList();
    }

    private Specification<Cat> buildSpecification(CatFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.ownerId() != null)
                predicates.add(criteriaBuilder.equal(
                        root.get("ownerId"), filter.ownerId()));

            if (filter.colors() != null)
                predicates.add(root.get("color").in(filter.colors()));

            if (filter.birthdateAfter() != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("birthday"), filter.birthdateAfter()));

            if (filter.birthdateBefore() != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("birthday"), filter.birthdateBefore()));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Override
    public List<CatDto> getCatsByOwnerId(long ownerId) {
        List<Cat> cats = catRepo.findByOwnerId(ownerId);
        return cats.stream()
                .map(Cat::toDto)
                .toList();
    }

    @Override
    public void setOwnerToCat(long catId, long ownerId) throws ResourceNotFoundException {
        Cat cat = catRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));
        cat.setOwnerId(ownerId);
        catRepo.save(cat);
    }

    @Override
    public void unsetOwnerFromCat(long catId) throws ResourceNotFoundException {
        Cat cat = catRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));
        cat.setOwnerId(null);
        catRepo.save(cat);
    }

    @Override
    public boolean ownerOwnsCat(long ownerId, long catId) throws ResourceNotFoundException {
        Cat cat = catRepo.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));
        return cat.getOwnerId().equals(ownerId);
    }

}