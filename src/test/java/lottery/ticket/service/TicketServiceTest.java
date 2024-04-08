package lottery.ticket.service;

import lottery.ticket.dto.request.CreateTicketRequest;
import lottery.ticket.dto.request.LineRequest;
import lottery.ticket.dto.request.UpdateTicketRequest;
import lottery.ticket.dto.response.LineResponse;
import lottery.ticket.dto.response.StatusResponse;
import lottery.ticket.dto.response.TicketResponse;
import lottery.ticket.entity.Line;
import lottery.ticket.entity.Ticket;
import lottery.ticket.exception.TicketNotFoundException;
import lottery.ticket.exception.TicketStatusCheckedException;
import lottery.ticket.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TicketServiceTest {
  private final TicketRepository ticketRepository = Mockito.mock(TicketRepository.class);
  private final TicketService ticketService = new TicketService(ticketRepository);

  @Test
  void shouldCreateTicket() {
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    CreateTicketRequest createTicketRequest = new CreateTicketRequest(List.of(lineRequest2, lineRequest1));

    ticketService.createTicket(createTicketRequest);

    ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
    verify(ticketRepository, times(1)).save(ticketArgumentCaptor.capture());
    assertFalse(ticketArgumentCaptor.getValue().getChecked());
    assertEquals(2, ticketArgumentCaptor.getValue().getLines().size());
  }

  @Test
  void shouldGetAllTicketsAlongWithResult() {
    when(ticketRepository.findAll())
      .thenReturn(List.of(new Ticket(1, false, List.of(new Line(1, 0, 1, 2),
          new Line(2, 0, 0, 0))),
        new Ticket(2, false, List.of(new Line(1, 0, 2, 2),
          new Line(2, 0, 0, 1)))));

    List<TicketResponse> ticketResponses = ticketService.getTickets();

    TicketResponse ticketResponse1 = new TicketResponse(1, List.of(new LineResponse(0, 1, 2, 1, 1),
      new LineResponse(0, 0, 0, 2, 5)));
    TicketResponse ticketResponse2 = new TicketResponse(2, List.of(new LineResponse(0, 0, 1, 2, 0),
      new LineResponse(0, 2, 2, 1, 1)));
    assertEquals(2, ticketResponses.size());
    assertEquals(List.of(1, 2), ticketResponses.stream().map(TicketResponse::id).collect(Collectors.toList()));
    assertTrue(ticketResponses.contains(ticketResponse1));
    assertTrue(ticketResponses.contains(ticketResponse2));
  }

  @Test
  void shouldGetEmptyTicketsIfNoTicketsArePresent() {
    when(ticketRepository.findAll())
      .thenReturn(List.of());

    List<TicketResponse> ticketResponses = ticketService.getTickets();

    assertEquals(0, ticketResponses.size());
  }

  @Test
  void shouldGetTicketForAGivenId() {
    when(ticketRepository.findById(1))
      .thenReturn(Optional.of(new Ticket(1, false, List.of(new Line(1, 0, 1, 2),
        new Line(2, 0, 0, 0)))));

    TicketResponse ticketResponse = ticketService.getTicket(1);

    TicketResponse ticketResponse1 = new TicketResponse(1, List.of(new LineResponse(0, 1, 2, 1, 1),
      new LineResponse(0, 0, 0, 2, 5)));
    assertEquals(ticketResponse1, ticketResponse);
  }

  @Test
  void shouldReturnEmptyForGetTicketIfIdIsNotThere() {
    when(ticketRepository.findById(10))
      .thenReturn(Optional.empty());

    assertThrows(TicketNotFoundException.class, () -> ticketService.getTicket(1));
  }

  @Test
  void shouldUpdateTicketWithNewLines() {
    when(ticketRepository.findById(1))
      .thenReturn(Optional.of(new Ticket(1, false, List.of(new Line(1, 0, 1, 2)))));
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    when(ticketRepository.findById(10))
      .thenReturn(Optional.empty());
    when(ticketRepository.save(any()))
      .thenReturn(new Ticket(1, false, List.of(new Line(1, 0, 1, 2),
        new Line(2, 0, 1, 2), new Line(3, 0, 0, 0))));

    TicketResponse ticketResponse = ticketService.updateTicket(1, updateTicketRequest);

    TicketResponse expectedResponse = new TicketResponse(1, List.of(new LineResponse(0, 1, 2, 1, 1),
      new LineResponse(0, 1, 2, 2, 1), new LineResponse(0, 0, 0, 3, 5)));

    assertEquals(expectedResponse, ticketResponse);
  }

  @Test
  void shouldThrowExceptionWhenUpdateTicketWithNotFoundTicket() {
    when(ticketRepository.findById(10))
      .thenReturn(Optional.empty());
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    assertThrows(TicketNotFoundException.class, () -> ticketService.updateTicket(10, updateTicketRequest));
  }

  @Test
  void shouldThrowExceptionWhenUpdateTicketWithAlreadyStatusCheckedTicket() {
    when(ticketRepository.findById(1))
      .thenReturn(Optional.of(new Ticket(1, true, List.of(new Line(1, 0, 1, 2)))));
    LineRequest lineRequest1 = new LineRequest(0, 1, 2);
    LineRequest lineRequest2 = new LineRequest(0, 0, 0);
    UpdateTicketRequest updateTicketRequest = new UpdateTicketRequest(List.of(lineRequest2, lineRequest1));

    assertThrows(TicketStatusCheckedException.class, () -> ticketService.updateTicket(1, updateTicketRequest));
  }

  @Test
  void shouldGetStatusOfTicket() {
    when(ticketRepository.findById(1))
      .thenReturn(Optional.of(new Ticket(1, false, List.of(new Line(1, 0, 1, 2)))));

    List<StatusResponse> statusResponses = ticketService.getStatusOfTicket(1);

    StatusResponse line1Response = new StatusResponse(1, 1);
    assertTrue(statusResponses.contains(line1Response));

    ArgumentCaptor<Ticket> ticketArgumentCaptor = ArgumentCaptor.forClass(Ticket.class);
    verify(ticketRepository, times(1)).save(ticketArgumentCaptor.capture());
    assertTrue(ticketArgumentCaptor.getValue().getChecked());
  }

  @Test
  void shouldThrowExceptionIfTicketIsNotFound() {
    when(ticketRepository.findById(10))
      .thenReturn(Optional.empty());

    assertThrows(TicketNotFoundException.class, () -> ticketService.getStatusOfTicket(10));
  }

}
