package br.edu.ucsal.tqs.pokesal.entities.statusconditions;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Condição de status que reduz o SPD do PokeSal afetado em 50% enquanto estiver ativa, sem causar
 * dano nem afetar outros atributos.
 */
public class Paralyzed implements StatusCondition {

  private static final double SPEED_REDUCTION_MULTIPLIER = 0.5;

  @Override
  public String getName() {
    return "Paralyzed";
  }

  @Override
  public String getDescription() {
    return "Reduz o SPD em 50%.";
  }

  @Override
  public void onApply(PokeSal self) {
    self.applySpeedBuff(SPEED_REDUCTION_MULTIPLIER);
  }

  @Override
  public void onRemove(PokeSal self) {
    self.applySpeedBuff(1 / SPEED_REDUCTION_MULTIPLIER);
  }
}