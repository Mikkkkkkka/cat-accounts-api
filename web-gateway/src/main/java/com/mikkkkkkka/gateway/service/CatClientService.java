package com.mikkkkkkka.gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikkkkkkka.common.exception.BadRequestException;
import com.mikkkkkkka.common.exception.ServiceUnavailableException;
import com.mikkkkkkka.common.model.dto.ApiResponse;
import com.mikkkkkkka.common.model.dto.CatDto;
import com.mikkkkkkka.common.model.filter.CatFilter;
import com.mikkkkkkka.gateway.config.RabbitMQConfig;
import com.mikkkkkkka.gateway.exception.TransferredHttpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CatClientService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public CatClientService(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    private Object readDataFromRequest(Message message) {
        var response = (ApiResponse<?>) rabbitTemplate.convertSendAndReceive(
                RabbitMQConfig.CAT_EXCHANGE,
                RabbitMQConfig.CAT_ROUTING_KEY,
                message);
        if (response == null)
            throw new ServiceUnavailableException("Cat Service unavailable");
        if (response.status() != HttpStatus.OK.value()) {
            RuntimeException exception = objectMapper.convertValue(response.data(), RuntimeException.class);
            throw new TransferredHttpException(response.status(), exception);
        }
        return response.data();
    }

    public Object createCat(CatDto catDto) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(catDto))
                .setHeader("action", "CREATE_CAT")
                .build();
        return readDataFromRequest(message);
    }

    public Object getCat(long id) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(id))
                .setHeader("action", "GET_CAT_BY_ID")
                .build();
        return readDataFromRequest(message);
    }

    public Object updateCat(long id, CatDto catDto) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(Map.of("id", id, "cat", catDto)))
                .setHeader("action", "UPDATE_CAT")
                .build();
        return readDataFromRequest(message);
    }

    public Object deleteCat(long id) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(id))
                .setHeader("action", "DELETE_CAT")
                .build();
        return readDataFromRequest(message);
    }

    public Object getAllCatsFiltered(CatFilter filter, int page, int size) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("filter", filter, "page", page, "size", size)))
                .setHeader("action", "GET_ALL_CATS_FILTERED")
                .build();
        return readDataFromRequest(message);
    }

    public Object befriendCats(long cat1Id, long cat2Id) throws BadRequestException, JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("cat1Id", cat1Id, "cat2Id", cat2Id)))
                .setHeader("action", "BEFRIEND_CATS")
                .build();
        return readDataFromRequest(message);
    }

    public Object unfriendCats(long cat1Id, long cat2Id) throws BadRequestException, JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("cat1Id", cat1Id, "cat2Id", cat2Id)))
                .setHeader("action", "UNFRIEND_CATS")
                .build();
        return readDataFromRequest(message);
    }

    public boolean ownerOwnsCat(long ownerId, long catId) throws JsonProcessingException {
        Message message = MessageBuilder
                .withBody(objectMapper.writeValueAsBytes(
                        Map.of("ownerId", ownerId, "catId", catId)))
                .setHeader("action", "OWNER_OWNS_CAT")
                .build();
        return (Boolean) readDataFromRequest(message);
    }
}