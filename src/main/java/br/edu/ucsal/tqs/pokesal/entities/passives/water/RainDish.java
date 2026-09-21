package br.edu.ucsal.tqs.pokesal.entities.passives.water;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;

/**
 * Passiva que aumenta a DEF do próprio PokeSal em 20% ao entrar em campo, uma única vez, no início
 * da batalha.
 */
public class RainDish implements Passive {

  private static final double DEFENSE_BUFF_MULTIPLIER = 1.2;

  @Override
  public String getName() {
    return "Rain Dish";
  }

  @Override
  public String getDescription() {
    return "Aumenta a DEF em 20% ao entrar em campo.";
  }

  @Override
  public void onSwitchIn(PokeSal self, PokeSal opponent) {
    self.applyDefenseBuff(DEFENSE_BUFF_MULTIPLIER);
  }
}