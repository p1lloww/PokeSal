package br.edu.ucsal.tqs.pokesal.services.log;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;

/**
 * Registro dos eventos que acontecem durante uma batalha. Dentro de uma ação de ataque, os eventos
 * são enviados nesta ordem: ataque, dano (ou erro), efeito de status e efeito de terreno. O uso de
 * item é registrado pela ação de item.
 */
public interface BattleLogger {

  /**
   * Registra que um PokeSal usou um golpe contra o adversário.
   *
   * @param attacker o PokeSal que usou o golpe
   * @param defender o PokeSal alvo do golpe
   * @param move     o golpe usado
   */
  void logAttack(PokeSal attacker, PokeSal defender, Move move);

  /**
   * Registra o dano causado por um golpe que acertou o alvo.
   *
   * @param defender o PokeSal que recebeu o dano
   * @param damage   o dano causado
   * @param critical true se o golpe foi um acerto crítico
   */
  void logDamage(PokeSal defender, double damage, boolean critical);

  /**
   * Registra que um golpe errou o alvo e não causou dano.
   *
   * @param attacker o PokeSal que errou o golpe
   * @param move     o golpe que errou
   */
  void logMiss(PokeSal attacker, Move move);

  /**
   * Registra uma condição de status aplicada ou acionada em um PokeSal.
   *
   * @param target o PokeSal afetado
   * @param status a condição de status
   */
  void logStatusEffect(PokeSal target, StatusCondition status);

  /**
   * Registra que o efeito do terreno foi aplicado.
   *
   * @param battleground o terreno cujo efeito foi aplicado
   * @param target       o PokeSal afetado pelo efeito
   */
  void logBattlegroundEffect(Battleground battleground, PokeSal target);

  /**
   * Registra o uso de um item.
   *
   * @param target o PokeSal que recebeu os efeitos do item
   * @param item   o item usado
   */
  void logItemUse(PokeSal target, Item item);

  /**
   * Registra um erro que não deve interromper a batalha, como uma falha ao salvar o resultado.
   *
   * @param message a mensagem descrevendo o erro
   * @param cause   a exceção que causou o erro
   */
  void logError(String message, Exception cause);
}
