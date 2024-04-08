package lottery.ticket.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

import static lottery.ticket.utils.Constants.LINES_NOT_EMPTY_MESSAGE;

public record UpdateTicketRequest(@NotEmpty(message = LINES_NOT_EMPTY_MESSAGE) List<LineRequest> newLines) {
}
