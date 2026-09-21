package br.edu.ucsal.tqs.pokesal.entities.passives.fire;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;

/**
 * Passiva de tipo Fogo que aumenta o ATK do PokeSal em 50% enquanto seu HP estiver abaixo de 1/3 do
 * máximo. O buff é ativado e desativado automaticamente conforme o HP cruza esse limite, reagindo a
 * cada dano recebido.
 */
public class Blaze implements Passive {

  private static final double HP_THRESHOLD = 1.0 / 3.0;
  private static final double ATTACK_BUFF_MULTIPLIER = 1.5;

  private boolean active = false;

  @Override
  public String getName() {
    return "Blaze";
  }

  @Override
  public String getDescription() {
    return "Aumenta o ATK em 50% quando o HP do PokeSal está abaixo de 1/3 do máximo.";
  }

  @Override
  public void onDamageTaken(PokeSal self, PokeSal opponent, double damageTaken) {
    boolean shouldBeActive = self.getElementType() == ElementType.FIRE
        && self.getHp() < self.getMaxHp() * HP_THRESHOLD;

    if (shouldBeActive && !active) {
      self.applyAttackBuff(ATTACK_BUFF_MULTIPLIER);
      active = true;
    } else if (!shouldBeActive && active) {
      self.applyAttackBuff(1 / ATTACK_BUFF_MULTIPLIER);
      active = false;
    }
  }
}