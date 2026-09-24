package br.edu.ucsal.tqs.pokesal.entities.passives.water;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.passives.LowHpAttackBoost;

/**
 * Passiva de tipo Água que aumenta o ATK do PokeSal em 50% enquanto seu HP estiver
 * abaixo de 1/3 do máximo.
 */
public class Torrent extends LowHpAttackBoost {

  /**
   * Cria a passiva Torrent, associada ao tipo Água.
   */
  public Torrent() {
    super(ElementType.WATER);
  }

  @Override
  public String getName() {
    return "Torrent";
  }
}
