package lottery.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Line {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;
  private Integer lineNumber;
  private Integer value1;
  private Integer value2;
  private Integer value3;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "ticket_id", referencedColumnName = "id", nullable = false)
  private Ticket ticket;

  public Line(Integer lineNumber,Integer value1, Integer value2, Integer value3) {
    this.lineNumber = lineNumber;
    this.value1 = value1;
    this.value2 = value2;
    this.value3 = value3;
  }

  public void setTicket(Ticket ticket) {
    this.ticket = ticket;
  }



  public List<Integer> getAllValues(){
    return List.of(this.value1, this.value2, this.value3);
  }
}
