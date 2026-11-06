package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.BattlegroundCatalog;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;

/**
 * Implementação de MatchRunner que restaura os PokeSal dos dois treinadores, sorteia o terreno e
 * conduz a batalha com o RunBattleUseCase, que também salva o resultado no repositório.
 */
public class DefaultMatchRunner implements MatchRunner {

  private final RunBattleUseCase runBattleUseCase;
  private final RandomGenerator randomGenerator;

  /**
   * Cria um novo executor de batalhas com os colaboradores informados.
   *
   * @param runBattleUseCase o caso de uso que conduz cada batalha
   * @param randomGenerator  o gerador usado para sortear o terreno de cada batalha
   * @throws NullPointerException se algum dos argumentos for nulo
   */
  public DefaultMatchRunner(RunBattleUseCase runBattleUseCase, RandomGenerator randomGenerator) {
    if (runBattleUseCase == null) {
      throw new NullPointerException("O caso de uso de batalha não pode ser nulo");
    }
    if (randomGenerator == null) {
      throw new NullPointerException("O gerador aleatório não pode ser nulo");
    }

    this.runBattleUseCase = runBattleUseCase;
    this.randomGenerator = randomGenerator;
  }

  @Override
  public BattleResult run(Trainer trainerA, Trainer trainerB) {
    trainerA.getPokeSal().restore();
    trainerB.getPokeSal().restore();

    Battle battle = new Battle(trainerA, trainerB,
        BattlegroundCatalog.randomBattleground(randomGenerator));
    runBattleUseCase.execute(battle);

    return BattleResult.from(battle);
  }
}
