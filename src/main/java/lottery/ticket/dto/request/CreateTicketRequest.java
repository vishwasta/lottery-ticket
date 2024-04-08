package lottery.ticket.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateTicketRequest(@NotEmpty List<LineRequest> lines) {
}
