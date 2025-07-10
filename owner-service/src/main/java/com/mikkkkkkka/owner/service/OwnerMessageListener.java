package com.mikkkkkkka.owner.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikkkkkkka.common.exception.ImproperUpdateException;
import com.mikkkkkkka.common.exception.ResourceNotFoundException;
import com.mikkkkkkka.common.model.dto.ApiResponse;
import com.mikkkkkkka.common.model.dto.OwnerDto;
import com.mikkkkkkka.common.model.filter.OwnerFilter;
import com.mikkkkkkka.owner.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OwnerMessageListener {

    private static final String PATH = "Owner Service";

    private final OwnerService ownerService;
    private final ObjectMapper objectMapper;

    @Autowired
    public OwnerMessageListener(OwnerService ownerService, ObjectMapper objectMapper) {
        this.ownerService = ownerService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = RabbitMQConfig.OWNER_QUEUE)
    public ApiResponse<?> handleMessage(Message message) {
        try {
            String action = message.getMessageProperties().getHeader("action");
            String payload = new String(message.getBody());

            return switch (action) {
                case "CREATE_OWNER" -> handleCreateOwner(payload);
                case "GET_OWNER_BY_ID" -> handleGetOwnerById(payload);
                case "UPDATE_OWNER" -> handleUpdateOwner(payload);
                case "DELETE_OWNER" -> handleDeleteOwner(payload);
                case "GET_ALL_OWNERS" -> handleGetAllOwners(payload);
                case "GET_ALL_OWNERS_FILTERED" -> handleGetAllOwnersFiltered(payload);
                default -> throw new RuntimeException("Unknown action: " + action);
            };
        } catch (JsonProcessingException | IllegalArgumentException exception) {
            return new ApiResponse<>(400, "Invalid payload", PATH, exception);
        } catch (ResourceNotFoundException exception) {
            return new ApiResponse<>(404, exception.getMessage(), PATH, exception);
        } catch (ImproperUpdateException exception) {
            return new ApiResponse<>(400, exception.getMessage(), PATH, exception);
        } catch (Exception exception) {
            return new ApiResponse<>(500, exception.getMessage(), PATH, exception);
        }
    }

    private ApiResponse<?> handleCreateOwner(String payload) throws JsonProcessingException {
        OwnerDto newOwner = objectMapper.readValue(payload, OwnerDto.class);
        OwnerDto owner = ownerService.createOwner(newOwner);
        return ApiResponse.ok(PATH, owner);
    }

    private ApiResponse<?> handleGetOwnerById(String payload) throws JsonProcessingException, ResourceNotFoundException {
        long id = objectMapper.readValue(payload, Long.class);
        OwnerDto owner = ownerService.getOwner(id);
        return ApiResponse.ok(PATH, owner);
    }

    private ApiResponse<?> handleUpdateOwner(String payload) throws JsonProcessingException, IllegalArgumentException, ResourceNotFoundException, ImproperUpdateException {
        Map<String, Object> updateData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        long id = Long.parseLong(updateData.get("id").toString());
        OwnerDto ownerDetails = objectMapper.convertValue(updateData.get("owner"), OwnerDto.class);
        OwnerDto owner = ownerService.updateOwner(id, ownerDetails);
        return ApiResponse.ok(PATH, owner);
    }

    private ApiResponse<?> handleDeleteOwner(String payload) throws JsonProcessingException {
        Long deleteId = objectMapper.readValue(payload, Long.class);
        ownerService.deleteOwnerById(deleteId);
        return ApiResponse.ok(PATH, null);
    }

    private ApiResponse<?> handleGetAllOwners(String payload) throws JsonProcessingException {
        Map<String, Object> getAllData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        int page = Integer.parseInt(getAllData.get("page").toString());
        int size = Integer.parseInt(getAllData.get("size").toString());
        List<OwnerDto> owners = ownerService.getAllOwners(PageRequest.of(page, size));
        return ApiResponse.ok(PATH, owners);
    }

    private ApiResponse<?> handleGetAllOwnersFiltered(String payload) throws JsonProcessingException {
        Map<String, Object> getAllData = objectMapper.readValue(payload,
                new TypeReference<>() {
                });
        int page = Integer.parseInt(getAllData.get("page").toString());
        int size = Integer.parseInt(getAllData.get("size").toString());
        OwnerFilter filter = objectMapper.convertValue(getAllData.get("filter"), OwnerFilter.class);
        List<OwnerDto> owners = ownerService.getAllOwnersFiltered(filter, PageRequest.of(page, size));
        return ApiResponse.ok(PATH, owners);
    }
}