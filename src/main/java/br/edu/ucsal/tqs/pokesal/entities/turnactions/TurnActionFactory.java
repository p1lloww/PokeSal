package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;

/**
 * Fábrica das ações de turno. Guarda os colaboradores que as ações precisam, para que quem escolhe
 * a ação (como a CLI) não precise conhecê-los.
 */
public class TurnActionFactory {

  private final DamageCalculatorService damageCalculatorService;
  private final RandomGenerator randomGenerator;
  private final BattleLogger battleLogger;

  /**
   * Cria uma nova fábrica com os colaboradores que serão repassados às ações criadas.
   *
   * @param damageCalculatorService o serviço de cálculo de dano usado pelas ações de ataque
   * @param randomGenerator         o gerador usado pelas ações de ataque
   * @param battleLogger            o registro que recebe os eventos das ações
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public TurnActionFactory(DamageCalculatorService damageCalculatorService,
      RandomGenerator randomGenerator, BattleLogger battleLogger) {
    if (damageCalculatorService == null) {
      throw new NullPointerException("O serviço de dano não pode ser nulo");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("O gerador aleatório não pode ser nulo");
    }
    if (battleLogger == null) {
      throw new NullPointerException("O registro de batalha não pode ser nulo");
    }

    this.damageCalculatorService = damageCalculatorService;
    this.randomGenerator = randomGenerator;
    this.battleLogger = battleLogger;
  }

  /**
   * Cria uma ação de ataque com o golpe informado.
   *
   * @param move o golpe a ser executado
   * @return a nova ação de ataque
   */
  public MoveAction createMoveAction(Move move) {
    return new MoveAction(move, damageCalculatorService, randomGenerator, battleLogger);
  }

  /**
   * Cria uma ação de uso de item.
   *
   * @param item              o item a ser usado
   * @param trainer           o treinador dono da mochila
   * @param currentTurnNumber o número do turno atual
   * @return a nova ação de uso de item
   */
  public ItemAction createItemAction(Item item, Trainer trainer, int currentTurnNumber) {
    return new ItemAction(item, trainer, currentTurnNumber, battleLogger);
  }
}
