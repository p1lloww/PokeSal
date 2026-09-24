package br.edu.ucsal.tqs.pokesal.entities.passives.fire;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.passives.LowHpAttackBoost;

/**
 * Passiva de tipo Fogo que aumenta o ATK do PokeSal em 50% enquanto seu HP estiver
 * abaixo de 1/3 do máximo.
 */
public class Blaze extends LowHpAttackBoost {

  /**
   * Cria a passiva Blaze, associada ao tipo Fogo.
   */
  public Blaze() {
    super(ElementType.FIRE);
  }

  @Override
  public String getName() {
    return "Blaze";
  }
}
