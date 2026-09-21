package br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Terreno que representa uma poça de chuva no estacionamento da UCSal, aumentando o dano de golpes
 * do tipo Água em 10%.
 */
public class WaterPuddle implements Battleground {

  private static final double WATER_DAMAGE_MULTIPLIER = 1.10;

  @Override
  public String getName() {
    return "Water Puddle";
  }

  @Override
  public String getDescription() {
    return "Aumenta o dano de golpes do tipo Água em 10%.";
  }

  @Override
  public double modifyDamage(double baseDamage, ElementType attackType) {
    if (attackType == ElementType.WATER) {
      return baseDamage * WATER_DAMAGE_MULTIPLIER;
    }

    return baseDamage;
  }
}