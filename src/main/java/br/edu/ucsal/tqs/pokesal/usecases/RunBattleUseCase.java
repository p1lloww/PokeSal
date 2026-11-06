package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Turn;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;

/**
 * Caso de uso responsável por conduzir uma batalha do início ao fim, alternando entre capturar as
 * ações escolhidas pelos treinadores e executar os turnos, até que a batalha tenha um vencedor.
 * Também dispara os eventos de entrada em campo, no início da batalha, e de fim de turno, após cada
 * turno, para as passivas, condições de status, terreno e efeitos temporários. Ao fim da batalha,
 * o resultado é salvo no repositório; uma falha ao salvar é registrada, mas não invalida a batalha.
 */
public class RunBattleUseCase {

  private final BattleCli battleCli;
  private final BattlegroundService battlegroundService;
  private final ResultRepository resultRepository;
  private final BattleLogger battleLogger;

  /**
   * Cria um novo caso de uso de condução de batalha com os colaboradores informados.
   *
   * @param battleCli           a CLI usada para interação com os jogadores
   * @param battlegroundService o serviço que informa se o efeito do terreno está ativo
   * @param resultRepository    o repositório onde o resultado da batalha é salvo
   * @param battleLogger        o registro que recebe o início da batalha, os eventos de fim de
   *                            turno e os erros
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public RunBattleUseCase(BattleCli battleCli, BattlegroundService battlegroundService,
      ResultRepository resultRepository, BattleLogger battleLogger) {
    if (battleCli == null) {
      throw new NullPointerException("A CLI não pode ser nula");
    }
    if (battlegroundService == null) {
      throw new NullPointerException("O serviço de terreno não pode ser nulo");
    }
    if (resultRepository == null) {
      throw new NullPointerException("O repositório de resultados não pode ser nulo");
    }
    if (battleLogger == null) {
      throw new NullPointerException("O registro de batalha não pode ser nulo");
    }

    this.battleCli = battleCli;
    this.battlegroundService = battlegroundService;
    this.resultRepository = resultRepository;
    this.battleLogger = battleLogger;
  }

  /**
   * Executa a batalha informada até que um vencedor seja definido e salva o resultado.
   *
   * @param battle a batalha a ser conduzida
   */
  public void execute(Battle battle) {
    battleLogger.logBattleStart(battle.getTrainerA(), battle.getTrainerB());

    PokeSal pokeSalA = battle.getTrainerA().getPokeSal();
    PokeSal pokeSalB = battle.getTrainerB().getPokeSal();
    Battleground battleground = battle.getBattleground();

    pokeSalA.getPassive().onSwitchIn(pokeSalA, pokeSalB);
    pokeSalB.getPassive().onSwitchIn(pokeSalB, pokeSalA);

    int turnNumber = 1;

    while (!battle.isOver()) {
      battleCli.renderBattleState(battle);

      TurnAction action1 = battleCli.promptAction(battle.getTrainerA(), turnNumber);
      TurnAction action2 = battleCli.promptAction(battle.getTrainerB(), turnNumber);

      Turn turn = new Turn(turnNumber);
      turn.execute(pokeSalA, pokeSalB, action1, action2, battleground);

      battle.addTurn(turn);
      battle.checkForWinner();

      if (!battle.isOver()) {
        endTurn(pokeSalA, pokeSalB, battleground, turnNumber);
        endTurn(pokeSalB, pokeSalA, battleground, turnNumber);
        battle.checkForWinner();
      }

      turnNumber++;
    }

    saveResult(battle);

    battleCli.renderBattleState(battle);
    battleCli.announceWinner(battle.getWinner());
  }

  /**
   * Salva o resultado da batalha encerrada. Caso o repositório falhe, o erro é registrado e a
   * batalha continua valendo.
   *
   * @param battle a batalha encerrada
   */
  private void saveResult(Battle battle) {
    BattleResult result = BattleResult.from(battle);
    try {
      resultRepository.save(result);
    } catch (RuntimeException e) {
      battleLogger.logError("Não foi possível salvar o resultado da batalha", e);
    }
  }

  /**
   * Aplica os efeitos de fim de turno ao PokeSal informado: dano da condição de status, efeito da
   * passiva, efeito do terreno (apenas se o PokeSal ainda estiver de pé e o terreno estiver ativo)
   * e expiração dos efeitos temporários agendados para este turno. Caso o PokeSal perca HP nesta
   * etapa, a passiva é avisada do dano recebido.
   *
   * @param self         o PokeSal que está encerrando o turno
   * @param opponent     o PokeSal adversário
   * @param battleground o terreno ativo na batalha
   * @param turnNumber   o número do turno que está sendo encerrado
   */
  private void endTurn(PokeSal self, PokeSal opponent, Battleground battleground,
      int turnNumber) {
    int hpBefore = self.getHp();

    StatusCondition status = self.getStatusCondition();
    if (status != null) {
      status.onTurnEnd(self);
      int hpLostToStatus = hpBefore - self.getHp();
      if (hpLostToStatus > 0) {
        battleLogger.logStatusDamage(self, status, hpLostToStatus);
      }
    }
    self.getPassive().onTurnEnd(self);

    int hpLost = hpBefore - self.getHp();
    if (hpLost > 0) {
      self.getPassive().onDamageTaken(self, opponent, hpLost);
    }

    if (self.getHp() > 0 && battlegroundService.isActive(battleground)) {
      int hpBeforeBattleground = self.getHp();
      battleground.onTurnEnd(self);
      if (self.getHp() != hpBeforeBattleground) {
        battleLogger.logBattlegroundEffect(battleground, self);
      }
    }

    self.expireEffects(turnNumber);
  }
}
