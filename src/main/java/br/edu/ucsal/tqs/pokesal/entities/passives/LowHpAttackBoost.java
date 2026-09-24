package br.edu.ucsal.tqs.pokesal.entities.passives;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Base das passivas que aumentam o ATK do PokeSal em 50% enquanto seu HP estiver abaixo de 1/3 do
 * máximo, desde que o PokeSal seja do tipo elemental associado à passiva. O buff é ativado e
 * desativado automaticamente conforme o HP cruza esse limite, reagindo a cada dano recebido. As
 * subclasses definem apenas o tipo elemental e o nome da passiva.
 */
public abstract class LowHpAttackBoost implements Passive {

  private static final double HP_THRESHOLD = 1.0 / 3.0;
  private static final double ATTACK_BUFF_MULTIPLIER = 1.5;

  private final ElementType elementType;
  private boolean active = false;

  /**
   * Cria a passiva associada ao tipo elemental informado.
   *
   * @param elementType o tipo elemental que o PokeSal precisa ter para o buff ser ativado
   */
  protected LowHpAttackBoost(ElementType elementType) {
    this.elementType = elementType;
  }

  @Override
  public String getDescription() {
    return "Aumenta o ATK em 50% quando o HP do PokeSal está abaixo de 1/3 do máximo.";
  }

  @Override
  public void onDamageTaken(PokeSal self, PokeSal opponent, double damageTaken) {
    boolean shouldBeActive = self.getElementType() == elementType
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
