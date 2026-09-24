package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import java.util.Random;

/**
 * Ação de turno que representa o uso de um golpe (Move) por um PokeSal, aplicando
 * dano ao oponente, avisando a passiva do oponente sobre o dano recebido e, com a chance
 * definida pelo golpe, aplicando uma condição de status.
 */
public class MoveAction implements TurnAction {

  private static final Random RANDOM = new Random();

  private final Move move;

  /**
   * Cria uma nova ação de ataque com o golpe informado.
   *
   * @param move o golpe a ser executado
   * @throws NullPointerException se move for nulo
   */
  public MoveAction(Move move) {
    if (move == null) {
      throw new NullPointerException("O golpe não pode ser nulo");
    }

    this.move = move;
  }

  @Override
  public void execute(PokeSal actor, PokeSal opponent, Battleground battleground) {
    double damage = DamageCalculatorService.calculateDamage(actor, opponent, move, battleground);
    opponent.takeDamage(damage);
    opponent.getPassive().onDamageTaken(opponent, actor, damage);

    if (move.statusEffect() != null && opponent.getHp() > 0
        && RANDOM.nextDouble() < move.statusChance()) {
      opponent.applyStatus(move.statusEffect());
    }
  }
}