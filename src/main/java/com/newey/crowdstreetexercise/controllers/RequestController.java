package com.newey.crowdstreetexercise.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newey.crowdstreetexercise.CrowdstreetexerciseApplication;
import com.newey.crowdstreetexercise.dto.RequestDto;
import com.newey.crowdstreetexercise.dto.RequestResultDto;
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

import javax.servlet.http.HttpServletResponse;
import java.time.OffsetDateTime;

@RestController
public class RequestController {
    private static final Logger log = LoggerFactory.getLogger(RequestController.class);

    @Autowired
    private RequestRepository repository;

    // I know that the instructions say that this endpoint should return a string, but we need to return the request
    // id so I'm going to do it in JSON. Hey, it's still a string, right?
    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public ResponseEntity<RequestResultDto> postReqeust(@RequestBody final String req) {
        // Normally, I would specify a specific DTO as the @RequestBody parameter that matches the expected json.
        // Doing so provides the advantage of allowing Spring to enforce the shape of the json object that is passed in
        // but I don't want to go into setting up Jackson marshalling in Spring (maybe it's not that hard but I don't
        // want to take the time). Besides, I'll just do the json shape enforcement myself below when I parse it with
        // Jackson.
        try {
            ObjectMapper jsonMapper = new ObjectMapper();
            RequestDto requestDto = jsonMapper.readValue(req, RequestDto.class);
            // In real life, I would never do this. I would put the crud methods in a service where I could control
            // the transaction. Also, the service would take care of setting create/update timestamps. An even approach
            // would be to extend the repository, overriding the save() method so that create/update timestamps are
            // updated correctly at the time of the save/update.
            RequestEntity entity = new RequestEntity(requestDto.getBody());
            OffsetDateTime now = OffsetDateTime.now();
            entity.setCreatedDate(now);
            entity.setLastUpdateDate(now);
            entity = repository.save(entity);

            requestDto.setCallback("/callback/" + entity.getId());
            String returnMessage = callThirdPartyEndpoint(requestDto);

            return new ResponseEntity<>(new RequestResultDto(returnMessage, entity.getId()), HttpStatus.OK);
        }
        catch (JsonProcessingException e) {
            // Bad json data.
            log.error("Exception parsing request body: " + req + " -- Exception: " + e.getMessage(), e);
            return new ResponseEntity<>(new RequestResultDto("ERROR: Bad json payload", -1), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            log.error("Exception handling POST request /request body: " + req + " -- Exception: " + e.getMessage(), e);
            return new ResponseEntity<>(new RequestResultDto("ERROR: unknown error", -1), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String callThirdPartyEndpoint(RequestDto dto) {
        // Make callout to the third-party service. This could be accomplished by using the Apache HttpClient library
        // or Spring's RestTemplate or WebClient or jax-rs or something like that. There are a number of options.

        // Send the above dto to the third-party URL. I assume it should be a POST since we are sending json data
        // in the body, but the instructions don't say which verb should be used to call the third-party url.

        // The call-out to the third-party endpoint would of course have to do error checking, checking response code,
        // etc. The directions don't specify what should be returned to the user but I would assume that if we detect an
        // error from the third-party endpoint, we would return an error to the caller of the /request endpoint.
        return "SUCCESS";
    }

    @RequestMapping(path = "/callback/{id}", method = RequestMethod.PUT)
    public void handleCallback(@PathVariable final Integer id, @RequestBody String body, HttpServletResponse response) {
        // Spring does the heavy validation lifting for us here, assuring that the id is an Integer.
        // Also see the comment in the /request endpoint above about the @RequestBody parameter.
        RequestEntity entity = repository.findById(id);
        if (null != entity) {
            ObjectMapper jsonMapper = new ObjectMapper();
            try {
                ThirdPartyStatusDto statusDto = jsonMapper.readValue(body, ThirdPartyStatusDto.class);
                // In real life, I would never do this. I would put the crud methods in a service where I could control
                // the transaction.
                if (null != statusDto.getStatus()) {
                    entity.setStatus(RequestEntity.Status.valueOf(statusDto.getStatus().name()));
                }
                entity.setDetail(statusDto.getDetail());
                // Once again, in the real world this save would be handled in a service class or in a repository
                // extension so that the update time is guaranteed to be set.
                entity.setLastUpdateDate(OffsetDateTime.now());
                repository.save(entity);
            }
            catch (JsonProcessingException e) {
                log.error("Exception parsing reqeust body: " + body + " -- Exception: " + e.getMessage(), e);
            }
        }
        // The instructions say we have to return a 204 response, no matter what. I assume that means that even if
        // we have an error or if the id is not found in our database...
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }

    @RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
    public ResponseEntity<StatusDto> getStatus(@PathVariable Integer id) {
        // Spring does the heavy validation lifting for us here, assuring that the id is an Integer.
        RequestEntity entity = repository.findById(id);
        if (null != entity) {
            return new ResponseEntity<>(new StatusDto(entity), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}
