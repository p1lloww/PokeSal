package br.edu.ucsal.tqs.pokesal.entities;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;

/**
 * Representa um turno de batalha, responsável por decidir a ordem de ação entre os dois PokeSal com
 * base em seu SPD e executar as ações escolhidas por cada um, na ordem determinada. Não conhece os
 * detalhes de implementação de cada ação — apenas delega a execução para a TurnAction
 * correspondente.
 */
public class Turn {

  private final int turnNumber;
  private PokeSal firstActor;
  private PokeSal secondActor;
  private TurnAction firstAction;
  private TurnAction secondAction;

  /**
   * Cria um novo turno com o número informado. A ordem de ação e as ações executadas só são
   * definidas ao chamar {@link #execute}.
   *
   * @param turnNumber o número deste turno na batalha
   */
  public Turn(int turnNumber) {
    this.turnNumber = turnNumber;
  }

  /**
   * Executa este turno: decide qual PokeSal age primeiro com base no SPD atual de cada um, executa
   * a ação do primeiro e, caso o segundo ainda esteja vivo, executa a ação do segundo em seguida.
   *
   * @param pokeSal1     o primeiro PokeSal participante do turno
   * @param pokeSal2     o segundo PokeSal participante do turno
   * @param action1      a ação escolhida para pokeSal1
   * @param action2      a ação escolhida para pokeSal2
   * @param battleground o terreno ativo na batalha, usado no cálculo das ações
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public void execute(PokeSal pokeSal1, PokeSal pokeSal2, TurnAction action1,
      TurnAction action2, Battleground battleground) {
    if (pokeSal1 == null || pokeSal2 == null) {
      throw new NullPointerException("Os PokeSal não podem ser nulos");
    }
    if (action1 == null || action2 == null) {
      throw new NullPointerException("As ações não podem ser nulas");
    }

    if (pokeSal1.getSpd() >= pokeSal2.getSpd()) {
      this.firstActor = pokeSal1;
      this.firstAction = action1;
      this.secondActor = pokeSal2;
      this.secondAction = action2;
    } else {
      this.firstActor = pokeSal2;
      this.firstAction = action2;
      this.secondActor = pokeSal1;
      this.secondAction = action1;
    }

    firstAction.execute(firstActor, secondActor, battleground);

    if (secondActor.getHp() > 0) {
      secondAction.execute(secondActor, firstActor, battleground);
    }
  }

  public int getTurnNumber() {
    return turnNumber;
  }

  public PokeSal getFirstActor() {
    return firstActor;
  }

  public PokeSal getSecondActor() {
    return secondActor;
  }

  public TurnAction getFirstAction() {
    return firstAction;
  }

  public TurnAction getSecondAction() {
    return secondAction;
  }
}