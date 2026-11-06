package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.MoveAction;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.DamageResult;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MoveActionTest {
  private final Battleground terreno = new HotAsphalt();

  @Mock
  private RandomGenerator randomGenerator;

  @Mock
  private BattlegroundService battlegroundService;

  @Mock
  private BattleLogger battleLogger;

  private DamageCalculatorService damageCalculatorService;

  @BeforeEach
  void setUp() {
    damageCalculatorService = new DamageCalculatorService(randomGenerator, battlegroundService);
  }

  private void semSorte() {
    when(randomGenerator.nextInt(anyInt())).thenReturn(0);
    when(randomGenerator.nextDouble()).thenReturn(0.99);
    when(battlegroundService.isActive(any())).thenReturn(true);
  }

  private MoveAction acao(Move move) {
    return new MoveAction(move, damageCalculatorService, randomGenerator, battleLogger);
  }

  private Move tackle(PokeSal pokeSal) {
    return pokeSal.getMoves().get(0);
  }

  private Move golpeVenenoso(double chance) {
    return new Move("Veneno", "Golpe de teste.", ElementType.PLANT, 45, new Poison(), chance);
  }

  @Test
  void golpeComChanceTotalAplicaOStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    semSorte();

    acao(golpeVenenoso(1.0)).execute(bulbaSal, charSal, terreno);

    assertInstanceOf(Poison.class, charSal.getStatusCondition());
  }

  @Test
  void golpeComChanceZeroNuncaAplicaOStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    semSorte();

    acao(golpeVenenoso(0.0)).execute(bulbaSal, charSal, terreno);

    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeSemStatusNaoAplicaNenhumStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    semSorte();

    acao(tackle(bulbaSal)).execute(bulbaSal, charSal, terreno);

    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeQueDerrubaNaoAplicaStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    semSorte();
    charSal.takeDamage(38.0);

    acao(golpeVenenoso(1.0)).execute(bulbaSal, charSal, terreno);

    assertEquals(0, charSal.getHp());
    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeAvisaAPassivaDoDefensorSobreODano() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    semSorte();
    charSal.takeDamage(23.0);

    acao(tackle(bulbaSal)).execute(bulbaSal, charSal, terreno);

    assertEquals(11, charSal.getHp());
    assertEquals(78, charSal.getAtk());
  }

  @Test
  void construtorDeveLancarExcecaoComGolpeNulo() {
    assertThrows(NullPointerException.class,
        () -> new MoveAction(null, damageCalculatorService, randomGenerator, battleLogger));
  }

  @Test
  @DisplayName("Golpe que falha na precisao deve causar dano zero e manter o HP do alvo")
  void testGolpeErraQuandoPrecisaoFalha() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    Move golpeImpreciso =
        new Move("Golpe Impreciso", "Golpe de teste.", ElementType.PLANT, 45, null, 0.0, 70);
    // Precisao 70: qualquer sorteio de 70 a 99 erra o alvo.
    when(randomGenerator.nextInt(100)).thenReturn(70);

    DamageResult resultado = damageCalculatorService.calculateDamage(
        bulbaSal, charSal, golpeImpreciso, terreno);
    acao(golpeImpreciso).execute(bulbaSal, charSal, terreno);

    assertFalse(resultado.hit());
    assertEquals(0.0, resultado.damage());
    assertEquals(39, charSal.getHp());
    verify(battleLogger).logMiss(bulbaSal, golpeImpreciso);
    verify(battleLogger, never()).logDamage(any(), anyDouble(), anyBoolean());
    verify(battlegroundService, never()).isActive(any());
    verify(randomGenerator, never()).nextDouble();
  }
}
