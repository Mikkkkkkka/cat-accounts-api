package com.mikkkkkkka.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikkkkkkka.common.exception.ServiceUnavailableException;
import com.mikkkkkkka.common.model.dto.ApiResponse;
import com.mikkkkkkka.common.model.dto.OwnerDto;
import com.mikkkkkkka.common.model.dto.OwnerDtoWithCats;
import com.mikkkkkkka.common.model.filter.OwnerFilter;
import com.mikkkkkkka.gateway.config.RabbitMQConfig;
import com.mikkkkkkka.gateway.exception.TransferredHttpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OwnerClientService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public OwnerClientService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    private Object readDataFromResponse(Message message) throws RuntimeException {
        var response = (ApiResponse<?>) rabbitTemplate.convertSendAndReceive(
                RabbitMQConfig.OWNER_EXCHANGE,
                RabbitMQConfig.OWNER_ROUTING_KEY,
                message);
        if (response == null)
            throw new ServiceUnavailableException("Owner service unavailable");
        if (response.status() != HttpStatus.OK.value()) {
            RuntimeException exception = objectMapper.convertValue(response.data(), RuntimeException.class);
            throw new TransferredHttpException(response.status(), exception);
        }
        return response.data();
    }

    private OwnerDtoWithCats addCatsToOwner(OwnerDto ownerNoCats) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(ownerNoCats.id()))
                .setHeader("action", "GET_CATS_BY_OWNER_ID")
                .build();
        var response = (ApiResponse<?>) rabbitTemplate.convertSendAndReceive(
                RabbitMQConfig.CAT_EXCHANGE,
                RabbitMQConfig.CAT_ROUTING_KEY,
                message);
        if (response == null)
            throw new ServiceUnavailableException("Owner service unavailable");
        if (response.status() != HttpStatus.OK.value()) {
            RuntimeException exception = objectMapper.convertValue(response.data(), RuntimeException.class);
            throw new TransferredHttpException(response.status(), exception);
        }
        var list = (List<?>) response.data();
        var cats = list.stream()
                .map(obj -> (Number) obj)
                .toList();
        return new OwnerDtoWithCats(ownerNoCats, cats);
    }

    public Object createOwner(OwnerDto ownerDto) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(ownerDto))
                .setHeader("action", "CREATE_OWNER")
                .build();
        return readDataFromResponse(message);
    }

    public Object getOwner(long id) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(id))
                .setHeader("action", "GET_OWNER_BY_ID")
                .build();
        OwnerDto ownerNoCats = objectMapper.convertValue(readDataFromResponse(message), OwnerDto.class);
        return addCatsToOwner(ownerNoCats);
    }

    public Object updateOwner(long id, OwnerDto ownerDto) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("id", id, "owner", ownerDto)))
                .setHeader("action", "UPDATE_OWNER")
                .build();
        OwnerDto ownerNoCats = objectMapper.convertValue(readDataFromResponse(message), OwnerDto.class);
        return addCatsToOwner(ownerNoCats);
    }

    public Object deleteOwner(long id) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(id))
                .setHeader("action", "DELETE_OWNER")
                .build();
        return readDataFromResponse(message);
    }

    public Object getAllOwnersFiltered(OwnerFilter filter, int page, int size) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("filter", filter, "page", page, "size", size)))
                .setHeader("action", "GET_ALL_OWNERS_FILTERED")
                .build();
        List<?> list = objectMapper.convertValue(readDataFromResponse(message), List.class);
        List<OwnerDtoWithCats> result = new ArrayList<>(); // Очень хотелось сделать через Stream Api но не получилось. ;-;
        for (Object obj : list) {
            OwnerDto ownerDto = objectMapper.convertValue(obj, OwnerDto.class);
            OwnerDtoWithCats ownerDtoWithCats = addCatsToOwner(ownerDto);
            result.add(ownerDtoWithCats);
        }
        return result;
    }

    public Object addCatToOwner(long ownerId, long catId) throws JsonProcessingException {
        getOwner(ownerId);
        Message message2 = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("ownerId", ownerId, "catId", catId)))
                .setHeader("action", "SET_OWNER_TO_CAT")
                .build();
        return readDataFromResponse(message2);
    }

    public Object removeCatFromOwner(long ownerId, long catId) throws JsonProcessingException {
        getOwner(ownerId);
        Message message2 = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(catId))
                .setHeader("action", "UNSET_OWNER_FROM_CAT")
                .build();
        return readDataFromResponse(message2);
    }
}