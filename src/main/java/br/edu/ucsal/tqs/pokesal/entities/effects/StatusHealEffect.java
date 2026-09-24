package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;

/**
 * Base dos efeitos de cura que reagem a uma condição de status específica: restauram pouco HP
 * quando o PokeSal alvo não está com essa condição, e restauram mais HP e removem a condição
 * quando o PokeSal alvo está com ela. As subclasses definem apenas qual condição é curada.
 */
public abstract class StatusHealEffect implements Effect {

  private static final double WEAK_HEAL_PERCENTAGE = 0.10;
  private static final double STRONG_HEAL_PERCENTAGE = 0.25;

  private final Class<? extends StatusCondition> curedStatus;

  /**
   * Cria o efeito de cura associado à condição de status informada.
   *
   * @param curedStatus a classe da condição de status que este efeito cura
   */
  protected StatusHealEffect(Class<? extends StatusCondition> curedStatus) {
    this.curedStatus = curedStatus;
  }

  @Override
  public void applyEffect(PokeSal pokeSal) {
    if (curedStatus.isInstance(pokeSal.getStatusCondition())) {
      pokeSal.heal(pokeSal.getMaxHp() * STRONG_HEAL_PERCENTAGE);
      pokeSal.clearStatus();
    } else {
      pokeSal.heal(pokeSal.getMaxHp() * WEAK_HEAL_PERCENTAGE);
    }
  }
}
