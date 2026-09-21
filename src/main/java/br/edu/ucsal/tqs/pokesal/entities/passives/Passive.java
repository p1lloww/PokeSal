package br.edu.ucsal.tqs.pokesal.entities.passives;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;

/**
 * Representa uma habilidade passiva que um PokeSal carrega, reagindo automaticamente a eventos de
 * batalha como entrar em campo, sofrer dano ou o fim de um turno, sem exigir ação do jogador. Cada
 * implementação concreta define apenas os eventos que lhe são relevantes, os demais métodos herdam
 * o comportamento neutro (sem efeito) declarado nesta interface.
 */
public interface Passive {

  /**
   * Retorna o nome de exibição da habilidade.
   *
   * @return o nome da passiva, como "Blaze" ou "Intimidate"
   */
  String getName();

  /**
   * Retorna a descrição textual do efeito da habilidade, exibida ao jogador.
   *
   * @return a descrição da passiva
   */
  String getDescription();

  /**
   * Reage ao momento em que o PokeSal dono desta passiva entra em campo no início da batalha.
   *
   * @param self o PokeSal dono desta passiva
   * @param opponent o PokeSal adversário presente na batalha
   */
  default void onSwitchIn(PokeSal self, PokeSal opponent) {
  }

  /**
   * Reage ao evento de o PokeSal dono desta passiva sofrer dano, permitindo aplicar efeitos
   * condicionais como buffs ativados por HP baixo.
   *
   * @param self o PokeSal dono desta passiva
   * @param opponent o PokeSal adversário que causou o dano
   * @param damageTaken a quantidade de dano recebida neste evento
   */
  default void onDamageTaken(PokeSal self, PokeSal opponent, double damageTaken) {
  }

  /**
   * Reage ao fim de um turno de batalha, permitindo efeitos recorrentes como regeneração de HP ou
   * custo de manutenção de um buff ativo.
   *
   * @param self o PokeSal dono desta passiva
   */
  default void onTurnEnd(PokeSal self) {
  }

  /**
   * Reage ao momento em que o PokeSal dono desta passiva executa um ataque, retornando um
   * multiplicador de dano adicional. Usado por passivas sensíveis ao terreno da batalha.
   *
   * @param self o PokeSal dono desta passiva
   * @param battleground o terreno ativo na batalha no momento do ataque
   * @return o multiplicador de dano a ser aplicado; 1.0 significa nenhuma alteração
   */
  default double onAttack(PokeSal self, Battleground battleground) {
    return 1.0;
  }

  /**
   * Indica se esta passiva impede a aplicação de qualquer condição de status negativa no PokeSal
   * dono.
   *
   * @param self o PokeSal dono desta passiva
   * @return true se a aplicação de status deve ser bloqueada; false caso contrário
   */
  default boolean preventsStatusApplication(PokeSal self) {
    return false;
  }
}