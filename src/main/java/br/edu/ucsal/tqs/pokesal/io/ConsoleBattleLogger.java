package br.edu.ucsal.tqs.pokesal.io;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Implementação de BattleLogger que escreve os eventos da batalha em um PrintStream, normalmente o
 * console. A partir do início da batalha, cada PokeSal é exibido junto com o nome do seu treinador,
 * para diferenciar dois PokeSal da mesma espécie.
 */
public class ConsoleBattleLogger implements BattleLogger {

  private final PrintStream out;
  private final Map<PokeSal, String> trainerNames = new IdentityHashMap<>();

  /**
   * Cria um novo registro que escreve no PrintStream informado.
   *
   * @param out o destino das mensagens
   * @throws NullPointerException se out for nulo
   */
  public ConsoleBattleLogger(PrintStream out) {
    if (out == null) {
      throw new NullPointerException("O PrintStream não pode ser nulo");
    }

    this.out = out;
  }

  @Override
  public void logBattleStart(Trainer trainerA, Trainer trainerB) {
    trainerNames.clear();
    trainerNames.put(trainerA.getPokeSal(), trainerA.getName());
    trainerNames.put(trainerB.getPokeSal(), trainerB.getName());
    out.println("A batalha entre " + trainerA.getName() + " e " + trainerB.getName()
        + " começou!");
  }

  @Override
  public void logActionTimeout(Trainer trainer) {
    out.println(trainer.getName() + " excedeu o tempo para escolher a ação e perdeu a vez!");
  }

  @Override
  public void logAttack(PokeSal attacker, PokeSal defender, Move move) {
    out.println(label(attacker) + " usou " + move.name() + " em " + label(defender) + "!");
  }

  @Override
  public void logDamage(PokeSal defender, double damage, boolean critical) {
    if (critical) {
      out.println("Acerto crítico!");
    }
    out.printf("%s recebeu %.1f de dano.%n", label(defender), damage);
  }

  @Override
  public void logMiss(PokeSal attacker, Move move) {
    out.println(move.name() + " de " + label(attacker) + " errou o alvo!");
  }

  @Override
  public void logStatusEffect(PokeSal target, StatusCondition status) {
    out.println(label(target) + " foi afetado por " + status.getName() + "!");
  }

  @Override
  public void logStatusDamage(PokeSal target, StatusCondition status, int hpLost) {
    out.println(label(target) + " perdeu " + hpLost + " de HP por causa de " + status.getName()
        + ".");
  }

  @Override
  public void logBattlegroundEffect(Battleground battleground, PokeSal target) {
    out.println("O terreno " + battleground.getName() + " afetou " + label(target) + ".");
  }

  @Override
  public void logItemUse(PokeSal target, Item item) {
    out.println(label(target) + " recebeu " + item.getName() + ".");
  }

  @Override
  public void logError(String message, Exception cause) {
    out.println("[ERRO] " + message + ": " + cause.getMessage());
  }

  private String label(PokeSal pokeSal) {
    String trainerName = trainerNames.get(pokeSal);
    if (trainerName == null) {
      return pokeSal.getName();
    }
    return pokeSal.getName() + " (" + trainerName + ")";
  }
}
