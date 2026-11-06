package br.edu.ucsal.tqs.pokesal.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.tqs.pokesal.entities.BattleResult;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class InMemoryResultRepositoryTest {
  private final InMemoryResultRepository repository = new InMemoryResultRepository();

  private static BattleResult resultado(String vencedor, String perdedor) {
    Trainer a = new Trainer(vencedor, PokeSalCatalog.createCharSal());
    Trainer b = new Trainer(perdedor, PokeSalCatalog.createSquirtSal());
    return new BattleResult(a, b, 4, "CharSal", "SquirtSal");
  }

  @Test
  @DisplayName("Resultados salvos devem ser listados na ordem em que foram salvos")
  void listaNaOrdemDeSalvamento() {
    BattleResult primeiro = resultado("A", "B");
    BattleResult segundo = resultado("C", "D");

    repository.save(primeiro);
    repository.save(segundo);

    assertEquals(List.of(primeiro, segundo), repository.findAll());
  }

  @Test
  @DisplayName("Lista devolvida deve ser uma copia que nao altera o repositorio")
  void listaDevolvidaEhUmaCopia() {
    repository.save(resultado("A", "B"));

    repository.findAll().clear();

    assertEquals(1, repository.findAll().size());
  }

  @Test
  @DisplayName("Repositorio novo deve comecar vazio")
  void repositorioComecaVazio() {
    assertTrue(repository.findAll().isEmpty());
  }

  @Test
  @DisplayName("Salvar resultado nulo deve lancar excecao")
  void salvarNuloLancaExcecao() {
    assertThrows(NullPointerException.class, () -> repository.save(null));
  }
}
