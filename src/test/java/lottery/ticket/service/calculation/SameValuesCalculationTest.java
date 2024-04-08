package lottery.ticket.service.calculation;

import lottery.ticket.entity.Line;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SameValuesCalculationTest {
  private SameValuesCalculation sameValuesCalculation = new SameValuesCalculation();

  @Test
  void shouldGetResultAs5() {
    assertEquals(5, sameValuesCalculation.getResult());
  }

  @Test
  void shouldReturnTrueIfAllValuesAreSame() {
    Line line = new Line(1, 1, 1, 1);
    assertTrue(sameValuesCalculation.matchedCondition(line));
  }

  @Test
  void shouldReturnTrueIfAllValuesAreNotSame() {
    Line line = new Line(1, 1, 1, 2);
    assertFalse(sameValuesCalculation.matchedCondition(line));
  }
}
