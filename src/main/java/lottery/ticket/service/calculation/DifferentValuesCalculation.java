package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;

public class DifferentValuesCalculation implements LineResultCalculation {
  @Override
  public Integer getResult() {
    return 1;
  }

  @Override
  public Boolean matchedCondition(Line line) {
    return (line.getValue2() != line.getValue1()) && (line.getValue3() != line.getValue1());
  }
}
