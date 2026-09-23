package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water.WaterPuddle;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.Blaze;
import br.edu.ucsal.tqs.pokesal.entities.passives.fire.SolarPower;
import br.edu.ucsal.tqs.pokesal.entities.passives.grass.LeafGuard;
import br.edu.ucsal.tqs.pokesal.entities.passives.grass.Overgrow;
import br.edu.ucsal.tqs.pokesal.entities.passives.neutral.Intimidate;
import br.edu.ucsal.tqs.pokesal.entities.passives.water.RainDish;
import br.edu.ucsal.tqs.pokesal.entities.passives.water.Torrent;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Burn;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Paralyzed;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PassiveTest {

  private static final double DELTA = 0.0001;
  private static final double BUFF_ATK = 1.5;
  private static final int MAX_HP = 90;

  static Stream<Arguments> fornecerPassivasDeHpBaixo() {
    return Stream.of(
        Arguments.of(new Blaze(), ElementType.FIRE),
        Arguments.of(new Overgrow(), ElementType.PLANT),
        Arguments.of(new Torrent(), ElementType.WATER)
    );
  }

  static Stream<Arguments> fornecerPassivasSemBonusDeAtaque() {
    return Stream.of(
        Arguments.of(new Blaze()),
        Arguments.of(new Overgrow()),
        Arguments.of(new Torrent()),
        Arguments.of(new Intimidate()),
        Arguments.of(new RainDish()),
        Arguments.of(new LeafGuard())
    );
  }

  static Stream<Arguments> fornecerTerrenosSemHotAsphalt() {
    return Stream.of(
        Arguments.of(new WaterPuddle()),
        Arguments.of(new CentralFlowerbed())
    );
  }

  static Stream<Arguments> fornecerPassivasQueNaoBloqueiamStatus() {
    return Stream.of(
        Arguments.of(new Blaze()),
        Arguments.of(new Overgrow()),
        Arguments.of(new Torrent()),
        Arguments.of(new Intimidate()),
        Arguments.of(new RainDish()),
        Arguments.of(new SolarPower())
    );
  }

  private PokeSal mockPokeSal(ElementType tipo, int hp) {
    PokeSal pokeSal = mock(PokeSal.class);
    when(pokeSal.getElementType()).thenReturn(tipo);
    when(pokeSal.getHp()).thenReturn(hp);
    when(pokeSal.getMaxHp()).thenReturn(MAX_HP);
    return pokeSal;
  }

  @Test
  void blazeDeveAtivarQuandoHpAbaixoDeUmTerco() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    int atkInicial = charSal.getAtk();

    charSal.takeDamage(27.0);
    charSal.getPassive().onDamageTaken(charSal, null, 27.0);

    assertEquals((int) (atkInicial * BUFF_ATK), charSal.getAtk());
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Buff de ATK deve ativar quando o HP cruza abaixo de 1/3")
  void buffDeveAtivarQuandoHpCruzaAbaixoDeUmTerco(Passive passive, ElementType tipo) {
    PokeSal pokeSal = mockPokeSal(tipo, 29);

    passive.onDamageTaken(pokeSal, null, 10.0);

    verify(pokeSal).applyAttackBuff(BUFF_ATK);
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Buff de ATK nao deve ativar com HP exatamente em 1/3")
  void buffNaoDeveAtivarComHpExatamenteEmUmTerco(Passive passive, ElementType tipo) {
    PokeSal pokeSal = mockPokeSal(tipo, 30);

    passive.onDamageTaken(pokeSal, null, 10.0);

    verify(pokeSal, never()).applyAttackBuff(anyDouble());
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Buff de ATK nao deve acumular com danos seguidos abaixo de 1/3")
  void buffNaoDeveAcumularComDanosSeguidos(Passive passive, ElementType tipo) {
    PokeSal pokeSal = mockPokeSal(tipo, 20);

    passive.onDamageTaken(pokeSal, null, 5.0);
    passive.onDamageTaken(pokeSal, null, 5.0);
    passive.onDamageTaken(pokeSal, null, 5.0);

    verify(pokeSal, times(1)).applyAttackBuff(anyDouble());
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Buff de ATK deve desativar quando o HP volta a subir acima de 1/3")
  void buffDeveDesativarQuandoHpVoltaASubir(Passive passive, ElementType tipo) {
    PokeSal pokeSal = mockPokeSal(tipo, 20);
    passive.onDamageTaken(pokeSal, null, 10.0);

    when(pokeSal.getHp()).thenReturn(60);
    passive.onDamageTaken(pokeSal, null, 0.0);

    verify(pokeSal).applyAttackBuff(BUFF_ATK);
    verify(pokeSal).applyAttackBuff(1 / BUFF_ATK);
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Buff de ATK deve poder reativar depois de desativado")
  void buffDeveReativarDepoisDeDesativado(Passive passive, ElementType tipo) {
    PokeSal pokeSal = mockPokeSal(tipo, 20);
    passive.onDamageTaken(pokeSal, null, 10.0);
    when(pokeSal.getHp()).thenReturn(60);
    passive.onDamageTaken(pokeSal, null, 0.0);

    when(pokeSal.getHp()).thenReturn(10);
    passive.onDamageTaken(pokeSal, null, 50.0);

    verify(pokeSal, times(2)).applyAttackBuff(BUFF_ATK);
    verify(pokeSal, times(1)).applyAttackBuff(1 / BUFF_ATK);
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasDeHpBaixo")
  @DisplayName("Passiva de HP baixo nao ativa em PokeSal de outro tipo")
  void buffNaoDeveAtivarEmPokeSalDeOutroTipo(Passive passive, ElementType tipo) {
    for (ElementType outroTipo : ElementType.values()) {
      if (outroTipo == tipo) {
        continue;
      }
      PokeSal pokeSal = mock(PokeSal.class);
      when(pokeSal.getElementType()).thenReturn(outroTipo);

      passive.onDamageTaken(pokeSal, null, 80.0);

      verify(pokeSal, never()).applyAttackBuff(anyDouble());
    }
  }

  @Test
  void intimidateReduzAtkDoOponenteAoEntrarEmCampo() {
    PokeSal cyndaSal = PokeSalCatalog.createCyndaSal();
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    int atkCyndaSal = cyndaSal.getAtk();

    new Intimidate().onSwitchIn(cyndaSal, squirtSal);

    assertEquals(32, squirtSal.getAtk());
    assertEquals(atkCyndaSal, cyndaSal.getAtk());
  }

  @Test
  void intimidateNaoMexeNoProprioPokeSal() {
    PokeSal self = mock(PokeSal.class);
    PokeSal oponente = mock(PokeSal.class);

    new Intimidate().onSwitchIn(self, oponente);

    verify(oponente).applyAttackBuff(0.67);
    verifyNoInteractions(self);
  }

  @Test
  void rainDishAumentaDefEmVintePorCentoAoEntrarEmCampo() {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    int defCharSal = charSal.getDef();

    new RainDish().onSwitchIn(totoSal, charSal);

    assertEquals(76, totoSal.getDef());
    assertEquals(defCharSal, charSal.getDef());
  }

  @Test
  void leafGuardImpedeAplicacaoDeStatus() {
    PokeSal chikoSal = PokeSalCatalog.createChikoSal();

    chikoSal.applyStatus(new Poison());
    chikoSal.applyStatus(new Burn());
    chikoSal.applyStatus(new Paralyzed());

    assertTrue(new LeafGuard().preventsStatusApplication(chikoSal));
    assertNull(chikoSal.getStatusCondition());
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasQueNaoBloqueiamStatus")
  @DisplayName("Passivas diferentes de Leaf Guard nao bloqueiam status")
  void passivasDiferentesDeLeafGuardNaoBloqueiamStatus(Passive passive) {
    PokeSal pokeSal = mock(PokeSal.class);

    assertFalse(passive.preventsStatusApplication(pokeSal));
  }

  @Test
  void solarPowerAumentaAtaqueEmHotAsphalt() {
    PokeSal pokeSal = mock(PokeSal.class);

    double multiplicador = new SolarPower().onAttack(pokeSal, new HotAsphalt());

    assertTrue(multiplicador > 1.0);
    assertEquals(1.5, multiplicador, DELTA);
  }

  @ParameterizedTest
  @MethodSource("fornecerTerrenosSemHotAsphalt")
  @DisplayName("Solar Power deve retornar 1.0 fora do Hot Asphalt")
  void solarPowerNaoAlteraAtaqueForaDeHotAsphalt(Battleground terreno) {
    PokeSal pokeSal = mock(PokeSal.class);

    double multiplicador = new SolarPower().onAttack(pokeSal, terreno);

    assertEquals(1.0, multiplicador, DELTA);
  }

  @Test
  void solarPowerCobraHpNoFimDoTurnoQuandoAtivo() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    SolarPower solarPower = new SolarPower();

    solarPower.onAttack(charSal, new HotAsphalt());
    solarPower.onTurnEnd(charSal);

    assertEquals(34, charSal.getHp());
  }

  @ParameterizedTest
  @MethodSource("fornecerTerrenosSemHotAsphalt")
  @DisplayName("Solar Power nao cobra HP quando atacou fora do Hot Asphalt")
  void solarPowerNaoCobraHpForaDeHotAsphalt(Battleground terreno) {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    SolarPower solarPower = new SolarPower();

    solarPower.onAttack(charSal, terreno);
    solarPower.onTurnEnd(charSal);

    assertEquals(charSal.getMaxHp(), charSal.getHp());
  }

  @Test
  void solarPowerNaoCobraHpSemTerAtacado() {
    PokeSal charSal = PokeSalCatalog.createCharSal();

    new SolarPower().onTurnEnd(charSal);

    assertEquals(charSal.getMaxHp(), charSal.getHp());
  }

  @Test
  void solarPowerDesativaQuandoOTerrenoMuda() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    SolarPower solarPower = new SolarPower();

    solarPower.onAttack(charSal, new HotAsphalt());
    solarPower.onAttack(charSal, new WaterPuddle());
    solarPower.onTurnEnd(charSal);

    assertEquals(charSal.getMaxHp(), charSal.getHp());
  }

  @ParameterizedTest
  @MethodSource("fornecerPassivasSemBonusDeAtaque")
  @DisplayName("Passivas diferentes de Solar Power nao alteram o dano em nenhum terreno")
  void passivasSemBonusNaoAlteramDanoEmNenhumTerreno(Passive passive) {
    PokeSal pokeSal = mock(PokeSal.class);

    List<Battleground> terrenos =
        List.of(new HotAsphalt(), new WaterPuddle(), new CentralFlowerbed());

    for (Battleground terreno : terrenos) {
      assertEquals(1.0, passive.onAttack(pokeSal, terreno), DELTA);
    }
  }
}
