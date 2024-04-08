package lottery.ticket.controller;

import jakarta.validation.Valid;
import lottery.ticket.dto.request.CreateTicketRequest;
import lottery.ticket.dto.request.UpdateTicketRequest;
import lottery.ticket.dto.response.StatusResponse;
import lottery.ticket.dto.response.TicketResponse;
import lottery.ticket.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/ticket")
public class TicketController {
  private final TicketService ticketService;

  public TicketController(TicketService ticketService) {
    this.ticketService = ticketService;
  }

  @PostMapping("")
  @ResponseStatus(value = CREATED)
  public void createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest) {
    ticketService.createTicket(createTicketRequest);
  }

  @GetMapping("")
  public List<TicketResponse> getTickets() {
    return ticketService.getTickets();
  }

  @GetMapping("{id}")
  public TicketResponse getTicket(@PathVariable("id") Integer id) {
    return ticketService.getTicket(id);
  }

  @PutMapping("{id}")
  public TicketResponse updateTicket(@PathVariable("id") Integer id, @Valid @RequestBody UpdateTicketRequest updateTicketRequest) {
    return ticketService.updateTicket(id, updateTicketRequest);
  }

  @GetMapping("{id}/status")
  public List<StatusResponse> getStatusOfTicket(@PathVariable("id") Integer id) {
    return ticketService.getStatusOfTicket(id);
  }
}
