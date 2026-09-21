package br.edu.ucsal.tqs.pokesal.entities.turnactions;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Representa uma ação que um PokeSal pode executar durante seu momento no turno,
 * como atacar com um golpe ou usar um item. Cada implementação encapsula os dados
 * necessários para a ação (o Move ou o Item escolhido) e sabe executar-se sobre os
 * PokeSal informados, sem que o Turn precise conhecer os detalhes de como cada tipo
 * de ação funciona internamente.
 */
public interface TurnAction {

  /**
   * Executa esta ação, aplicando seus efeitos sobre os PokeSal envolvidos.
   *
   * @param actor o PokeSal que está executando a ação
   * @param opponent o PokeSal adversário no turno
   * @param battleground o terreno ativo na batalha, usado quando a ação depende
   *     dele para seu cálculo
   */
  void execute(PokeSal actor, PokeSal opponent, Battleground battleground);
}