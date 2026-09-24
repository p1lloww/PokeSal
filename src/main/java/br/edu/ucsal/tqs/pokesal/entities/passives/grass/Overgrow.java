package br.edu.ucsal.tqs.pokesal.entities.passives.grass;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.passives.LowHpAttackBoost;

/**
 * Passiva de tipo Planta que aumenta o ATK do PokeSal em 50% enquanto seu HP estiver
 * abaixo de 1/3 do máximo.
 */
public class Overgrow extends LowHpAttackBoost {

  /**
   * Cria a passiva Overgrow, associada ao tipo Planta.
   */
  public Overgrow() {
    super(ElementType.PLANT);
  }

  @Override
  public String getName() {
    return "Overgrow";
  }
}
