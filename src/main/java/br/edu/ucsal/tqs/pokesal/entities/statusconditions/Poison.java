package br.edu.ucsal.tqs.pokesal.entities.statusconditions;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Condição de status que causa dano contínuo ao final de cada turno, sem afetar nenhum outro
 * atributo do PokeSal afetado.
 */
public class Poison implements StatusCondition {

  private static final double DAMAGE_PERCENTAGE = 1.0 / 8.0;

  @Override
  public String getName() {
    return "Poison";
  }

  @Override
  public String getDescription() {
    return "Causa dano ao final de cada turno.";
  }

  @Override
  public void onTurnEnd(PokeSal self) {
    self.takeDamage(self.getMaxHp() * DAMAGE_PERCENTAGE);
  }
}
