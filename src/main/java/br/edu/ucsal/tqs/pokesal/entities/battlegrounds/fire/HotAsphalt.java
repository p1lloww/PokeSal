package br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Terreno que representa o estacionamento da UCSal sob sol forte, aumentando o dano de golpes do
 * tipo Fogo em 15%.
 */
public class HotAsphalt implements Battleground {

  private static final double FIRE_DAMAGE_MULTIPLIER = 1.15;

  @Override
  public String getName() {
    return "Hot Asphalt";
  }

  @Override
  public String getDescription() {
    return "Aumenta o dano de golpes do tipo Fogo em 15%.";
  }

  @Override
  public double modifyDamage(double baseDamage, ElementType attackType) {
    if (attackType == ElementType.FIRE) {
      return baseDamage * FIRE_DAMAGE_MULTIPLIER;
    }

    return baseDamage;
  }
}