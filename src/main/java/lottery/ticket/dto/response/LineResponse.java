package lottery.ticket.dto.response;

import lottery.ticket.entity.Line;

public record LineResponse(Integer value1, Integer value2, Integer value3, Integer lineNumber, Integer result) {

  public static LineResponse from(Line line, Integer result){
    return new LineResponse(line.getValue1(), line.getValue2(), line.getValue3(), line.getLineNumber(), result);
  }
}
