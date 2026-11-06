package br.edu.ucsal.tqs.pokesal.usecases;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;

/**
 * Executa uma batalha completa entre dois treinadores e informa o resultado. É usado pelo torneio
 * para disputar cada confronto do chaveamento.
 */
public interface MatchRunner {

  /**
   * Disputa uma batalha entre os dois treinadores informados.
   *
   * @param trainerA o primeiro treinador
   * @param trainerB o segundo treinador
   * @return o resultado da batalha, com vencedor, perdedor e número de turnos
   */
  BattleResult run(Trainer trainerA, Trainer trainerB);
}
