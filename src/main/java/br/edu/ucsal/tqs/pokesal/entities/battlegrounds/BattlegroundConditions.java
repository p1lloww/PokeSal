package br.edu.ucsal.tqs.pokesal.entities.battlegrounds;

import java.time.LocalTime;

/**
 * Condições de clima e horário consultadas por um terreno para decidir se seu efeito está ativo.
 *
 * @param time    o horário atual
 * @param weather o clima atual
 */
public record BattlegroundConditions(LocalTime time, Weather weather) {

  private static final LocalTime DAY_START = LocalTime.of(6, 0);
  private static final LocalTime NIGHT_START = LocalTime.of(18, 0);

  /**
   * Valida os componentes destas condições no momento da criação.
   *
   * @throws NullPointerException se time ou weather forem nulos
   */
  public BattlegroundConditions {
    if (time == null) {
      throw new NullPointerException("O horário não pode ser nulo");
    }
    if (weather == null) {
      throw new NullPointerException("O clima não pode ser nulo");
    }
  }

  /**
   * Indica se o horário destas condições é de dia, ou seja, entre 06:00 (inclusivo) e 18:00
   * (exclusivo).
   *
   * @return true se for de dia; false se for de noite
   */
  public boolean isDaytime() {
    return !time.isBefore(DAY_START) && time.isBefore(NIGHT_START);
  }
}
