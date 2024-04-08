package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;

public interface LineResultCalculation {

  public Integer getResult();

  Boolean matchedCondition(Line line);
}
