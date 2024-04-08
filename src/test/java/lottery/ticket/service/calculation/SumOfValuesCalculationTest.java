package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SumOfValuesCalculationTest {
  private final SumOfValuesCalculation sumOfValuesCalculation = new SumOfValuesCalculation();

  @Test
  void shouldGetResultAs10() {
    assertEquals(10, sumOfValuesCalculation.getResult());
  }

  @Test
  void shouldReturnTrueIfSumOfValuesIs2() {
    Line line = new Line(1, 0, 1, 1);
    assertTrue(sumOfValuesCalculation.matchedCondition(line));
  }

  @Test
  void shouldReturnFalseIfSumOfValuesIsNot2() {
    Line line = new Line(1, 0, 2, 1);
    assertFalse(sumOfValuesCalculation.matchedCondition(line));
  }
}
