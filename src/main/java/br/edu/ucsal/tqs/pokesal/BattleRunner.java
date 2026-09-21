package br.edu.ucsal.tqs.pokesal;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.BattlegroundCatalog;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.usecases.ChooseInitialPokeSalUseCase;
import br.edu.ucsal.tqs.pokesal.usecases.RunBattleUseCase;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação, responsável por montar as dependências e iniciar
 * uma batalha completa via linha de comando.
 */
public final class BattleRunner {

  private BattleRunner() {
  }

  /**
   * Inicia a aplicação.
   *
   * @param args argumentos de linha de comando, não utilizados
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    BattleCli battleCli = new BattleCli(scanner);

    ChooseInitialPokeSalUseCase chooseInitialPokeSalUseCase =
        new ChooseInitialPokeSalUseCase(battleCli);

    System.out.println("Treinador 1:");
    Trainer trainerA = chooseInitialPokeSalUseCase.execute();

    System.out.println("Treinador 2:");
    Trainer trainerB = chooseInitialPokeSalUseCase.execute();

    Battle battle = new Battle(trainerA, trainerB, BattlegroundCatalog.randomBattleground());

    RunBattleUseCase runBattleUseCase = new RunBattleUseCase(battleCli);
    runBattleUseCase.execute(battle);
  }
}