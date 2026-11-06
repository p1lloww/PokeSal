package br.edu.ucsal.tqs.pokesal.services.battleground;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Weather;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RandomWeatherProviderTest {
  @Mock
  private RandomGenerator randomGenerator;

  @ParameterizedTest(name = "sorteio {0} -> {1}")
  @CsvSource({"0, SUNNY", "1, CLOUDY", "2, RAINY"})
  @DisplayName("Clima deve corresponder ao indice sorteado pelo gerador")
  void climaCorrespondeAoSorteio(int sorteio, Weather esperado) {
    when(randomGenerator.nextInt(3)).thenReturn(sorteio);

    assertEquals(esperado, new RandomWeatherProvider(randomGenerator).getCurrentWeather());
  }

  @Test
  @DisplayName("Clima deve ser sorteado uma unica vez e se manter durante a execucao")
  void climaSorteadoUmaUnicaVez() {
    when(randomGenerator.nextInt(3)).thenReturn(2);
    RandomWeatherProvider provider = new RandomWeatherProvider(randomGenerator);

    provider.getCurrentWeather();
    provider.getCurrentWeather();

    assertEquals(Weather.RAINY, provider.getCurrentWeather());
    verify(randomGenerator, times(1)).nextInt(3);
  }

  @Test
  @DisplayName("Construtor deve rejeitar gerador nulo")
  void construtorRejeitaGeradorNulo() {
    assertThrows(NullPointerException.class, () -> new RandomWeatherProvider(null));
  }
}
