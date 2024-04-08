package lottery.ticket.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import static lottery.ticket.utils.Constants.MAX_VALUE_MESSAGE;
import static lottery.ticket.utils.Constants.MIN_VALUE_MESSAGE;

public record LineRequest(
  @Min(value = 0, message = MIN_VALUE_MESSAGE) @Max(value = 2, message = MAX_VALUE_MESSAGE) Integer value1,
  @Min(value = 0, message = MIN_VALUE_MESSAGE) @Max(value = 2, message = MAX_VALUE_MESSAGE) Integer value2,
  @Min(value = 0, message = MIN_VALUE_MESSAGE) @Max(value = 2, message = MAX_VALUE_MESSAGE) Integer value3) {

}
