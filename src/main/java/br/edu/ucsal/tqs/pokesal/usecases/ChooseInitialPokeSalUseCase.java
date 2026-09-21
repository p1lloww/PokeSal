package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;

/**
 * Caso de uso responsável por criar um treinador, capturando seu nome e a escolha de PokeSal
 * inicial através da CLI.
 */
public class ChooseInitialPokeSalUseCase {

  private final BattleCli battleCli;

  /**
   * Cria um novo caso de uso de escolha de PokeSal inicial, usando a CLI informada para capturar as
   * escolhas do jogador.
   *
   * @param battleCli a CLI usada para interação com o jogador
   */
  public ChooseInitialPokeSalUseCase(BattleCli battleCli) {
    this.battleCli = battleCli;
  }

  /**
   * Executa o caso de uso, criando um novo treinador a partir das escolhas capturadas pela CLI.
   *
   * @return o treinador criado
   */
  public Trainer execute() {
    String name = battleCli.promptTrainerName();
    PokeSal pokeSal = battleCli.promptInitialPokeSal();
    return new Trainer(name, pokeSal);
  }
}