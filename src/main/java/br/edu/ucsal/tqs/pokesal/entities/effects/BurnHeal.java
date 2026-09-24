package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;

/**
 * Efeito de cura que restaura pouco HP quando o PokeSal alvo não está queimado, e
 * restaura mais HP e remove a condição quando o PokeSal alvo está queimado.
 */
public class BurnHeal extends StatusHealEffect {

  /**
   * Cria o efeito BurnHeal, que cura a condição Burn.
   */
  public BurnHeal() {
    super(Burn.class);
  }
}
