package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Turn;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;

/**
 * Caso de uso responsável por conduzir uma batalha do início ao fim, alternando entre capturar as
 * ações escolhidas pelos treinadores e executar os turnos, até que a batalha tenha um vencedor.
 * Também dispara os eventos de entrada em campo, no início da batalha, e de fim de turno, após cada
 * turno, para as passivas, condições de status, terreno e efeitos temporários.
 */
public class RunBattleUseCase {

  private final BattleCli battleCli;

  /**
   * Cria um novo caso de uso de condução de batalha, usando a CLI informada para capturar as
   * escolhas dos jogadores e exibir o estado da batalha.
   *
   * @param battleCli a CLI usada para interação com os jogadores
   */
  public RunBattleUseCase(BattleCli battleCli) {
    this.battleCli = battleCli;
  }

  /**
   * Executa a batalha informada até que um vencedor seja definido.
   *
   * @param battle a batalha a ser conduzida
   */
  public void execute(Battle battle) {
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

    battleCli.renderBattleState(battle);
    battleCli.announceWinner(battle.getWinner());
  }

  /**
   * Aplica os efeitos de fim de turno ao PokeSal informado: dano da condição de status, efeito da
   * passiva, cura do terreno (apenas se o PokeSal ainda estiver de pé) e expiração dos efeitos
   * temporários agendados para este turno. Caso o PokeSal perca HP nesta etapa, a passiva é avisada
   * do dano recebido.
   *
   * @param self         o PokeSal que está encerrando o turno
   * @param opponent     o PokeSal adversário
   * @param battleground o terreno ativo na batalha
   * @param turnNumber   o número do turno que está sendo encerrado
   */
  private void endTurn(PokeSal self, PokeSal opponent, Battleground battleground,
      int turnNumber) {
    int hpBefore = self.getHp();

    if (self.getStatusCondition() != null) {
      self.getStatusCondition().onTurnEnd(self);
    }
    self.getPassive().onTurnEnd(self);

    int hpLost = hpBefore - self.getHp();
    if (hpLost > 0) {
      self.getPassive().onDamageTaken(self, opponent, hpLost);
    }

    if (self.getHp() > 0) {
      battleground.onTurnEnd(self);
    }

    self.expireEffects(turnNumber);
  }
}
