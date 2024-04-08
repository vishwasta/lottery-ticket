package lottery.ticket.dto.request;

import java.util.List;

public record UpdateTicketRequest(List<LineRequest> newLines) {
}
