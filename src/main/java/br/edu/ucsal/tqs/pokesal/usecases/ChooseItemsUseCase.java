package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;

/**
 * Caso de uso responsável por capturar, via CLI, a escolha de itens de mochila de um
 * treinador antes do início da batalha, respeitando o limite de dois itens.
 */
public class ChooseItemsUseCase {

  private final BattleCli battleCli;

  /**
   * Cria um novo caso de uso de escolha de itens, usando a CLI informada.
   *
   * @param battleCli a CLI usada para interação com o jogador
   */
  public ChooseItemsUseCase(BattleCli battleCli) {
    this.battleCli = battleCli;
  }

  /**
   * Executa o caso de uso, adicionando à mochila do treinador os itens escolhidos
   * pelo jogador via CLI.
   *
   * @param trainer o treinador que vai escolher os itens
   */
  public void execute(Trainer trainer) {
    boolean wantsMoreItems = true;

    while (wantsMoreItems && trainer.getBackpack().size() < Trainer.MAX_ITEMS) {
      Item chosenItem = battleCli.promptItemSelection(trainer.getBackpack().size());
      if (chosenItem == null) {
        wantsMoreItems = false;
      } else {
        trainer.addItem(chosenItem);
      }
    }
  }
}