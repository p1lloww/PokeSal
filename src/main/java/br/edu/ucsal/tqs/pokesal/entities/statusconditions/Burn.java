package br.edu.ucsal.tqs.pokesal.entities.statusconditions;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Condição de status que causa dano contínuo ao final de cada turno e reduz o ATK do PokeSal
 * afetado em 50% enquanto estiver ativa.
 */
public class Burn implements StatusCondition {

  private static final double DAMAGE_PERCENTAGE = 1.0 / 16.0;
  private static final double ATTACK_REDUCTION_MULTIPLIER = 0.5;

  @Override
  public String getName() {
    return "Burn";
  }

  @Override
  public String getDescription() {
    return "Causa dano ao final de cada turno e reduz o ATK em 50%.";
  }

  @Override
  public void onApply(PokeSal self) {
    self.applyAttackBuff(ATTACK_REDUCTION_MULTIPLIER);
  }

  @Override
  public void onTurnEnd(PokeSal self) {
    self.takeDamage(self.getMaxHp() * DAMAGE_PERCENTAGE);
  }

  @Override
  public void onRemove(PokeSal self) {
    self.applyAttackBuff(1 / ATTACK_REDUCTION_MULTIPLIER);
  }
}
