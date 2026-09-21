package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;

/**
 * Efeito de cura que restaura pouco HP quando o PokeSal alvo não está envenenado, e restaura mais
 * HP e cura o veneno quando o PokeSal alvo está envenenado.
 */
public class SuperAntidote implements Effect {

  private static final double WEAK_HEAL_PERCENTAGE = 0.10;
  private static final double STRONG_HEAL_PERCENTAGE = 0.25;

  @Override
  public void applyEffect(PokeSal pokeSal) {
    if (pokeSal.getStatusCondition() instanceof Poison) {
      pokeSal.heal(pokeSal.getMaxHp() * STRONG_HEAL_PERCENTAGE);
      pokeSal.clearStatus();
    } else {
      pokeSal.heal(pokeSal.getMaxHp() * WEAK_HEAL_PERCENTAGE);
    }
  }
}