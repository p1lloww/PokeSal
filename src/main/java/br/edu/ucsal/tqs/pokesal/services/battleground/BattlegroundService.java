package br.edu.ucsal.tqs.pokesal.services.battleground;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Serviço que decide se o efeito de um terreno está ativo no momento, considerando as condições
 * externas de clima e horário.
 */
public interface BattlegroundService {

  /**
   * Indica se o efeito do terreno informado está ativo nas condições atuais.
   *
   * @param battleground o terreno a ser consultado
   * @return true se o efeito do terreno deve ser aplicado; false caso contrário
   */
  boolean isActive(Battleground battleground);
}
