package br.edu.ucsal.tqs.pokesal.repositories;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import java.util.List;

/**
 * Repositório onde os resultados das batalhas são guardados.
 */
public interface ResultRepository {

  /**
   * Salva o resultado de uma batalha.
   *
   * @param result o resultado a ser salvo
   */
  void save(BattleResult result);

  /**
   * Retorna todos os resultados salvos, na ordem em que foram salvos.
   *
   * @return a lista de resultados
   */
  List<BattleResult> findAll();
}
