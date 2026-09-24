package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;

/**
 * Efeito de cura que restaura pouco HP quando o PokeSal alvo não está envenenado, e
 * restaura mais HP e remove a condição quando o PokeSal alvo está envenenado.
 */
public class SuperAntidote extends StatusHealEffect {

  /**
   * Cria o efeito SuperAntidote, que cura a condição Poison.
   */
  public SuperAntidote() {
    super(Poison.class);
  }
}
