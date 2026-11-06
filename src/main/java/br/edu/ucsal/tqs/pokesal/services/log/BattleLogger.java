package br.edu.ucsal.tqs.pokesal.services.log;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;

/**
 * Registro dos eventos que acontecem durante uma batalha. Dentro de uma ação de ataque, os eventos
 * são enviados nesta ordem: ataque, dano (ou erro), efeito de status e efeito de terreno. O uso de
 * item não faz parte dessa sequência: ele é registrado pela ação de item, no momento em que o item
 * é usado.
 */
public interface BattleLogger {

  /**
   * Registra o início de uma batalha entre os dois treinadores.
   *
   * @param trainerA o primeiro treinador
   * @param trainerB o segundo treinador
   */
  void logBattleStart(Trainer trainerA, Trainer trainerB);

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
   * Registra que uma condição de status foi aplicada a um PokeSal.
   *
   * @param target o PokeSal que recebeu a condição
   * @param status a condição de status aplicada
   */
  void logStatusEffect(PokeSal target, StatusCondition status);

  /**
   * Registra o HP perdido por um PokeSal por causa da sua condição de status no fim do turno.
   *
   * @param target o PokeSal que perdeu HP
   * @param status a condição de status que causou a perda
   * @param hpLost a quantidade de HP perdida
   */
  void logStatusDamage(PokeSal target, StatusCondition status, int hpLost);

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
