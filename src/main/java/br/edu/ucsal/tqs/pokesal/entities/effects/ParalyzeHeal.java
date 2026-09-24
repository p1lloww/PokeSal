package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Paralyzed;

/**
 * Efeito de cura que restaura pouco HP quando o PokeSal alvo não está paralisado, e
 * restaura mais HP e remove a condição quando o PokeSal alvo está paralisado.
 */
public class ParalyzeHeal extends StatusHealEffect {

  /**
   * Cria o efeito ParalyzeHeal, que cura a condição Paralyzed.
   */
  public ParalyzeHeal() {
    super(Paralyzed.class);
  }
}
