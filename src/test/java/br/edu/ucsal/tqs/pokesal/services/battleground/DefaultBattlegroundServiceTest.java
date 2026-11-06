package br.edu.ucsal.tqs.pokesal.services.battleground;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Weather;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water.WaterPuddle;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DefaultBattlegroundServiceTest {
  private static final Instant MEIO_DIA = Instant.parse("2026-01-01T12:00:00Z");
  private static final Instant DEZ_DA_NOITE = Instant.parse("2026-01-01T22:00:00Z");

  @Mock
  private Clock clock;

  @Mock
  private WeatherProvider weatherProvider;

  @InjectMocks
  private DefaultBattlegroundService service;

  private void relogioEm(Instant primeiro, Instant... seguintes) {
    when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    when(clock.instant()).thenReturn(primeiro, seguintes);
  }

  @Test
  @DisplayName("Hot Asphalt deve estar ativo ao meio-dia e inativo as 22h")
  void testAsfaltoQuenteApenasDuranteODia() {
    HotAsphalt asfalto = new HotAsphalt();
    relogioEm(MEIO_DIA, DEZ_DA_NOITE);
    when(weatherProvider.getCurrentWeather()).thenReturn(Weather.SUNNY);

    boolean ativoDeDia = service.isActive(asfalto);
    boolean ativoDeNoite = service.isActive(asfalto);

    assertTrue(ativoDeDia);
    assertFalse(ativoDeNoite);
    verify(clock, times(2)).instant();
  }

  @ParameterizedTest(name = "{0} -> ativo: {1}")
  @CsvSource({
      "2026-01-01T05:59:59Z, false",
      "2026-01-01T06:00:00Z, true",
      "2026-01-01T17:59:59Z, true",
      "2026-01-01T18:00:00Z, false"
  })
  @DisplayName("Hot Asphalt deve respeitar o limite das 6h as 18h")
  void asfaltoQuenteRespeitaOsLimitesDoDia(String horario, boolean esperado) {
    relogioEm(Instant.parse(horario));
    when(weatherProvider.getCurrentWeather()).thenReturn(Weather.CLOUDY);

    assertEquals(esperado, service.isActive(new HotAsphalt()));
  }

  @ParameterizedTest(name = "{0} -> ativo: {1}")
  @CsvSource({"SUNNY, false", "CLOUDY, true", "RAINY, true"})
  @DisplayName("Water Puddle deve ficar inativo apenas com sol")
  void pocaDeAguaDependeDoClima(Weather clima, boolean esperado) {
    relogioEm(MEIO_DIA);
    when(weatherProvider.getCurrentWeather()).thenReturn(clima);

    assertEquals(esperado, service.isActive(new WaterPuddle()));
  }

  @Test
  @DisplayName("Central Flowerbed deve ficar ativo a noite e com qualquer clima")
  void canteiroCentralSempreAtivo() {
    relogioEm(DEZ_DA_NOITE);
    when(weatherProvider.getCurrentWeather()).thenReturn(Weather.RAINY);

    assertTrue(service.isActive(new CentralFlowerbed()));
  }

  @Test
  @DisplayName("Servico deve rejeitar terreno nulo e colaboradores nulos")
  void rejeitaNulos() {
    assertThrows(NullPointerException.class, () -> service.isActive(null));
    assertThrows(NullPointerException.class,
        () -> new DefaultBattlegroundService(null, weatherProvider));
    assertThrows(NullPointerException.class, () -> new DefaultBattlegroundService(clock, null));
  }
}
