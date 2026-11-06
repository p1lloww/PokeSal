package br.edu.ucsal.tqs.pokesal.services.notification;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;

/**
 * Canal externo usado para enviar alertas aos treinadores durante a batalha.
 */
public interface TrainerNotifier {

  /**
   * Avisa o treinador de que o HP do seu PokeSal está crítico.
   *
   * @param trainer o treinador que recebe o alerta
   * @param pokeSal o PokeSal do treinador que está com HP crítico
   */
  void notifyCriticalHp(Trainer trainer, PokeSal pokeSal);
}
