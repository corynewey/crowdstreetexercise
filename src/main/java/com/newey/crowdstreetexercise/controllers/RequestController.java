package com.newey.crowdstreetexercise.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newey.crowdstreetexercise.CrowdstreetexerciseApplication;
import com.newey.crowdstreetexercise.dto.RequestDto;
import com.newey.crowdstreetexercise.dto.StatusDto;
import com.newey.crowdstreetexercise.dto.ThirdPartyStatusDto;
import com.newey.crowdstreetexercise.persistence.entities.RequestEntity;
import com.newey.crowdstreetexercise.persistence.repository.RequestRepository;
import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class RequestController {
    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestRepository repository;

    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public ResponseEntity<String> postReqeust(@RequestBody final String req) {
        // Normally, I would accept a DTO that matches the expected json. This would provide the advantage of
        // allowing Spring to enforce the shape of the json object that is passed in but I don't want to go into setting
        // up Jackson marshalling in Spring (maybe it's not that hard but I don't want to take the time). Besides, I'll
        // just do the json shape enforcement myself below when I parse it with Jackson.
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            RequestDto requestDto = jsonMapper.readValue(req, RequestDto.class);
            // In real life, I would never do this. I would put the crud methods in a service where I could control
            // the transaction.
            RequestEntity entity = repository.save(new RequestEntity(requestDto.getBody()));

            // Make callout to the third-party service. This could be accomplished by using the Apache HttpClient library
            // or jaxrs of something like that.
            requestDto.setCallback("/callback/" + entity.getId());
            // Send the above dto to the third-party URL. I assume it should be a POST since we are sending json data
            // in the body, but the instructions don't say how we should call the third-party url.

            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }
        catch (JsonProcessingException e) {
            // Bad json data.
            log.error("Exception parsing reqeust body: " + req + " -- Exception: " + e.getMessage(), e);
            return new ResponseEntity<>("ERROR: Bad json payload", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(path = "/callback/{id}", method = RequestMethod.PUT)
    public void handleCallback(@PathVariable final Integer id, @RequestBody String body, ServerHttpResponse response) {
        // Spring does the heavy lifting for us here, assureing that the id is an Integer.
        RequestEntity entity = repository.findById(id);
        if (null != entity) {
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                ThirdPartyStatusDto statusDto = jsonMapper.readValue(body, ThirdPartyStatusDto.class);
                // In real life, I would never do this. I would put the crud methods in a service where I could control
                // the transaction.
                entity.setStatus(statusDto.getStatus());
                entity.setDetail(statusDto.getDetail());
                repository.save(entity);
            }
            catch (JsonProcessingException e) {
                log.error("Exception parsing reqeust body: " + body + " -- Exception: " + e.getMessage(), e);
            }
        }
        // The instructions say we have to return a 203 response, no matter what. I assume that means that even if
        // we have an error...
        response.setStatusCode(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
    public ResponseEntity<StatusDto> getStatus(@PathVariable Integer id) {
        // Spring does the heavy lifting for us here, assureing that the id is an Integer.
        RequestEntity entity = repository.findById(id);
        return new ResponseEntity<>(new StatusDto(entity.getBody(), entity.getStatus(), entity.getDetail()), HttpStatus.OK);
    }
}
