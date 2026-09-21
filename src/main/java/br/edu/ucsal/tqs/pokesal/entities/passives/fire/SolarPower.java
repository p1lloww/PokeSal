package br.edu.ucsal.tqs.pokesal.entities.passives.fire;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;

/**
 * Passiva de tipo Fogo que aumenta o ATK do PokeSal ao atacar enquanto o terreno
 * ativo for Hot Asphalt, com um custo de HP a cada turno enquanto o bônus estiver
 * ativo.
 */
public class SolarPower implements Passive {

  private static final double ATTACK_BOOST_MULTIPLIER = 1.5;
  private static final double HP_COST_PERCENTAGE = 0.125;

  private boolean active = false;

  @Override
  public String getName() {
    return "Solar Power";
  }

  @Override
  public String getDescription() {
    return "Aumenta o ATK ao atacar em Hot Asphalt, com um custo de HP por turno.";
  }

  @Override
  public double onAttack(PokeSal self, Battleground battleground) {
    if (battleground instanceof HotAsphalt) {
      active = true;
      return ATTACK_BOOST_MULTIPLIER;
    }

    active = false;
    return 1.0;
  }

  @Override
  public void onTurnEnd(PokeSal self) {
    if (active) {
      self.takeDamage(self.getMaxHp() * HP_COST_PERCENTAGE);
    }
  }
}