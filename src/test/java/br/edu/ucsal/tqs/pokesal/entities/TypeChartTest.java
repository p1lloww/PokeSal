package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testes da tabela de vantagens e desvantagens elementais.
 */
public class TypeChartTest {

  private static final double DELTA = 0.0001;

  @ParameterizedTest(name = "{0} contra {1} deve causar multiplicador {2}")
  @CsvSource({
      "FIRE, PLANT, 2.0",
      "FIRE, WATER, 0.5",
      "WATER, FIRE, 2.0",
      "WATER, PLANT, 0.5",
      "PLANT, WATER, 2.0",
      "PLANT, FIRE, 0.5"
  })
  void testeVantagemElemental(ElementType attacker, ElementType defender, double expected) {
    double multiplier = TypeChart.getMultiplier(attacker, defender);

    assertEquals(expected, multiplier, DELTA);
  }

  @ParameterizedTest(name = "{0} contra {1} deve ser neutro(1.0)")
  @CsvSource({
      "FIRE, FIRE",
      "WATER, WATER",
      "PLANT, PLANT"
  })
  void testeCombinacaoDoMesmoElementoEhNeutra(ElementType attacker, ElementType defender) {
    double multiplier = TypeChart.getMultiplier(attacker, defender);

    assertEquals(1.0, multiplier, DELTA);
  }

  @Test
  void todaCombinacaoDeTiposTemMultiplicadorValido() {
    for (ElementType attacker : ElementType.values()) {
      for (ElementType defender : ElementType.values()) {
        double multiplier = TypeChart.getMultiplier(attacker, defender);

        assertTrue(multiplier == 0.5 || multiplier == 1.0 || multiplier == 2.0,
            attacker + " vs " + defender + " retornou " + multiplier);
      }
    }
  }

  @Test
  void todaRelacaoCadastradaTemSuaInversaTambemCadastrada() {
    for (TypeMatchup matchup : TypeChart.getRegisteredMatchups()) {
      TypeMatchup inverso = new TypeMatchup(matchup.defender(), matchup.attacker());

      assertTrue(TypeChart.getRegisteredMatchups().contains(inverso),
          "Falta a relação inversa de " + matchup.attacker()
              + " vs " + matchup.defender());
    }
  }
}
