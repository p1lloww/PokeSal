package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Ação de turno que representa o uso de um item por um treinador, aplicando os
 * efeitos do item no próprio PokeSal do ator.
 */
public class ItemAction implements TurnAction {

  private final Item item;
  private final int currentTurnNumber;

  /**
   * Cria uma nova ação de uso de item com o item e o turno informados.
   *
   * @param item o item a ser usado
   * @param currentTurnNumber o número do turno atual, usado para calcular a
   *     expiração de efeitos temporários
   * @throws NullPointerException se item for nulo
   */
  public ItemAction(Item item, int currentTurnNumber) {
    if (item == null) {
      throw new NullPointerException("O item não pode ser nulo");
    }

    this.item = item;
    this.currentTurnNumber = currentTurnNumber;
  }

  @Override
  public void execute(PokeSal actor, PokeSal opponent, Battleground battleground) {
    item.useItem(actor, currentTurnNumber);
  }
}