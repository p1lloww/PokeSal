package br.edu.ucsal.tqs.pokesal.services.battleground;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Weather;

/**
 * Fonte externa do clima atual no local da batalha.
 */
public interface WeatherProvider {

  /**
   * Retorna o clima atual.
   *
   * @return o clima atual
   */
  Weather getCurrentWeather();
}
