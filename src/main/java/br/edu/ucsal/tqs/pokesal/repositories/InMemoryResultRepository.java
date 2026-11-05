package br.edu.ucsal.tqs.pokesal.repositories;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementação de ResultRepository que guarda os resultados em memória, enquanto a aplicação
 * estiver em execução.
 */
public class InMemoryResultRepository implements ResultRepository {

  private final List<BattleResult> results = new ArrayList<>();

  @Override
  public void save(BattleResult result) {
    if (result == null) {
      throw new NullPointerException("O resultado não pode ser nulo");
    }

    results.add(result);
  }

  @Override
  public List<BattleResult> findAll() {
    return new ArrayList<>(results);
  }
}
