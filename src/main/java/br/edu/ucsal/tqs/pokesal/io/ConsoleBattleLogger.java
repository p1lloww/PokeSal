package br.edu.ucsal.tqs.pokesal.io;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import java.io.PrintStream;

/**
 * Implementação de BattleLogger que escreve os eventos da batalha em um PrintStream, normalmente o
 * console.
 */
public class ConsoleBattleLogger implements BattleLogger {

  private final PrintStream out;

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
  public void logAttack(PokeSal attacker, PokeSal defender, Move move) {
    out.println(attacker.getName() + " usou " + move.name() + " em " + defender.getName() + "!");
  }

  @Override
  public void logDamage(PokeSal defender, double damage, boolean critical) {
    if (critical) {
      out.println("Acerto crítico!");
    }
    out.printf("%s recebeu %.1f de dano.%n", defender.getName(), damage);
  }

  @Override
  public void logMiss(PokeSal attacker, Move move) {
    out.println(move.name() + " de " + attacker.getName() + " errou o alvo!");
  }

  @Override
  public void logStatusEffect(PokeSal target, StatusCondition status) {
    out.println(target.getName() + " está sob efeito de " + status.getName() + ".");
  }

  @Override
  public void logBattlegroundEffect(Battleground battleground, PokeSal target) {
    out.println("O terreno " + battleground.getName() + " afetou " + target.getName() + ".");
  }

  @Override
  public void logItemUse(PokeSal target, Item item) {
    out.println(target.getName() + " recebeu " + item.getName() + ".");
  }

  @Override
  public void logError(String message, Exception cause) {
    out.println("[ERRO] " + message + ": " + cause.getMessage());
  }
}
