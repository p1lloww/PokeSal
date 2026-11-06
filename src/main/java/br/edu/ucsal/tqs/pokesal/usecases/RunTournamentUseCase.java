package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso de uso responsável por conduzir um torneio eliminatório com 4 ou 8 treinadores. Os
 * confrontos de cada fase seguem a ordem de inscrição (o 1º enfrenta o 2º, o 3º enfrenta o 4º, e
 * assim por diante) e apenas o vencedor de cada batalha avança para a fase seguinte, até restar o
 * campeão.
 */
public class RunTournamentUseCase {

  private static final int SMALL_TOURNAMENT_SIZE = 4;
  private static final int LARGE_TOURNAMENT_SIZE = 8;

  private final MatchRunner matchRunner;
  private final BattleCli battleCli;

  /**
   * Cria um novo caso de uso de torneio com os colaboradores informados.
   *
   * @param matchRunner o executor que disputa cada batalha do chaveamento
   * @param battleCli   a CLI usada para anunciar os confrontos e o campeão
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public RunTournamentUseCase(MatchRunner matchRunner, BattleCli battleCli) {
    if (matchRunner == null) {
      throw new NullPointerException("O executor de batalhas não pode ser nulo");
    }
    if (battleCli == null) {
      throw new NullPointerException("A CLI não pode ser nula");
    }

    this.matchRunner = matchRunner;
    this.battleCli = battleCli;
  }

  /**
   * Conduz o torneio com os treinadores informados, na ordem de inscrição, e retorna o campeão.
   *
   * @param trainers os treinadores inscritos, na ordem do chaveamento
   * @return o treinador campeão
   * @throws NullPointerException     se a lista ou algum treinador for nulo
   * @throws IllegalArgumentException se a quantidade de treinadores não for 4 nem 8
   * @throws IllegalStateException    se uma batalha terminar com um vencedor que não estava no
   *                                  confronto
   */
  public Trainer execute(List<Trainer> trainers) {
    if (trainers == null) {
      throw new NullPointerException("A lista de treinadores não pode ser nula");
    }
    if (trainers.size() != SMALL_TOURNAMENT_SIZE && trainers.size() != LARGE_TOURNAMENT_SIZE) {
      throw new IllegalArgumentException("O torneio deve ter 4 ou 8 treinadores");
    }

    List<Trainer> remaining = List.copyOf(trainers);
    while (remaining.size() > 1) {
      remaining = playRound(remaining);
    }

    Trainer champion = remaining.get(0);
    battleCli.announceChampion(champion);
    return champion;
  }

  /**
   * Disputa todos os confrontos de uma fase e retorna os vencedores, na mesma ordem dos
   * confrontos.
   *
   * @param trainers os treinadores que disputam a fase
   * @return os treinadores que avançam para a próxima fase
   */
  private List<Trainer> playRound(List<Trainer> trainers) {
    String roundName = roundName(trainers.size());
    List<Trainer> winners = new ArrayList<>();

    for (int i = 0; i < trainers.size(); i += 2) {
      Trainer trainerA = trainers.get(i);
      Trainer trainerB = trainers.get(i + 1);

      battleCli.announceMatch(roundName, trainerA, trainerB);
      BattleResult result = matchRunner.run(trainerA, trainerB);
      winners.add(winnerOf(result, trainerA, trainerB));
    }

    return winners;
  }

  private static Trainer winnerOf(BattleResult result, Trainer trainerA, Trainer trainerB) {
    Trainer winner = result.winner();
    if (winner != trainerA && winner != trainerB) {
      throw new IllegalStateException("O vencedor da batalha não participou do confronto");
    }
    return winner;
  }

  private static String roundName(int trainersInRound) {
    return switch (trainersInRound) {
      case LARGE_TOURNAMENT_SIZE -> "Quartas de final";
      case SMALL_TOURNAMENT_SIZE -> "Semifinal";
      default -> "Final";
    };
  }
}
