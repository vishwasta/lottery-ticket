package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DifferentValuesCalculationTest {
  private DifferentValuesCalculation differentValuesCalculation = new DifferentValuesCalculation();

  @Test
  void shouldGetResultAs1() {
    assertEquals(1, differentValuesCalculation.getResult());
  }

  @Test
  void shouldReturnTrueIfSecondAndThirdAreDifferentFromFirst() {
    Line line = new Line(1, 0, 1, 1);
    assertTrue(differentValuesCalculation.matchedCondition(line));
  }

  @Test
  void shouldReturnFalseIfSecondIsSameAsFirst() {
    Line line = new Line(1, 0, 0, 1);
    assertFalse(differentValuesCalculation.matchedCondition(line));
  }

  @Test
  void shouldReturnFalseIfThirdIsSameAsFirst() {
    Line line = new Line(1, 0, 1, 0);
    assertFalse(differentValuesCalculation.matchedCondition(line));
  }
}
