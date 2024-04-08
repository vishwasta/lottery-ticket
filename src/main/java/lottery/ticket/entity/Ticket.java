package lottery.ticket.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Entity(name = "ticket")
@Table(name = "ticket")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Ticket {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer id;
  private Boolean checked;
  @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<Line> lines = new ArrayList<>();

  public static Ticket from(List<Line> lines) {
    Ticket ticket = builder()
      .lines(lines)
      .checked(false)
      .build();
    lines.forEach(line -> line.setTicket(ticket));
    return ticket;
  }

  public Ticket updateLines(List<Line> lines) {
    List<Line> updatedLines = new ArrayList<>();
    updatedLines.addAll(getLines());
    updatedLines.addAll(lines);
    Ticket ticket = this.copy(tick -> {
      tick.lines = updatedLines;
      return tick;
    });
    lines
      .forEach(line -> line.setTicket(ticket));
    return ticket;
  }

  public Ticket markChecked() {
    return this.copy(tick -> {
      tick.checked = true;
      return tick;
    });
  }

  public Ticket copy(UnaryOperator<Ticket> ticketToBeCopied) {
    return ticketToBeCopied.apply(new Ticket(
      this.id,
      this.checked,
      this.lines));
  }

}
