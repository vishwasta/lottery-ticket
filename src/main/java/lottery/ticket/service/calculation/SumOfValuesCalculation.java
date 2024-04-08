package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;

public class SumOfValuesCalculation implements LineResultCalculation {
  @Override
  public Integer getResult() {
    return 10;
  }

  @Override
  public Boolean matchedCondition(Line line) {
    Integer sum = line.getValue3() + line.getValue1() + line.getValue2();
    return sum == 2;
  }
}
