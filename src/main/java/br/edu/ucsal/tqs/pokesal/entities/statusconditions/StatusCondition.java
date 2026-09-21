package br.edu.ucsal.tqs.pokesal.entities.statusconditions;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Representa uma condição de status negativa aplicada a um PokeSal durante a batalha, como
 * queimadura, veneno ou paralisia. Diferente de uma Passive, uma StatusCondition não é fixa: é
 * aplicada durante o combate, persiste por vários turnos e pode ser removida. Cada implementação
 * concreta define apenas o comportamento que lhe é próprio; os demais métodos herdam o
 * comportamento neutro declarado nesta interface.
 */
public interface StatusCondition {

  /**
   * Retorna o nome de exibição desta condição de status.
   *
   * @return o nome da condição, como "Burn" ou "Poison"
   */
  String getName();

  /**
   * Retorna a descrição textual do efeito desta condição de status, exibida ao jogador.
   *
   * @return a descrição da condição de status
   */
  String getDescription();

  /**
   * Reage ao momento em que esta condição de status é aplicada ao PokeSal informado, permitindo
   * efeitos imediatos, se houver.
   *
   * @param self o PokeSal ao qual a condição foi aplicada
   */
  default void onApply(PokeSal self) {
  }

  /**
   * Reage ao fim de um turno de batalha enquanto esta condição de status está ativa, permitindo
   * efeitos recorrentes como dano contínuo.
   *
   * @param self o PokeSal afetado por esta condição de status
   */
  default void onTurnEnd(PokeSal self) {
  }
}