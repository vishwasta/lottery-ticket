package lottery.ticket.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lottery.ticket.dto.request.CreateTicketRequest;
import lottery.ticket.dto.request.LineRequest;
import lottery.ticket.dto.request.UpdateTicketRequest;
import lottery.ticket.dto.response.LineResponse;
import lottery.ticket.dto.response.StatusResponse;
import lottery.ticket.dto.response.TicketResponse;
import lottery.ticket.entity.Line;
import lottery.ticket.entity.Ticket;
import lottery.ticket.repository.TicketRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.com.google.common.net.HttpHeaders;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TicketControllerTest {

  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:16-alpine"
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.stop();
  }

  @Autowired
  private TicketRepository ticketRepository;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @BeforeEach
  public void setup() {
    ticketRepository.deleteAll();
  }

  @Test
  void shouldCreateTicket() throws Exception {
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    CreateTicketRequest createTicketRequest = new CreateTicketRequest(List.of(lineRequest2, lineRequest1));

    mockMvc.perform(post("/ticket")
        .content(mapper.writeValueAsBytes(createTicketRequest))
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isCreated());

    List<Ticket> ticket = ticketRepository.findAll();

    assertNotNull(ticket.get(0).getId());
    assertFalse(ticket.get(0).getChecked());
    assertTrue(ticket.get(0).getLines().stream().noneMatch(Objects::isNull));
  }

  @Test
  void shouldGetTickets() throws Exception {
    Ticket ticket1 = Ticket.from(List.of(new Line(1, 0, 1, 2),
      new Line(2, 0, 0, 0)));
    Ticket ticket2 = Ticket.from(List.of(new Line(1, 0, 1, 2),
      new Line(2, 0, 1, 0)));
    ticketRepository.saveAll(List.of(ticket2, ticket1));

    MvcResult mvcResult = mockMvc.perform(get("/ticket")
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isOk())
      .andDo(print())
      .andExpect(jsonPath("$").isArray())
      .andReturn();

    List<TicketResponse> ticketResponses = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<TicketResponse>>() {
    });


    TicketResponse ticketResponse1 = new TicketResponse(ticket2.getId(), List.of(new LineResponse(0, 1, 0, 2, 0),
      new LineResponse(0, 1, 2, 1, 1)));
    TicketResponse ticketResponse2 = new TicketResponse(ticket1.getId(), List.of(new LineResponse(0, 1, 2, 1, 1),
      new LineResponse(0, 0, 0, 2, 5)
    ));
    assertEquals(2, ticketResponses.size());
    assertTrue(ticketResponses.stream().map(TicketResponse::id).toList()
      .containsAll(List.of(ticket2.getId(), ticket1.getId())));
    assertTrue(ticketResponses.contains(ticketResponse1));
    assertTrue(ticketResponses.contains(ticketResponse2));
  }

  @Test
  void shouldGetTicketByTicketId() throws Exception {
    Ticket ticket1 = ticketRepository.save(Ticket.from(List.of(new Line(1, 0, 1, 2),
      new Line(2, 0, 0, 0))));


    MvcResult mvcResult = mockMvc.perform(get("/ticket/" + ticket1.getId())
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isOk())
      .andReturn();

    TicketResponse ticketResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<TicketResponse>() {
    });

    assertEquals(ticket1.getId(), ticketResponse.id());
    assertTrue(ticketResponse.lines()
      .containsAll(List.of(new LineResponse(0, 0, 0, 2, 5),
        new LineResponse(0, 1, 2, 1, 1))));
  }

  @Test
  void shouldGiveNotFoundWhenGetTicketWithoutTicket() throws Exception {

    mockMvc.perform(get("/ticket/" + 10000)
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Ticket with id 10000 not found"));
  }

  @Test
  void shouldUpdateTicketByAddingNewLines() throws Exception {
    Ticket ticket1 = ticketRepository.save(Ticket.from(List.of(new Line(1, 0, 1, 2))));
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    MvcResult mvcResult = mockMvc.perform(put("/ticket/" + ticket1.getId())
        .content(mapper.writeValueAsBytes(updateTicketRequest))
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isOk())
      .andReturn();

    TicketResponse ticketResponse = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<TicketResponse>() {
    });

    assertEquals(ticket1.getId(), ticketResponse.id());
    assertTrue(ticketResponse.lines()
      .containsAll(List.of(new LineResponse(0, 0, 0, 2, 5),
        new LineResponse(0, 1, 2, 3, 1),
        new LineResponse(0, 1, 2, 1, 1))));
  }

  @Test
  void shouldGiveBadRequestWhenUpdatingTicketWhenTicketStatusHasBeenChecked() throws Exception {
    Line line = new Line(1, 0, 1, 2);
    Ticket ticket = Ticket
      .builder()
      .checked(true)
      .lines(List.of(line))
      .build();
    line.setTicket(ticket);

    Ticket ticket1 = ticketRepository.save(ticket);
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    mockMvc.perform(put("/ticket/" + ticket1.getId())
        .content(mapper.writeValueAsBytes(updateTicketRequest))
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Ticket status already checked, cannot update"));
  }

  @Test
  void shouldGiveNotFoundWhenUpdatingTicketWhenTicketIsNotPresent() throws Exception {
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    mockMvc.perform(put("/ticket/10000")
        .content(mapper.writeValueAsBytes(updateTicketRequest))
        .header(HttpHeaders.CONTENT_TYPE, "application/json"))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("Ticket with id 10000 not found"));
  }

  @Test
  void shouldGetTheStatusOfTheTicket() throws Exception {
    Ticket ticket1 = ticketRepository.save(Ticket.from(List.of(new Line(1, 0, 1, 2))));

    MvcResult mvcResult = mockMvc.perform(get("/ticket/" + ticket1.getId() + "/status"))
      .andExpect(status().isOk())
      .andReturn();

    List<StatusResponse> statusResponses = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<StatusResponse>>() {
    });

    assertTrue(statusResponses
      .contains(new StatusResponse(1, 1)));
    assertTrue(ticketRepository.findById(ticket1.getId()).get().getChecked());
  }
}
