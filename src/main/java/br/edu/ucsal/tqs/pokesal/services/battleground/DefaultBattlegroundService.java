package br.edu.ucsal.tqs.pokesal.services.battleground;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.BattlegroundConditions;
import java.time.Clock;
import java.time.LocalTime;

/**
 * Implementação de BattlegroundService que monta as condições atuais a partir de um Clock e de um
 * WeatherProvider, e pergunta ao próprio terreno se ele está ativo nessas condições.
 */
public class DefaultBattlegroundService implements BattlegroundService {

  private final Clock clock;
  private final WeatherProvider weatherProvider;

  /**
   * Cria um novo serviço de terreno com o relógio e o provedor de clima informados.
   *
   * @param clock           o relógio usado para obter o horário atual
   * @param weatherProvider o provedor do clima atual
   * @throws NullPointerException se clock ou weatherProvider forem nulos
   */
  public DefaultBattlegroundService(Clock clock, WeatherProvider weatherProvider) {
    if (clock == null) {
      throw new NullPointerException("O relógio não pode ser nulo");
    }
    if (weatherProvider == null) {
      throw new NullPointerException("O provedor de clima não pode ser nulo");
    }

    this.clock = clock;
    this.weatherProvider = weatherProvider;
  }

  @Override
  public boolean isActive(Battleground battleground) {
    if (battleground == null) {
      throw new NullPointerException("O terreno não pode ser nulo");
    }

    BattlegroundConditions conditions = new BattlegroundConditions(
        LocalTime.now(clock), weatherProvider.getCurrentWeather());
    return battleground.isActive(conditions);
  }
}
