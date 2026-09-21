package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;

/**
 * Efeito de cura que restaura pouco HP quando o PokeSal alvo não está queimado, e
 * restaura mais HP e cura a queimadura quando o PokeSal alvo está queimado.
 */
public class BurnHeal implements Effect {

  private static final double WEAK_HEAL_PERCENTAGE = 0.10;
  private static final double STRONG_HEAL_PERCENTAGE = 0.25;

  @Override
  public void applyEffect(PokeSal pokeSal) {
    if (pokeSal.getStatusCondition() instanceof Burn) {
      pokeSal.heal(pokeSal.getMaxHp() * STRONG_HEAL_PERCENTAGE);
      pokeSal.clearStatus();
    } else {
      pokeSal.heal(pokeSal.getMaxHp() * WEAK_HEAL_PERCENTAGE);
    }
  }
}