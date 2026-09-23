package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.effects.BurnHeal;
import br.edu.ucsal.tqs.pokesal.entities.effects.Effect;
import br.edu.ucsal.tqs.pokesal.entities.effects.HealEffect;
import br.edu.ucsal.tqs.pokesal.entities.effects.ParalyzeHeal;
import br.edu.ucsal.tqs.pokesal.entities.effects.SpeedBuffEffect;
import br.edu.ucsal.tqs.pokesal.entities.effects.SuperAntidote;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Paralyzed;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class EffectTest {
  // TotoSal tem 50 de HP maximo, entao com 20 de HP a cura fraca (10%) vai pra 25
  // e a forte (25%) vai pra 32
  private static final double DANO = 30.0;
  private static final int HP_APOS_CURA_FRACA = 25;
  private static final int HP_APOS_CURA_FORTE = 32;

  static Stream<Arguments> fornecerCurasDeStatus() {
    return Stream.of(
        Arguments.of(new SuperAntidote(), new Poison()),
        Arguments.of(new BurnHeal(), new Burn()),
        Arguments.of(new ParalyzeHeal(), new Paralyzed())
    );
  }

  static Stream<Arguments> fornecerCurasComStatusErrado() {
    return Stream.of(
        Arguments.of(new SuperAntidote(), new Burn()),
        Arguments.of(new SuperAntidote(), new Paralyzed()),
        Arguments.of(new BurnHeal(), new Poison()),
        Arguments.of(new BurnHeal(), new Paralyzed()),
        Arguments.of(new ParalyzeHeal(), new Poison()),
        Arguments.of(new ParalyzeHeal(), new Burn())
    );
  }

  static Stream<Arguments> fornecerEfeitosInstantaneos() {
    return Stream.of(
        Arguments.of(new SuperAntidote()),
        Arguments.of(new BurnHeal()),
        Arguments.of(new ParalyzeHeal()),
        Arguments.of(new HealEffect(0.2))
    );
  }

  private PokeSal criarTotoSalMachucado() {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();
    totoSal.takeDamage(DANO);
    return totoSal;
  }

  @ParameterizedTest
  @MethodSource("fornecerCurasDeStatus")
  @DisplayName("Item de status deve curar pouco quando o PokeSal nao tem nenhum status")
  void deveCurarPoucoSemStatus(Effect effect, StatusCondition status) {
    PokeSal totoSal = criarTotoSalMachucado();

    effect.applyEffect(totoSal);

    assertEquals(HP_APOS_CURA_FRACA, totoSal.getHp());
    assertNull(totoSal.getStatusCondition());
  }

  @ParameterizedTest
  @MethodSource("fornecerCurasDeStatus")
  @DisplayName("Item de status deve curar mais e remover o status correspondente")
  void deveCurarMaisERemoverStatusCorrespondente(Effect effect, StatusCondition status) {
    PokeSal totoSal = criarTotoSalMachucado();
    totoSal.applyStatus(status);

    effect.applyEffect(totoSal);

    assertEquals(HP_APOS_CURA_FORTE, totoSal.getHp());
    assertNull(totoSal.getStatusCondition());
  }

  @ParameterizedTest
  @MethodSource("fornecerCurasComStatusErrado")
  @DisplayName("Item de status deve curar pouco e manter o status quando ele for diferente")
  void deveCurarPoucoQuandoStatusForDiferente(Effect effect, StatusCondition status) {
    PokeSal totoSal = criarTotoSalMachucado();
    totoSal.applyStatus(status);

    effect.applyEffect(totoSal);

    assertEquals(HP_APOS_CURA_FRACA, totoSal.getHp());
    assertSame(status, totoSal.getStatusCondition());
  }

  @Test
  void burnHealDevolveOAtkReduzidoPelaQueimadura() {
    PokeSal totoSal = criarTotoSalMachucado();
    int atkInicial = totoSal.getAtk();
    totoSal.applyStatus(new Burn());

    new BurnHeal().applyEffect(totoSal);

    assertEquals(atkInicial, totoSal.getAtk());
  }

  @Test
  void paralyzeHealDevolveOSpdReduzidoPelaParalisia() {
    PokeSal totoSal = criarTotoSalMachucado();
    int spdInicial = totoSal.getSpd();
    totoSal.applyStatus(new Paralyzed());

    new ParalyzeHeal().applyEffect(totoSal);

    assertEquals(spdInicial, totoSal.getSpd());
  }

  @ParameterizedTest
  @MethodSource("fornecerCurasDeStatus")
  @DisplayName("Cura forte nao deve passar do HP maximo")
  void curaForteNaoPassaDoHpMaximo(Effect effect, StatusCondition status) {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();
    totoSal.takeDamage(5.0);
    totoSal.applyStatus(status);

    effect.applyEffect(totoSal);

    assertEquals(totoSal.getMaxHp(), totoSal.getHp());
  }

  @Test
  void healEffectCuraAPorcentagemDoHpMaximo() {
    PokeSal totoSal = criarTotoSalMachucado();

    new HealEffect(0.2).applyEffect(totoSal);

    assertEquals(30, totoSal.getHp());
  }

  @Test
  void healEffectNaoPassaDoHpMaximo() {
    PokeSal totoSal = criarTotoSalMachucado();

    new HealEffect(0.5).applyEffect(totoSal);
    new HealEffect(0.5).applyEffect(totoSal);

    assertEquals(totoSal.getMaxHp(), totoSal.getHp());
  }

  @Test
  void healEffectNaoRemoveStatus() {
    PokeSal totoSal = criarTotoSalMachucado();
    Poison poison = new Poison();
    totoSal.applyStatus(poison);

    new HealEffect(0.5).applyEffect(totoSal);

    assertSame(poison, totoSal.getStatusCondition());
  }

  @ParameterizedTest
  @ValueSource(doubles = {0.0, -0.1, -10.0})
  void healEffectDeveLancarExcecaoComCuraNaoPositiva(double quantidade) {
    assertThrows(IllegalArgumentException.class, () -> new HealEffect(quantidade));
  }

  @Test
  void speedBuffEffectAumentaSpdEmTrintaPorCento() {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();

    new SpeedBuffEffect().applyEffect(totoSal);

    assertEquals(55, totoSal.getSpd());
  }

  @Test
  void speedBuffEffectRemoveAplicandoOMultiplicadorInverso() {
    PokeSal pokeSal = mock(PokeSal.class);
    SpeedBuffEffect effect = new SpeedBuffEffect();

    effect.applyEffect(pokeSal);
    effect.removeEffect(pokeSal);

    verify(pokeSal).applySpeedBuff(1.3);
    verify(pokeSal).applySpeedBuff(1 / 1.3);
  }

  @Test
  void speedBuffEffectEhTemporario() {
    assertTrue(new SpeedBuffEffect().isTemporary());
  }

  @Test
  void speedBuffEffectExpiraNoTurnoSeguinte() {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();
    SpeedBuffEffect effect = new SpeedBuffEffect();

    totoSal.receiveEffect(effect, 3);

    assertEquals(List.of(effect), totoSal.getActiveEffects().get(4));

    totoSal.expireEffects(4);

    assertTrue(totoSal.getActiveEffects().isEmpty());
    assertTrue(totoSal.getSpd() < 55);
  }

  @ParameterizedTest
  @MethodSource("fornecerEfeitosInstantaneos")
  @DisplayName("Efeitos de cura nao sao temporarios e nao ficam agendados")
  void efeitosDeCuraNaoSaoTemporarios(Effect effect) {
    PokeSal totoSal = criarTotoSalMachucado();

    totoSal.receiveEffect(effect, 1);

    assertFalse(effect.isTemporary());
    assertTrue(totoSal.getActiveEffects().isEmpty());
  }
}
