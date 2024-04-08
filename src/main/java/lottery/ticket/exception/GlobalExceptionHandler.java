package lottery.ticket.exception;

import lombok.extern.slf4j.Slf4j;
import lottery.ticket.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler({Exception.class})
  public ResponseEntity<ErrorResponse> exception(Exception e) {
    log.error("Global exception handler - custom exception", e);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler({TicketNotFoundException.class})
  public ResponseEntity<ErrorResponse> notFound(Exception e) {
    log.error("Global exception handler - custom exception", e);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler({TicketStatusCheckedException.class})
  public ResponseEntity<ErrorResponse> badRequest(Exception e) {
    log.error("Global exception handler - custom exception", e);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<List<ErrorResponse>> handleValidationExceptions(
    MethodArgumentNotValidException ex) {
    var errors = ex.getBindingResult().getAllErrors().stream().map((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      return new ErrorResponse(fieldName + ":" + errorMessage);
    }).toList();
    return ResponseEntity.badRequest().body(errors);
  }

}
