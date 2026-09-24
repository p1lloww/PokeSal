package br.edu.ucsal.tqs.pokesal.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.ElementType;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.Blaze;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.SolarPower;
import br.edu.ucsal.tqs.pokesal.entities.passives.neutral.Intimidate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DamageCalculatorServiceTest {
  private static final double DELTA = 0.0001;

  private static final Battleground TERRENO_NEUTRO = new CentralFlowerbed();

  private PokeSal criarPokeSal(ElementType tipo, int hp, int atk, int def, Passive passive) {
    Move tackle = new Move("Tackle", "Ataque básico.", tipo, 40);
    return new PokeSal("Teste", "PokeSal de teste", hp, atk, def, 50, tipo,
        List.of(tackle), passive);
  }

  private Move golpe(ElementType tipo, int poder) {
    return new Move("Golpe", "Golpe de teste.", tipo, poder);
  }

  @ParameterizedTest(name = "atk={0}, def={1}, poder={2} -> dano {3}")
  @CsvSource({
      "1, 1, 1, 0.2",
      "1, 1, 50, 10.0",
      "2, 1, 50, 20.0",
      "1, 2, 50, 5.0",
      "100, 100, 50, 10.0",
      "1000, 1, 50, 10000.0",
      "1, 1000, 50, 0.01"
  })
  @DisplayName("Dano deve seguir a formula nos valores limite de ATK, DEF e poder")
  void testCalculoDanoBoundaryValues(int atk, int def, int poder, double esperado) {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, atk, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(ElementType.FIRE, 50, 50, def, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, poder), TERRENO_NEUTRO);

    assertEquals(esperado, dano, DELTA);
  }

  @Test
  @DisplayName("Dano igual ao HP deve deixar o defensor com exatamente 0")
  void danoIgualAoHpDeixaDefensorComZero() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(ElementType.FIRE, 10, 50, 10, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, 10), TERRENO_NEUTRO);
    defensor.takeDamage(dano);

    assertEquals(10.0, dano, DELTA);
    assertEquals(0, defensor.getHp());
  }

  @Test
  @DisplayName("Dano igual ao HP menos 1 deve deixar o defensor com 1")
  void danoUmAbaixoDoHpDeixaDefensorComUm() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(ElementType.FIRE, 11, 50, 10, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, 10), TERRENO_NEUTRO);
    defensor.takeDamage(dano);

    assertEquals(1, defensor.getHp());
  }

  @Test
  @DisplayName("Dano maior que o HP nao deve deixar o defensor com HP negativo")
  void danoMaiorQueOHpNaoDeixaHpNegativo() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 1000, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(ElementType.FIRE, 10, 50, 1, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, 100), TERRENO_NEUTRO);
    defensor.takeDamage(dano);

    assertEquals(0, defensor.getHp());
  }

  @Test
  void golpeComPoderZeroDeveSerRejeitado() {
    assertThrows(IllegalArgumentException.class, () -> golpe(ElementType.FIRE, 0));
  }

  @ParameterizedTest(name = "{0} contra {1} -> dano {2}")
  @CsvSource({
      "FIRE, PLANT, 20.0",
      "FIRE, WATER, 5.0",
      "FIRE, FIRE, 10.0"
  })
  void danoDeveAplicarMultiplicadorDeTipo(ElementType tipoGolpe, ElementType tipoDefensor,
      double esperado) {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(tipoDefensor, 50, 50, 50, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(tipoGolpe, 50), TERRENO_NEUTRO);

    assertEquals(esperado, dano, DELTA);
  }

  @Test
  @DisplayName("Tipo, terreno e Solar Power devem se acumular no dano")
  void tipoTerrenoESolarPowerSeAcumulam() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new SolarPower());
    PokeSal defensor = criarPokeSal(ElementType.PLANT, 50, 50, 50, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, 50), new HotAsphalt());

    assertEquals(34.5, dano, DELTA);
  }

  @Test
  void hotAsphaltSemSolarPowerAplicaSoOBonusDoTerreno() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Blaze());
    PokeSal defensor = criarPokeSal(ElementType.PLANT, 50, 50, 50, new Intimidate());

    double dano = DamageCalculatorService.calculateDamage(
        atacante, defensor, golpe(ElementType.FIRE, 50), new HotAsphalt());

    assertEquals(23.0, dano, DELTA);
  }

  @Test
  void deveLancarExcecaoComArgumentosNulos() {
    PokeSal atacante = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Intimidate());
    PokeSal defensor = criarPokeSal(ElementType.FIRE, 50, 50, 50, new Intimidate());
    Move move = golpe(ElementType.FIRE, 50);

    assertThrows(NullPointerException.class, () ->
        DamageCalculatorService.calculateDamage(null, defensor, move, TERRENO_NEUTRO));
    assertThrows(NullPointerException.class, () ->
        DamageCalculatorService.calculateDamage(atacante, null, move, TERRENO_NEUTRO));
    assertThrows(NullPointerException.class, () ->
        DamageCalculatorService.calculateDamage(atacante, defensor, null, TERRENO_NEUTRO));
    assertThrows(NullPointerException.class, () ->
        DamageCalculatorService.calculateDamage(atacante, defensor, move, null));
  }
}
