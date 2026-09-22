package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Ação de turno que representa o uso de um item por um treinador, aplicando os efeitos do item no
 * próprio PokeSal do ator e removendo o item da mochila do treinador após o uso.
 */
public class ItemAction implements TurnAction {

  private final Item item;
  private final Trainer trainer;
  private final int currentTurnNumber;

  /**
   * Cria uma nova ação de uso de item com o item, o treinador e o turno informados.
   *
   * @param item              o item a ser usado
   * @param trainer           o treinador dono da mochila da qual o item será removido
   * @param currentTurnNumber o número do turno atual, usado para calcular a expiração de efeitos
   *                          temporários
   * @throws NullPointerException se item ou trainer forem nulos
   */
  public ItemAction(Item item, Trainer trainer, int currentTurnNumber) {
    if (item == null) {
      throw new NullPointerException("O item não pode ser nulo");
    }
    if (trainer == null) {
      throw new NullPointerException("O treinador não pode ser nulo");
    }

    this.item = item;
    this.trainer = trainer;
    this.currentTurnNumber = currentTurnNumber;
  }

  /**
   * Executa esta ação: aplica os efeitos do item ao PokeSal ator e remove o item da mochila do
   * treinador.
   *
   * @param actor        o PokeSal que recebe os efeitos do item
   * @param opponent     o PokeSal adversário no turno; não utilizado por esta ação
   * @param battleground o terreno ativo na batalha; não utilizado por esta ação
   */
  @Override
  public void execute(PokeSal actor, PokeSal opponent, Battleground battleground) {
    item.useItem(actor, currentTurnNumber);
    trainer.removeItem(item);
  }
}