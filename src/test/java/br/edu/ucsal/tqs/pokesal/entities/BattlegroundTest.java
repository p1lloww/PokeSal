package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.grass.CentralFlowerbed;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.water.WaterPuddle;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import org.junit.jupiter.api.Test;

public class BattlegroundTest {
  private static final double DELTA = 0.0001;

  @Test
  void hotAsphaltAumentaSomenteDanosDeGolpesDeFogoEmQuinzePorCento() {
    double baseDamage = 100.0;

    double result = new HotAsphalt().modifyDamage(baseDamage, ElementType.FIRE);

    assertEquals(115.0, result, DELTA);
  }

  @Test
  void hotAsphaltNaoAlteraDanoDeOutrosTipos() {
    double baseDamage = 100.0;
    double resultWater = new HotAsphalt().modifyDamage(baseDamage, ElementType.WATER);
    double resultPlant = new HotAsphalt().modifyDamage(baseDamage, ElementType.PLANT);

    assertEquals(100.0, resultWater, DELTA);
    assertEquals(100.0, resultPlant, DELTA);
  }

  @Test
  void waterPuddleAumentaDanoDeAguaEmDezPorCento() {
    double baseDamage = 100.0;

    double result = new WaterPuddle().modifyDamage(baseDamage, ElementType.WATER);

    assertEquals(110.0, result, DELTA);
  }

  @Test
  void waterPuddleNaoAlteraDanoDeOutrosTipos() {
    double baseDamage = 100.0;
    double resultFire = new WaterPuddle().modifyDamage(baseDamage, ElementType.FIRE);
    double resultPlant = new WaterPuddle().modifyDamage(baseDamage, ElementType.PLANT);

    assertEquals(100.0, resultFire, DELTA);
    assertEquals(100.0, resultPlant, DELTA);
  }

  @Test
  void centralFlowerbedCuraCincoPorCentoDoHpMaximoDePokeSalDeTipoPlanta() {
    PokeSal pokeSal = PokeSalCatalog.createBulbaSal();
    pokeSal.takeDamage(20.0);

    new CentralFlowerbed().onTurnEnd(pokeSal);

    assertEquals(27, pokeSal.getHp());
  }

  @Test
  void centralFlowerbedNaoCuraPokeSalDeTipoDiferenteDePlanta() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    charSal.takeDamage(20.0);
    squirtSal.takeDamage(20.0);
    Battleground centralFlowerBed = new CentralFlowerbed();

    centralFlowerBed.onTurnEnd(charSal);
    centralFlowerBed.onTurnEnd(squirtSal);

    assertEquals(19, charSal.getHp());
    assertEquals(24, squirtSal.getHp());
  }

  @Test
  void centralFlowerbedNaoCuraAlemDoHpMaximo() {
    PokeSal pokeSal = PokeSalCatalog.createBulbaSal();
    pokeSal.takeDamage(1.0);

    new CentralFlowerbed().onTurnEnd(pokeSal);

    assertEquals(pokeSal.getMaxHp(), pokeSal.getHp());
  }

  @Test
  void hotAsphaltEWaterPuddleNaoAlteramHpNoFimDoTurno() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    charSal.takeDamage(20.0);
    squirtSal.takeDamage(20.0);

    new HotAsphalt().onTurnEnd(charSal);
    new WaterPuddle().onTurnEnd(squirtSal);

    assertEquals(19, charSal.getHp());
    assertEquals(24, squirtSal.getHp());
  }
}
