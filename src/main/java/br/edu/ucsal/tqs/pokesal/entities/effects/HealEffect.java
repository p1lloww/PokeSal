package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Efeito que restaura uma quantidade fixa de HP no PokeSal alvo. É um efeito instantâneo, sem
 * duração nem reversão.
 */
public class HealEffect implements Effect {

  private final double healAmount;

  /**
   * Cria um novo efeito de cura com a quantidade especificada.
   *
   * @param healAmount a quantidade de HP a ser restaurada
   * @throws IllegalArgumentException se healAmount não for positivo
   */
  public HealEffect(double healAmount) {
    if (healAmount <= 0) {
      throw new IllegalArgumentException("A quantidade de cura precisa ser maior que 0");
    }

    this.healAmount = healAmount;
  }

  @Override
  public void applyEffect(PokeSal pokeSal) {
    pokeSal.heal(pokeSal.getMaxHp() * healAmount);
  }
}