package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Efeito que aumenta temporariamente o SPD do PokeSal alvo, revertido automaticamente ao expirar.
 */
public class SpeedBuffEffect implements Effect {

  private static final double SPEED_BUFF_MULTIPLIER = 1.3;

  @Override
  public void applyEffect(PokeSal pokeSal) {
    pokeSal.applySpeedBuff(SPEED_BUFF_MULTIPLIER);
  }

  @Override
  public void removeEffect(PokeSal pokeSal) {
    pokeSal.applySpeedBuff(1 / SPEED_BUFF_MULTIPLIER);
  }

  @Override
  public boolean isTemporary() {
    return true;
  }
}