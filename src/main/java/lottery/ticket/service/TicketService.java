package lottery.ticket.service;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import lottery.ticket.dto.request.CreateTicketRequest;
import lottery.ticket.dto.request.UpdateTicketRequest;
import lottery.ticket.dto.response.LineResponse;
import lottery.ticket.dto.response.StatusResponse;
import lottery.ticket.dto.response.TicketResponse;
import lottery.ticket.entity.Line;
import lottery.ticket.entity.Ticket;
import lottery.ticket.exception.TicketNotFoundException;
import lottery.ticket.exception.TicketStatusCheckedException;
import lottery.ticket.repository.TicketRepository;
import lottery.ticket.service.calculation.DifferentValuesCalculation;
import lottery.ticket.service.calculation.LineResultCalculation;
import lottery.ticket.service.calculation.SameValuesCalculation;
import lottery.ticket.service.calculation.SumOfValuesCalculation;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class TicketService {
  private final TicketRepository ticketRepository;
  private final List<LineResultCalculation> resultCalculations;

  public TicketService(TicketRepository ticketRepository) {
    this.ticketRepository = ticketRepository;
    resultCalculations = List.of(new DifferentValuesCalculation(), new SameValuesCalculation(), new SumOfValuesCalculation());
  }

  public void createTicket(CreateTicketRequest createTicketRequest) {
    List<Line> lines = Stream
      .iterate(0, lineNumber -> lineNumber < createTicketRequest.lines().size(), lineNumber -> lineNumber + 1)
      .map(it -> new Line(it + 1, createTicketRequest.lines().get(it).value1(), createTicketRequest.lines().get(it).value2(),
        createTicketRequest.lines().get(it).value3()))
      .toList();
    Ticket ticket = Ticket.from(lines);
    ticketRepository.save(ticket);
  }

  public List<TicketResponse> getTickets() {
    return ticketRepository
      .findAll()
      .stream()
      .map(this::getTicketResponse)
      .toList();
  }

  public TicketResponse getTicket(Integer id) {
    return ticketRepository
      .findById(id)
      .map(this::getTicketResponse)
      .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + id + " not found"));
  }

  public TicketResponse updateTicket(Integer id, UpdateTicketRequest updateTicketRequest) {
    Ticket ticket = ticketRepository
      .findById(id)
      .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + id + " not found"));
    if (ticket.getChecked()) {
      throw new TicketStatusCheckedException("Ticket status already checked, cannot update");
    }
    int currentLines = ticket.getLines().size();
    List<Line> lines = Stream
      .iterate(0, lineNumber -> lineNumber < updateTicketRequest.newLines().size(), lineNumber -> lineNumber + 1)
      .map(it -> new Line(it + 1 + currentLines, updateTicketRequest.newLines().get(it).value1(), updateTicketRequest.newLines().get(it).value2(),
        updateTicketRequest.newLines().get(it).value3()))
      .toList();
    Ticket updatedTicket = ticketRepository.save(ticket.updateLines(lines));
    return getTicketResponse(updatedTicket);
  }

  private TicketResponse getTicketResponse(Ticket ticket) {
    List<LineResponse> lineResponses = ticket.getLines()
      .stream()
      .map(this::getLineResult)
      .map(it -> LineResponse.from(it._1, it._2))
      .sorted(Comparator.comparingInt(LineResponse::result))
      .toList();

    return new TicketResponse(ticket.getId(), lineResponses);
  }

  private Tuple2<Line, Integer> getLineResult(Line line) {
    Integer result = resultCalculations
      .stream()
      .filter(it -> it.matchedCondition(line))
      .findFirst()
      .map(LineResultCalculation::getResult)
      .orElse(0);
    return Tuple.of(line, result);
  }

  public List<StatusResponse> getStatusOfTicket(Integer id) {
    Ticket ticket = ticketRepository
      .findById(id)
      .orElseThrow(() -> new TicketNotFoundException("Ticket with id " + id + " not found"));
    List<StatusResponse> statusResponses = ticket
      .getLines()
      .stream()
      .map(this::getLineResult)
      .map(it -> new StatusResponse(it._2, it._1.getLineNumber()))
      .toList();
    ticketRepository.save(ticket.markChecked());
    return statusResponses;
  }
}
