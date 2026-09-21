package br.edu.ucsal.tqs.pokesal.entities.passives.neutral;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;

/**
 * Passiva que reduz o ATK do oponente ao entrar em campo, uma única vez, no início da batalha.
 */
public class Intimidate implements Passive {

  private static final double OPPONENT_ATTACK_DEBUFF_MULTIPLIER = 0.67;

  @Override
  public String getName() {
    return "Intimidate";
  }

  @Override
  public String getDescription() {
    return "Reduz o ATK do oponente ao entrar em campo.";
  }

  @Override
  public void onSwitchIn(PokeSal self, PokeSal opponent) {
    opponent.applyAttackBuff(OPPONENT_ATTACK_DEBUFF_MULTIPLIER);
  }
}