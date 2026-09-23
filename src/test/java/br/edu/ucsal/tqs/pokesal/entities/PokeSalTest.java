package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.Blaze;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

public class PokeSalTest {
  private static final Move TACKLE = new Move("Tackle", "Ataque básico.", ElementType.FIRE, 40);
  private static final Passive BLAZE = new Blaze();

  static Stream<Arguments> fornecerPokeSals() {
    return Stream.of(
        Arguments.of(PokeSalCatalog.createBulbaSal()),
        Arguments.of(PokeSalCatalog.createCharSal()),
        Arguments.of(PokeSalCatalog.createSquirtSal()),
        Arguments.of(PokeSalCatalog.createTotoSal()),
        Arguments.of(PokeSalCatalog.createCyndaSal()),
        Arguments.of(PokeSalCatalog.createChikoSal())
    );
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("PokeSal nao deve estar com hp negativo")
  void pokeSalNaoDeveEstarComHpNegativo(PokeSal pokeSal) {
    pokeSal.takeDamage(1000.0);

    assertEquals(0, pokeSal.getHp());
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("PokeSal Nao deve estar com hp maior que maxHp")
  void pokeSalNaoDeveEstarComHpMaiorQueMaxHp(PokeSal pokeSal) {
    pokeSal.takeDamage(10.0);

    pokeSal.heal(1000.0);

    assertEquals(pokeSal.getMaxHp(), pokeSal.getHp());
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("heal deve lancar excecao se o valor de amount for negativo")
  void healDeveLancarExcecaoSeOValorDeAmountForNegativo(PokeSal pokeSal) {
    assertThrows(IllegalArgumentException.class, () -> pokeSal.heal(-100.0));
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("takeDamage deve lancar excecao se o valor de amount for negativo")
  void takeDamageDeveLancarExcecaoSeOValorDeAmountForNegativo(PokeSal pokeSal) {
    assertThrows(IllegalArgumentException.class, () -> pokeSal.takeDamage(-100.0));
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("Buff seguido do multiplicador inverso deve devolver os atributos originais")
  void buffSeguidoDoInversoDevolveAtributosOriginais(PokeSal pokeSal) {
    int atk = pokeSal.getAtk();
    int def = pokeSal.getDef();
    int spd = pokeSal.getSpd();

    pokeSal.applyAttackBuff(0.5);
    pokeSal.applyAttackBuff(2.0);
    pokeSal.applyDefenseBuff(1.5);
    pokeSal.applyDefenseBuff(1 / 1.5);
    pokeSal.applySpeedBuff(1.3);
    pokeSal.applySpeedBuff(1 / 1.3);

    assertEquals(atk, pokeSal.getAtk());
    assertEquals(def, pokeSal.getDef());
    assertEquals(spd, pokeSal.getSpd());
  }

  @ParameterizedTest
  @MethodSource("fornecerPokeSals")
  @DisplayName("Buffs devem lancar excecao com multiplicador negativo")
  void buffsDevemLancarExcecaoComMultiplicadorNegativo(PokeSal pokeSal) {
    assertThrows(IllegalArgumentException.class, () -> pokeSal.applyAttackBuff(-1.0));
    assertThrows(IllegalArgumentException.class, () -> pokeSal.applyDefenseBuff(-1.0));
    assertThrows(IllegalArgumentException.class, () -> pokeSal.applySpeedBuff(-1.0));
  }

  @Test
  void naoDeveAplicarSegundoStatusQuandoJaTemUm() {
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    int atkInicial = squirtSal.getAtk();

    squirtSal.applyStatus(new Poison());
    squirtSal.applyStatus(new Burn());

    assertInstanceOf(Poison.class, squirtSal.getStatusCondition());
    assertEquals(atkInicial, squirtSal.getAtk());
  }

  @Test
  void clearStatusSemStatusNaoDeveLancarExcecao() {
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();

    assertDoesNotThrow(squirtSal::clearStatus);
    assertNull(squirtSal.getStatusCondition());
  }

  @Test
  void applyStatusDeveLancarExcecaoComStatusNulo() {
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();

    assertThrows(NullPointerException.class, () -> squirtSal.applyStatus(null));
  }

  @Test
  void receiveEffectDeveLancarExcecaoComEfeitoNulo() {
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();

    assertThrows(NullPointerException.class, () -> squirtSal.receiveEffect(null, 1));
  }

  @ParameterizedTest(name = "hp={0}, atk={1}, def={2}, spd={3}")
  @CsvSource({
      "0, 50, 50, 50",
      "50, 0, 50, 50",
      "50, 50, 0, 50",
      "50, 50, 50, 0",
      "-1, 50, 50, 50"
  })
  void construtorDeveLancarExcecaoComAtributoNaoPositivo(int hp, int atk, int def, int spd) {
    assertThrows(IllegalArgumentException.class, () -> new PokeSal("Teste", "Descricao",
        hp, atk, def, spd, ElementType.FIRE, List.of(TACKLE), BLAZE));
  }

  @Test
  void construtorDeveLancarExcecaoComCamposNulos() {
    List<Move> moves = List.of(TACKLE);

    assertThrows(NullPointerException.class, () -> new PokeSal(null, "Descricao",
        50, 50, 50, 50, ElementType.FIRE, moves, BLAZE));
    assertThrows(NullPointerException.class, () -> new PokeSal("Teste", null,
        50, 50, 50, 50, ElementType.FIRE, moves, BLAZE));
    assertThrows(NullPointerException.class, () -> new PokeSal("Teste", "Descricao",
        50, 50, 50, 50, null, moves, BLAZE));
    assertThrows(NullPointerException.class, () -> new PokeSal("Teste", "Descricao",
        50, 50, 50, 50, ElementType.FIRE, moves, null));
  }

  @Test
  void construtorDeveLancarExcecaoSemMovimentos() {
    assertThrows(IllegalArgumentException.class, () -> new PokeSal("Teste", "Descricao",
        50, 50, 50, 50, ElementType.FIRE, null, BLAZE));
    assertThrows(IllegalArgumentException.class, () -> new PokeSal("Teste", "Descricao",
        50, 50, 50, 50, ElementType.FIRE, List.of(), BLAZE));
  }

  @Test
  void construtorDeveLancarExcecaoComMaisDeQuatroMovimentos() {
    List<Move> moves = Collections.nCopies(5, TACKLE);

    assertThrows(IllegalArgumentException.class, () -> new PokeSal("Teste", "Descricao",
        50, 50, 50, 50, ElementType.FIRE, moves, BLAZE));
  }
}
