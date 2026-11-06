package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.DamageResult;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;

/**
 * Ação de turno que representa o uso de um golpe (Move) por um PokeSal. Se o golpe acertar, aplica
 * dano ao oponente, avisa a passiva do oponente sobre o dano recebido e, com a chance definida pelo
 * golpe, aplica uma condição de status. Cada etapa é enviada ao BattleLogger na ordem: ataque,
 * dano, efeito de status e efeito de terreno.
 */
public class MoveAction implements TurnAction {

  private final Move move;
  private final DamageCalculatorService damageCalculatorService;
  private final RandomGenerator randomGenerator;
  private final BattleLogger battleLogger;

  /**
   * Cria uma nova ação de ataque com o golpe e os colaboradores informados.
   *
   * @param move                    o golpe a ser executado
   * @param damageCalculatorService o serviço usado para calcular o dano do golpe
   * @param randomGenerator         o gerador usado no sorteio da condição de status
   * @param battleLogger            o registro que recebe os eventos da ação
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public MoveAction(Move move, DamageCalculatorService damageCalculatorService,
      RandomGenerator randomGenerator, BattleLogger battleLogger) {
    if (move == null) {
      throw new NullPointerException("O golpe não pode ser nulo");
    }
    if (damageCalculatorService == null) {
      throw new NullPointerException("O serviço de dano não pode ser nulo");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("O gerador aleatório não pode ser nulo");
    }
    if (battleLogger == null) {
      throw new NullPointerException("O registro de batalha não pode ser nulo");
    }

    this.move = move;
    this.damageCalculatorService = damageCalculatorService;
    this.randomGenerator = randomGenerator;
    this.battleLogger = battleLogger;
  }

  @Override
  public void execute(PokeSal actor, PokeSal opponent, Battleground battleground) {
    battleLogger.logAttack(actor, opponent, move);

    DamageResult result =
        damageCalculatorService.calculateDamage(actor, opponent, move, battleground);
    if (!result.hit()) {
      battleLogger.logMiss(actor, move);
      return;
    }

    opponent.takeDamage(result.damage());
    battleLogger.logDamage(opponent, result.damage(), result.critical());
    opponent.getPassive().onDamageTaken(opponent, actor, result.damage());

    if (move.statusEffect() != null && opponent.getHp() > 0
        && randomGenerator.nextDouble() < move.statusChance()) {
      boolean hadStatus = opponent.getStatusCondition() != null;
      opponent.applyStatus(move.statusEffect());
      if (!hadStatus && opponent.getStatusCondition() != null) {
        battleLogger.logStatusEffect(opponent, opponent.getStatusCondition());
      }
    }

    if (result.battlegroundApplied()) {
      battleLogger.logBattlegroundEffect(battleground, opponent);
    }
  }
}
