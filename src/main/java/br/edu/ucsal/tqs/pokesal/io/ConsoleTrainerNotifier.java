package br.edu.ucsal.tqs.pokesal.io;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import java.io.PrintStream;

/**
 * Implementação de TrainerNotifier que escreve os alertas em um PrintStream, normalmente o
 * console.
 */
public class ConsoleTrainerNotifier implements TrainerNotifier {

  private final PrintStream out;

  /**
   * Cria um novo notificador que escreve no PrintStream informado.
   *
   * @param out o destino dos alertas
   * @throws NullPointerException se out for nulo
   */
  public ConsoleTrainerNotifier(PrintStream out) {
    if (out == null) {
      throw new NullPointerException("O PrintStream não pode ser nulo");
    }

    this.out = out;
  }

  @Override
  public void notifyCriticalHp(Trainer trainer, PokeSal pokeSal) {
    out.println("Atenção, " + trainer.getName() + ": " + pokeSal.getName()
        + " está com HP crítico (" + pokeSal.getHp() + "/" + pokeSal.getMaxHp() + ")!");
  }
}
