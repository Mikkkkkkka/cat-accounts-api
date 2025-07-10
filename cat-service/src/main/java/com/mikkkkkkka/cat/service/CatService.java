package com.mikkkkkkka.cat.service;


import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.CatDto;
import com.mikkkkkkka.common.model.filter.CatFilter;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CatService {

    CatDto createCat(CatDto cat);

    CatDto getCat(long id) throws ResourceNotFoundException;

    CatDto updateCat(long id, CatDto cat) throws ResourceNotFoundException, ImproperUpdateException;

    void deleteCatByDto(CatDto cat);

    void deleteCatById(long id);

    @Transactional
    void befriendCats(long cat1Id, long cat2Id) throws ResourceNotFoundException;

    @Transactional
    void unfriendCats(long cat1Id, long cat2Id) throws ResourceNotFoundException;

    List<CatDto> getAllCats(Pageable pageable);

    List<CatDto> getAllCatsFiltered(CatFilter filter, Pageable pageable);

    List<CatDto> getCatsByOwnerId(long ownerId);

    void setOwnerToCat(long catId, long ownerId) throws ResourceNotFoundException;

    void unsetOwnerFromCat(long catId) throws ResourceNotFoundException;

    boolean ownerOwnsCat(long ownerId, long catId) throws ResourceNotFoundException;
}