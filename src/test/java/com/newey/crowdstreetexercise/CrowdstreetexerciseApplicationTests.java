package com.newey.crowdstreetexercise;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.newey.crowdstreetexercise.controllers.RequestController;
import com.newey.crowdstreetexercise.dto.RequestDto;
import com.newey.crowdstreetexercise.dto.StatusDto;
import com.newey.crowdstreetexercise.dto.ThirdPartyStatusDto;
import com.newey.crowdstreetexercise.persistence.entities.RequestEntity;
import com.newey.crowdstreetexercise.persistence.repository.RequestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(RequestController.class)
class CrowdstreetexerciseApplicationTests {

	@Autowired
	private MockMvc mockMvc;

//	@Mock
//	private RequestRepository repository;

	@Autowired
 	private RequestRepository repository;

	private static ObjectMapper jsonMapper;

	@BeforeAll
	static void setup() {
		jsonMapper = new ObjectMapper();
		jsonMapper.registerModule(new JavaTimeModule());
	}

	@Test
	public void successRequestPost() throws Exception {
		// Test a successful /request POST request.

		// Normally, I would use an in-memory database for testing but I'm doing this quick-and-dirty so I'll just
		// use the normal run-time database and create a unique body value for each test run so I can test to make sure
		// the test created the correct entity. This of course would not be feasible in the real world because the
		// database could fill up with so many rows that running tests would take forever to run, plus it would clutter
		// the database with junk rows.
		UUID testBodyUuid = UUID.randomUUID();
		String testBody = "Test body: " + testBodyUuid;
		//
		RequestDto requestDto = new RequestDto(testBody);
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(requestDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("SUCCESS");

		// Make sure that the controller saved a new RequestEntity
		RequestEntity entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();
	}

	@Test
	public void badRequestPostBody() throws Exception {
		// Test a /request POST request with invalid json payload.

		// Get the number of RequestEntities in the database before the call.
		List<RequestEntity> createdEntitiesBefore = repository.findAll();

		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content("{\"bad\": \"unexpected\"}"))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat(response.getContentAsString()).isEqualTo("ERROR: Bad json payload");

		// Get the number of RequestEntities in the database before the call.
		List<RequestEntity> createdEntitiesAfter = repository.findAll();
		// Make sure no news rows were added to the database.
		assertThat(createdEntitiesAfter.size()).isEqualTo(createdEntitiesBefore.size());
	}

	@Test
	public void successfulCallback() throws Exception {
		// Test a successful /callback POST request.

		// First create the entity
		UUID testBodyUuid = UUID.randomUUID();
		String testBody = "Test body: " + testBodyUuid;
		//
		RequestDto requestDto = new RequestDto(testBody);
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(requestDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("SUCCESS");

		// Get the new entity.
		RequestEntity entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();

		// Now perform the callback.
		ThirdPartyStatusDto statusDto = new ThirdPartyStatusDto();
		statusDto.setStatus(ThirdPartyStatusDto.Status.PROCESSED);
		statusDto.setDetail("This is the first callback.");
		response = mockMvc.perform(
				MockMvcRequestBuilders.put("/callback/" + entity.getId())
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(statusDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(204);

		// Now get the entity again and be sure the status and detail are correct.
		entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();
		assertThat(entity.getStatus()).isEqualTo(RequestEntity.Status.PROCESSED);
		assertThat(entity.getDetail()).isEqualTo("This is the first callback.");

		// Now perform another callback.
		statusDto.setStatus(ThirdPartyStatusDto.Status.COMPLETED);
		statusDto.setDetail("This is the second callback.");
		response = mockMvc.perform(
				MockMvcRequestBuilders.put("/callback/" + entity.getId())
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(statusDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(204);

		// Now get the entity again and be sure the status and detail are correct.
		entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();
		assertThat(entity.getStatus()).isEqualTo(RequestEntity.Status.COMPLETED);
		assertThat(entity.getDetail()).isEqualTo("This is the second callback.");

		// Finally perform a third callback.
		statusDto.setStatus(ThirdPartyStatusDto.Status.ERROR);
		statusDto.setDetail("This is the third callback.");
		response = mockMvc.perform(
				MockMvcRequestBuilders.put("/callback/" + entity.getId())
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(statusDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(204);

		// Now get the entity again and be sure the status and detail are correct.
		entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();
		assertThat(entity.getStatus()).isEqualTo(RequestEntity.Status.ERROR);
		assertThat(entity.getDetail()).isEqualTo("This is the third callback.");
	}

	@Test
	public void callbackWithInvalidId() throws Exception {
		// First get the largest id in our database.
		int lastId = getMaxId();
		// Now perform the callback.
		ThirdPartyStatusDto statusDto = new ThirdPartyStatusDto();
		statusDto.setStatus(ThirdPartyStatusDto.Status.PROCESSED);
		statusDto.setDetail("This is the first callback.");
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.put("/callback/" + (lastId + 100))
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(statusDto)))
												  .andReturn()
												  .getResponse();
		// We should get a 204 response whether the id is valid or not.
		assertThat(response.getStatus()).isEqualTo(204);
	}

	@Test
	public void getStatusSuccessful() throws Exception {
		// Test a successful /status GET request.

		// First create the entity
		UUID testBodyUuid = UUID.randomUUID();
		String testBody = "Test body: " + testBodyUuid;
		//
		RequestDto requestDto = new RequestDto(testBody);
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(requestDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("SUCCESS");

		// Get the new entity.
		RequestEntity entity = getRequestEntityWithBody(testBody);
		assertThat(entity).isNotNull();

		// Now call /status before the callback is called.
		response = mockMvc.perform(
				MockMvcRequestBuilders.get("/status/" + entity.getId()))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);

		StatusDto statusResult = jsonMapper.readValue(response.getContentAsString(), StatusDto.class);

		assertThat(statusResult.getBody()).isEqualTo(testBody);
		assertThat(statusResult.getStatus()).isNull();
		assertThat(statusResult.getDetail()).isNull();
		// No update has occurred yet.
		assertThat(statusResult.getCreated()).isEqualTo(statusResult.getLastUpdated());

		// Now perform the callback.
		ThirdPartyStatusDto statusDto = new ThirdPartyStatusDto();
		statusDto.setStatus(ThirdPartyStatusDto.Status.PROCESSED);
		statusDto.setDetail("This is the first callback.");
		response = mockMvc.perform(
				MockMvcRequestBuilders.put("/callback/" + entity.getId())
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(statusDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(204);

		// Now call /status again.
		response = mockMvc.perform(
				MockMvcRequestBuilders.get("/status/" + entity.getId()))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);

		statusResult = jsonMapper.readValue(response.getContentAsString(), StatusDto.class);

		assertThat(statusResult.getBody()).isEqualTo(testBody);
		assertThat(statusResult.getStatus()).isEqualTo("PROCESSED");
		assertThat(statusResult.getDetail()).isEqualTo("This is the first callback.");
		assertThat(statusResult.getCreated()).isNotEqualTo(statusResult.getLastUpdated());
	}

	@Test
	public void testStatusNotFound() throws Exception {
		// Test status called with an invalid id.
		// First get the largest id in our database.
		int lastId = getMaxId();
		// Now perform the status call.
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.get("/status/" + (lastId + 100)))
												  .andReturn()
												  .getResponse();
		// We should get a 404 response because the id is not in our database.
		assertThat(response.getStatus()).isEqualTo(404);
	}

	private RequestEntity getRequestEntityWithBody(String body) {
		List<RequestEntity> createdEntities = repository.findAll()
														.stream()
														.filter(entity -> entity.getBody().equals(body))
														.collect(Collectors.toList());
		if (1 == createdEntities.size()) {
			return createdEntities.get(0);
		}
		// none found or more than one found
		return null;
	}

	private int getMaxId() {
		return repository.findAll().stream().max(new Comparator<RequestEntity>() {
			@Override
			public int compare(RequestEntity o1, RequestEntity o2) {
				return o1.getId() - o2.getId();
			}
		}).get().getId();
	}
}
