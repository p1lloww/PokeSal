package br.edu.ucsal.tqs.pokesal.entities.effects;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Representa o efeito de um item de batalha, aplicado a um PokeSal quando o item é
 * utilizado. Um efeito pode ser instantâneo (como restaurar HP) ou temporário, caso em
 * que sua reversão é agendada e executada automaticamente ao final do turno de
 * expiração. Cada implementação concreta define apenas o comportamento que lhe é
 * próprio; os demais métodos herdam o comportamento neutro declarado nesta interface.
 */
public interface Effect {

  /**
   * Aplica este efeito ao PokeSal informado. É sempre invocado exatamente uma vez,
   * no momento em que o item correspondente é utilizado.
   *
   * @param pokeSal o PokeSal que recebe o efeito
   */
  void applyEffect(PokeSal pokeSal);

  /**
   * Reverte este efeito no PokeSal informado. É invocado apenas para efeitos
   * temporários, exatamente uma vez, no turno em que a expiração está agendada.
   *
   * @param pokeSal o PokeSal do qual o efeito é revertido
   */
  default void removeEffect(PokeSal pokeSal) {}

  /**
   * Indica se este efeito é temporário e deve ter sua reversão agendada ao ser aplicado.
   *
   * @return true se o efeito é temporário e será revertido posteriormente;
   *     false se o efeito é instantâneo e permanente
   */
  default boolean isTemporary() {
    return false;
  }
}
