package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;

public class SameValuesCalculation implements LineResultCalculation {
  @Override
  public Integer getResult() {
    return 5;
  }

  @Override
  public Boolean matchedCondition(Line line) {
    return line.getAllValues().stream().distinct().count() == 1;
  }
}
