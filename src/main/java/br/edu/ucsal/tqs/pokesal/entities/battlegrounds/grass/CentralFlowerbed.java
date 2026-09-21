package br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Terreno que representa o canteiro central do estacionamento da UCSal, curando 5% do HP máximo de
 * PokeSal do tipo Planta ao final de cada turno.
 */
public class CentralFlowerbed implements Battleground {

  private static final double HEAL_PERCENTAGE = 0.05;

  @Override
  public String getName() {
    return "Central Flowerbed";
  }

  @Override
  public String getDescription() {
    return "Cura 5% do HP máximo de PokeSal do tipo Planta ao final de cada turno.";
  }

  @Override
  public void onTurnEnd(PokeSal activePokeSal) {
    if (activePokeSal.getElementType() == ElementType.PLANT) {
      activePokeSal.heal(activePokeSal.getMaxHp() * HEAL_PERCENTAGE);
    }
  }
}