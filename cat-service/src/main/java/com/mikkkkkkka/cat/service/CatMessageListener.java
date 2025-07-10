package com.mikkkkkkka.cat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikkkkkkka.cat.config.RabbitMQConfig;
import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.ApiResponse;
import com.mikkkkkkka.common.model.dto.CatDto;
import com.mikkkkkkka.common.model.filter.CatFilter;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CatMessageListener {

    private static final String PATH = "Cat Service";

    private final CatService catService;
    private final ObjectMapper objectMapper;

    @Autowired
    public CatMessageListener(CatService catService, ObjectMapper objectMapper) {
        this.catService = catService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.CAT_QUEUE)
    public ApiResponse<?> handleMessage(Message message) {
        try {
            String action = message.getMessageProperties().getHeader("action");
            String payload = new String(message.getBody());

            return switch (action) {
                case "CREATE_CAT" -> handleCreateCat(payload);
                case "GET_CAT_BY_ID" -> handleGetCatById(payload);
                case "UPDATE_CAT" -> handleUpdateCat(payload);
                case "DELETE_CAT" -> handleDeleteCat(payload);
                case "BEFRIEND_CATS" -> handleBefriendCats(payload);
                case "UNFRIEND_CATS" -> handleUnfriendCats(payload);
                case "GET_ALL_CATS" -> handleGetAllCats(payload);
                case "GET_ALL_CATS_FILTERED" -> handleGetAllCatsFiltered(payload);
                case "GET_CATS_BY_OWNER_ID" -> handleGetCatsByOwnerId(payload);
                case "SET_OWNER_TO_CAT" -> handleSetOwnerToCat(payload);
                case "UNSET_OWNER_FROM_CAT" -> handleUnsetOwnerFromCat(payload);
                case "OWNER_OWNS_CAT" -> handleOwnerOwnsCat(payload);
                default -> throw new RuntimeException("Unknown action: " + action);
            };
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            return new ApiResponse<>(400, "Invalid payload", PATH, exception);
        } catch (ImproperUpdateException exception) {
            return new ApiResponse<>(400, exception.getMessage(), PATH, exception);
        } catch (ResourceNotFoundException exception) {
            return new ApiResponse<>(404, exception.getMessage(), PATH, exception);
        } catch (Exception exception) {
            return new ApiResponse<>(500, exception.getMessage(), PATH, exception);
        }
    }

    private ApiResponse<?> handleCreateCat(String payload) throws JsonProcessingException {
        CatDto newCat = objectMapper.readValue(payload, CatDto.class);
        CatDto cat = catService.createCat(newCat);
        return ApiResponse.ok(PATH, cat);
    }

    private ApiResponse<?> handleGetCatById(String payload) throws JsonProcessingException, ResourceNotFoundException {
        long id = objectMapper.readValue(payload, Long.class);
        CatDto cat = catService.getCat(id);
        return ApiResponse.ok(PATH, cat);
    }

    private ApiResponse<?> handleUpdateCat(String payload) throws JsonProcessingException, ResourceNotFoundException, ImproperUpdateException {
        Map<String, Object> updateData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long catId = Long.parseLong(updateData.get("id").toString());
        CatDto catDetails = objectMapper.convertValue(updateData.get("cat"), CatDto.class);
        CatDto cat = catService.updateCat(catId, catDetails);
        return ApiResponse.ok(PATH, cat);
    }

    private ApiResponse<?> handleDeleteCat(String payload) throws JsonProcessingException {
        long id = objectMapper.readValue(payload, Long.class);
        catService.deleteCatById(id);
        return ApiResponse.ok(PATH, "Deleted cat successfully");
    }

    private ApiResponse<?> handleBefriendCats(String payload) throws JsonProcessingException, ResourceNotFoundException {
        Map<String, Object> befriendData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long cat1Id = Long.parseLong(befriendData.get("cat1Id").toString());
        long cat2Id = Long.parseLong(befriendData.get("cat2Id").toString());
        catService.befriendCats(cat1Id, cat2Id);
        return ApiResponse.ok(PATH, "Befriended cats successfully");
    }

    private ApiResponse<?> handleUnfriendCats(String payload) throws JsonProcessingException, ResourceNotFoundException {
        Map<String, Object> unfriendData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long cat1Id = Long.parseLong(unfriendData.get("cat1Id").toString());
        long cat2Id = Long.parseLong(unfriendData.get("cat2Id").toString());
        catService.unfriendCats(cat1Id, cat2Id);
        return ApiResponse.ok(PATH, "Unfriended cats successfully");
    }

    private ApiResponse<?> handleGetAllCats(String payload) throws JsonProcessingException, IllegalArgumentException {
        Map<String, Object> getAllData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        int page = Integer.parseInt(getAllData.get("page").toString());
        int size = Integer.parseInt(getAllData.get("size").toString());
        List<CatDto> cats = catService.getAllCats(PageRequest.of(page, size));
        return ApiResponse.ok(PATH, cats);
    }

    private ApiResponse<?> handleGetAllCatsFiltered(String payload) throws JsonProcessingException, IllegalArgumentException {
        Map<String, Object> getAllData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        CatFilter filter = objectMapper.convertValue(getAllData.get("filter"), CatFilter.class);
        int page = Integer.parseInt(getAllData.get("page").toString());
        int size = Integer.parseInt(getAllData.get("size").toString());
        List<CatDto> cats = catService.getAllCatsFiltered(filter, PageRequest.of(page, size));
        return ApiResponse.ok(PATH, cats);
    }

    private ApiResponse<?> handleGetCatsByOwnerId(String payload) throws JsonProcessingException {
        long ownerId = objectMapper.readValue(payload, Long.class);
        List<Long> catIds = catService.getCatsByOwnerId(ownerId)
                .stream()
                .map(CatDto::id)
                .toList();
        return ApiResponse.ok(PATH, catIds);
    }

    private ApiResponse<?> handleSetOwnerToCat(String payload) throws JsonProcessingException, ResourceNotFoundException {
        Map<String, Object> setOwnerData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long catId = Long.parseLong(setOwnerData.get("catId").toString());
        long ownerId = Long.parseLong(setOwnerData.get("ownerId").toString());
        catService.setOwnerToCat(catId, ownerId);
        return ApiResponse.ok(PATH, "Owner is set successfully");
    }

    private ApiResponse<?> handleUnsetOwnerFromCat(String payload) throws ResourceNotFoundException {
        long catId = Long.parseLong(payload);
        catService.unsetOwnerFromCat(catId);
        return ApiResponse.ok(PATH, "Owner is unset successfully");
    }

    private ApiResponse<?> handleOwnerOwnsCat(String payload) throws JsonProcessingException, ResourceNotFoundException {
        Map<String, Object> ownerVerificationData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long ownerId = Long.parseLong(ownerVerificationData.get("ownerId").toString());
        long ownersCatId = Long.parseLong(ownerVerificationData.get("catId").toString());
        boolean isOwner = catService.ownerOwnsCat(ownerId, ownersCatId);
        return ApiResponse.ok(PATH, isOwner);
    }
}