package lottery.ticket.dto.response;

import java.util.List;

public record TicketResponse(Integer id, List<LineResponse> lines) {
}
