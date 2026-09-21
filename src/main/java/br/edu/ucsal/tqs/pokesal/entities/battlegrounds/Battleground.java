package br.edu.ucsal.tqs.pokesal.entities.Battlegrounds;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;

/**
 * Representa um efeito de terreno ativo durante uma batalha, afetando o dano causado por golpes de
 * determinado tipo elemental ou concedendo efeitos ao final de cada turno para o PokeSal ativo.
 * Diferente de uma Passive ou StatusCondition, pertence à batalha como um todo, não a um PokeSal
 * específico. Cada implementação concreta define apenas o comportamento que lhe é próprio; os
 * demais métodos herdam o comportamento neutro declarado nesta interface.
 */
public interface Battleground {

  /**
   * Retorna o nome de exibição deste terreno.
   *
   * @return o nome do terreno, como "Hot Asphalt" ou "Central Flowerbed"
   */
  String getName();

  /**
   * Retorna a descrição textual do efeito deste terreno, exibida ao jogador.
   *
   * @return a descrição do terreno
   */
  String getDescription();

  /**
   * Reage ao fim de um turno de batalha para o PokeSal ativo informado, permitindo efeitos
   * recorrentes como regeneração de HP para determinado tipo elemental.
   *
   * @param activePokeSal o PokeSal ativo no momento do fim do turno
   */
  default void onTurnEnd(PokeSal activePokeSal) {
  }

  /**
   * Modifica o valor de dano informado, refletindo o efeito deste terreno sobre golpes do tipo
   * elemental indicado.
   *
   * @param baseDamage o valor de dano antes da modificação
   * @param attackType o tipo elemental do golpe que está causando o dano
   * @return o valor de dano já modificado; igual a baseDamage caso este terreno não afete golpes do
   *     tipo informado
   */
  default double modifyDamage(double baseDamage, ElementType attackType) {
    return baseDamage;
  }
}