package br.edu.ucsal.tqs.pokesal;

import br.edu.ucsal.tqs.pokesal.entities.Trainer;
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
import br.edu.ucsal.tqs.pokesal.usecases.DefaultMatchRunner;
import br.edu.ucsal.tqs.pokesal.usecases.MatchRunner;
import br.edu.ucsal.tqs.pokesal.usecases.RunBattleUseCase;
import br.edu.ucsal.tqs.pokesal.usecases.RunTournamentUseCase;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Ponto de entrada da aplicação, responsável por montar as dependências e iniciar uma batalha
 * simples ou um torneio via linha de comando. É o único lugar onde as implementações concretas dos
 * colaboradores (gerador aleatório, relógio, serviço de terreno, repositório, registro e
 * notificador) são criadas.
 */
public final class BattleRunner {

  private static final int TOURNAMENT_MODE = 2;
  private static final int SINGLE_BATTLE_TRAINERS = 2;

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

    boolean tournament = battleCli.promptGameMode() == TOURNAMENT_MODE;
    int trainerCount = tournament ? battleCli.promptTournamentSize() : SINGLE_BATTLE_TRAINERS;
    List<Trainer> trainers = registerTrainers(battleCli, trainerCount);

    ResultRepository resultRepository = new InMemoryResultRepository();
    TrainerNotifier trainerNotifier = new ConsoleTrainerNotifier(System.out);
    RunBattleUseCase runBattleUseCase = new RunBattleUseCase(battleCli, battlegroundService,
        resultRepository, battleLogger, trainerNotifier, clock);
    MatchRunner matchRunner = new DefaultMatchRunner(runBattleUseCase, randomGenerator);

    if (tournament) {
      new RunTournamentUseCase(matchRunner, battleCli).execute(trainers);
    } else {
      matchRunner.run(trainers.get(0), trainers.get(1));
    }
  }

  private static List<Trainer> registerTrainers(BattleCli battleCli, int trainerCount) {
    ChooseInitialPokeSalUseCase chooseInitialPokeSalUseCase =
        new ChooseInitialPokeSalUseCase(battleCli);
    ChooseItemsUseCase chooseItemsUseCase = new ChooseItemsUseCase(battleCli);

    List<Trainer> trainers = new ArrayList<>();
    for (int i = 1; i <= trainerCount; i++) {
      System.out.println("Treinador " + i + ":");
      Trainer trainer = chooseInitialPokeSalUseCase.execute();
      chooseItemsUseCase.execute(trainer);
      trainers.add(trainer);
    }
    return trainers;
  }
}
