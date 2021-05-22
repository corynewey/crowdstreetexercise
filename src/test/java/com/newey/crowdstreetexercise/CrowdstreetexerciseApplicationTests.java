package com.newey.crowdstreetexercise;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newey.crowdstreetexercise.controllers.RequestController;
import com.newey.crowdstreetexercise.dto.RequestDto;
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

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(RequestController.class)
class CrowdstreetexerciseApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private RequestRepository repository;

	@InjectMocks
	private RequestController requestController;

	private static ObjectMapper jsonMapper;

	@BeforeAll
	static void setup() {
		jsonMapper = new ObjectMapper();
	}

	@Test
	public void successRequestPost() throws Exception {
		//
		RequestDto requestDto = new RequestDto("Test body");
		RequestEntity requestEntity = new RequestEntity("Test body");
		requestEntity.setId(1);
		given(repository.save(new RequestEntity("Test body"))).willReturn(requestEntity);
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content(jsonMapper.writeValueAsString(requestDto)))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(200);
		assertThat(response.getContentAsString()).isEqualTo("SUCCESS");
	}

	@Test
	public void badRequestPostBody() throws Exception {
		//
		MockHttpServletResponse response = mockMvc.perform(
				MockMvcRequestBuilders.post("/request")
									  .contentType(MediaType.APPLICATION_JSON)
									  .content("{\"bad\": \"unexpected\"}"))
												  .andReturn()
												  .getResponse();
		assertThat(response.getStatus()).isEqualTo(400);
		assertThat(response.getContentAsString()).isEqualTo("ERROR: Bad json payload");
	}

}
