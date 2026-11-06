package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.Turn;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Caso de uso responsável por conduzir uma batalha do início ao fim, alternando entre capturar as
 * ações escolhidas pelos treinadores e executar os turnos, até que a batalha tenha um vencedor.
 * Também dispara os eventos de entrada em campo, no início da batalha, e de fim de turno, após cada
 * turno, para as passivas, condições de status, terreno e efeitos temporários. Ao fim da batalha,
 * o resultado é salvo no repositório; uma falha ao salvar é registrada, mas não invalida a batalha.
 *
 * <p>Cada treinador tem um tempo limite para escolher a ação do turno, medido pelo relógio
 * recebido no construtor; quem passar do limite perde a vez naquele turno. Quando o HP de um
 * PokeSal fica abaixo de 25% do máximo, o treinador recebe um alerta, uma única vez por batalha.
 */
public class RunBattleUseCase {

  /** Tempo máximo para um treinador escolher a ação do turno. */
  public static final Duration ACTION_TIME_LIMIT = Duration.ofSeconds(30);

  /** Fração do HP máximo abaixo da qual o HP do PokeSal é considerado crítico. */
  public static final double CRITICAL_HP_RATIO = 0.25;

  private static final TurnAction SKIPPED_ACTION = (actor, opponent, battleground) -> { };

  private final BattleCli battleCli;
  private final BattlegroundService battlegroundService;
  private final ResultRepository resultRepository;
  private final BattleLogger battleLogger;
  private final TrainerNotifier trainerNotifier;
  private final Clock clock;

  /**
   * Cria um novo caso de uso de condução de batalha com os colaboradores informados.
   *
   * @param battleCli           a CLI usada para interação com os jogadores
   * @param battlegroundService o serviço que informa se o efeito do terreno está ativo
   * @param resultRepository    o repositório onde o resultado da batalha é salvo
   * @param battleLogger        o registro que recebe o início da batalha, os eventos de fim de
   *                            turno e os erros
   * @param trainerNotifier     o canal usado para enviar os alertas de HP crítico
   * @param clock               o relógio usado para medir o tempo de escolha das ações
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public RunBattleUseCase(BattleCli battleCli, BattlegroundService battlegroundService,
      ResultRepository resultRepository, BattleLogger battleLogger,
      TrainerNotifier trainerNotifier, Clock clock) {
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
    if (trainerNotifier == null) {
      throw new NullPointerException("O notificador de treinador não pode ser nulo");
    }
    if (clock == null) {
      throw new NullPointerException("O relógio não pode ser nulo");
    }

    this.battleCli = battleCli;
    this.battlegroundService = battlegroundService;
    this.resultRepository = resultRepository;
    this.battleLogger = battleLogger;
    this.trainerNotifier = trainerNotifier;
    this.clock = clock;
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
    Set<PokeSal> alertedPokeSals = new HashSet<>();

    while (!battle.isOver()) {
      battleCli.renderBattleState(battle);

      TurnAction action1 = promptActionWithinTimeLimit(battle.getTrainerA(), turnNumber);
      TurnAction action2 = promptActionWithinTimeLimit(battle.getTrainerB(), turnNumber);

      Turn turn = new Turn(turnNumber);
      turn.execute(pokeSalA, pokeSalB, action1, action2, battleground);

      battle.addTurn(turn);
      battle.checkForWinner();
      alertCriticalHp(battle, alertedPokeSals);

      if (!battle.isOver()) {
        endTurn(pokeSalA, pokeSalB, battleground, turnNumber);
        endTurn(pokeSalB, pokeSalA, battleground, turnNumber);
        battle.checkForWinner();
        alertCriticalHp(battle, alertedPokeSals);
      }

      turnNumber++;
    }

    saveResult(battle);

    battleCli.renderBattleState(battle);
    battleCli.announceWinner(battle.getWinner());
  }

  /**
   * Pede a ação do turno ao treinador e mede quanto tempo ele levou para escolher. Se passar do
   * tempo limite, a ação escolhida é descartada e o treinador perde a vez.
   *
   * @param trainer    o treinador que vai escolher a ação
   * @param turnNumber o número do turno atual
   * @return a ação escolhida, ou uma ação que não faz nada se o tempo limite foi excedido
   */
  private TurnAction promptActionWithinTimeLimit(Trainer trainer, int turnNumber) {
    Instant start = clock.instant();
    TurnAction action = battleCli.promptAction(trainer, turnNumber);
    Duration elapsed = Duration.between(start, clock.instant());

    if (elapsed.compareTo(ACTION_TIME_LIMIT) > 0) {
      battleLogger.logActionTimeout(trainer);
      return SKIPPED_ACTION;
    }
    return action;
  }

  /**
   * Envia o alerta de HP crítico aos treinadores cujo PokeSal está abaixo do limite e ainda não
   * foi alertado nesta batalha. Nada é enviado se a batalha já terminou.
   *
   * @param battle          a batalha em andamento
   * @param alertedPokeSals os PokeSal que já receberam o alerta nesta batalha
   */
  private void alertCriticalHp(Battle battle, Set<PokeSal> alertedPokeSals) {
    if (battle.isOver()) {
      return;
    }

    alertCriticalHp(battle.getTrainerA(), alertedPokeSals);
    alertCriticalHp(battle.getTrainerB(), alertedPokeSals);
  }

  private void alertCriticalHp(Trainer trainer, Set<PokeSal> alertedPokeSals) {
    PokeSal pokeSal = trainer.getPokeSal();
    boolean critical = pokeSal.getHp() > 0
        && pokeSal.getHp() < pokeSal.getMaxHp() * CRITICAL_HP_RATIO;
    if (!critical || !alertedPokeSals.add(pokeSal)) {
      return;
    }

    try {
      trainerNotifier.notifyCriticalHp(trainer, pokeSal);
    } catch (RuntimeException e) {
      battleLogger.logError("Não foi possível enviar o alerta de HP crítico", e);
    }
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
