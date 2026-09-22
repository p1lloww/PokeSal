package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.Turn;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;

/**
 * Caso de uso responsável por conduzir uma batalha do início ao fim, alternando entre capturar as
 * ações escolhidas pelos treinadores e executar os turnos, até que a batalha tenha um vencedor.
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
    int turnNumber = 1;

    while (!battle.isOver()) {
      battleCli.renderBattleState(battle);

      TurnAction action1 = battleCli.promptAction(battle.getTrainerA(), turnNumber);
      TurnAction action2 = battleCli.promptAction(battle.getTrainerB(), turnNumber);

      Turn turn = new Turn(turnNumber);
      turn.execute(battle.getTrainerA().getPokeSal(), battle.getTrainerB().getPokeSal(),
          action1, action2, battle.getBattleground());

      battle.addTurn(turn);
      battle.checkForWinner();

      turnNumber++;
    }

    battleCli.renderBattleState(battle);
    battleCli.announceWinner(battle.getWinner());
  }
}