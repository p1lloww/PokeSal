package br.edu.ucsal.tqs.pokesal.services.battleground;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Weather;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;

/**
 * Implementação de WeatherProvider usada no jogo real enquanto não há um serviço de clima de
 * verdade: sorteia o clima uma única vez, na criação, e o mantém durante toda a execução.
 */
public class RandomWeatherProvider implements WeatherProvider {

  private final Weather weather;

  /**
   * Cria um novo provedor, sorteando o clima com o gerador informado.
   *
   * @param randomGenerator o gerador usado para sortear o clima
   * @throws NullPointerException se randomGenerator for nulo
   */
  public RandomWeatherProvider(RandomGenerator randomGenerator) {
    if (randomGenerator == null) {
      throw new NullPointerException("O gerador aleatório não pode ser nulo");
    }

    Weather[] options = Weather.values();
    this.weather = options[randomGenerator.nextInt(options.length)];
  }

  @Override
  public Weather getCurrentWeather() {
    return weather;
  }
}
