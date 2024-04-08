package lottery.ticket.exception;

public class TicketStatusCheckedException extends RuntimeException{
  public TicketStatusCheckedException(String message) {
    super(message);
  }
}
