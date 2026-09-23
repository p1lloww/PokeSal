package br.edu.ucsal.tqs.pokesal.entities;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Tabela estática de multiplicadores de dano entre tipos elementais, cobrindo as relações de
 * vantagem e desvantagem entre Fogo, Água e Planta. Combinações não cadastradas nesta tabela são
 * consideradas neutras, retornando o multiplicador padrão 1.0. Classe utilitária, não
 * instanciável.
 */
public final class TypeChart {

  private static final double SUPER_EFFECTIVE_MULTIPLIER = 2.0;
  private static final double NOT_VERY_EFFECTIVE_MULTIPLIER = 0.5;
  private static final double NEUTRAL_MULTIPLIER = 1.0;

  private static final Map<TypeMatchup, Double> CHART = Map.ofEntries(
      Map.entry(new TypeMatchup(ElementType.FIRE, ElementType.PLANT),
          SUPER_EFFECTIVE_MULTIPLIER),
      Map.entry(new TypeMatchup(ElementType.FIRE, ElementType.WATER),
          NOT_VERY_EFFECTIVE_MULTIPLIER),
      Map.entry(new TypeMatchup(ElementType.WATER, ElementType.FIRE),
          SUPER_EFFECTIVE_MULTIPLIER),
      Map.entry(new TypeMatchup(ElementType.WATER, ElementType.PLANT),
          NOT_VERY_EFFECTIVE_MULTIPLIER),
      Map.entry(new TypeMatchup(ElementType.PLANT, ElementType.WATER),
          SUPER_EFFECTIVE_MULTIPLIER),
      Map.entry(new TypeMatchup(ElementType.PLANT, ElementType.FIRE),
          NOT_VERY_EFFECTIVE_MULTIPLIER)
  );

  private TypeChart() {
  }

  /**
   * Retorna o multiplicador de dano para um golpe do tipo atacante informado contra um PokeSal do
   * tipo defensor informado. Combinações não cadastradas retornam o multiplicador neutro.
   *
   * @param attacker o tipo elemental do golpe que ataca
   * @param defender o tipo elemental do PokeSal que defende
   * @return o multiplicador de dano correspondente à combinação informada
   */
  public static double getMultiplier(ElementType attacker, ElementType defender) {
    return CHART.getOrDefault(new TypeMatchup(attacker, defender), NEUTRAL_MULTIPLIER);
  }

  /**
   * Retorna o conjunto de combinações de tipos cadastradas nesta tabela.
   *
   * @return o conjunto de combinações cadastradas, não modificável
   */
  public static Set<TypeMatchup> getRegisteredMatchups() {
    return Collections.unmodifiableSet(CHART.keySet());
  }
}