package br.edu.ucsal.tqs.pokesal;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.BattlegroundCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnActionFactory;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import br.edu.ucsal.tqs.pokesal.io.ConsoleBattleLogger;
import br.edu.ucsal.tqs.pokesal.io.ConsoleTrainerNotifier;
import br.edu.ucsal.tqs.pokesal.repositories.InMemoryResultRepository;
import br.edu.ucsal.tqs.pokesal.repositories.ResultRepository;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.battleground.DefaultBattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.battleground.RandomWeatherProvider;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.notification.TrainerNotifier;
import br.edu.ucsal.tqs.pokesal.services.random.DefaultRandomGenerator;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import br.edu.ucsal.tqs.pokesal.usecases.ChooseInitialPokeSalUseCase;
import br.edu.ucsal.tqs.pokesal.usecases.ChooseItemsUseCase;
import br.edu.ucsal.tqs.pokesal.usecases.RunBattleUseCase;
import java.time.Clock;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação, responsável por montar as dependências e iniciar uma batalha
 * completa via linha de comando. É o único lugar onde as implementações concretas dos
 * colaboradores (gerador aleatório, relógio, serviço de terreno, repositório e registro) são
 * criadas.
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
    RandomGenerator randomGenerator = new DefaultRandomGenerator(new Random());
    Clock clock = Clock.systemDefaultZone();
    BattlegroundService battlegroundService = new DefaultBattlegroundService(
        clock, new RandomWeatherProvider(randomGenerator));
    BattleLogger battleLogger = new ConsoleBattleLogger(System.out);

    DamageCalculatorService damageCalculatorService =
        new DamageCalculatorService(randomGenerator, battlegroundService);
    TurnActionFactory turnActionFactory =
        new TurnActionFactory(damageCalculatorService, randomGenerator, battleLogger);

    Scanner scanner = new Scanner(System.in);
    BattleCli battleCli = new BattleCli(scanner, turnActionFactory);

    ChooseInitialPokeSalUseCase chooseInitialPokeSalUseCase =
        new ChooseInitialPokeSalUseCase(battleCli);
    ChooseItemsUseCase chooseItemsUseCase = new ChooseItemsUseCase(battleCli);

    System.out.println("Treinador 1:");
    Trainer trainerA = chooseInitialPokeSalUseCase.execute();
    chooseItemsUseCase.execute(trainerA);

    System.out.println("Treinador 2:");
    Trainer trainerB = chooseInitialPokeSalUseCase.execute();
    chooseItemsUseCase.execute(trainerB);

    Battle battle = new Battle(trainerA, trainerB,
        BattlegroundCatalog.randomBattleground(randomGenerator));

    ResultRepository resultRepository = new InMemoryResultRepository();
    TrainerNotifier trainerNotifier = new ConsoleTrainerNotifier(System.out);
    RunBattleUseCase runBattleUseCase = new RunBattleUseCase(battleCli, battlegroundService,
        resultRepository, battleLogger, trainerNotifier, clock);
    runBattleUseCase.execute(battle);
  }
}