package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.MoveAction;
import org.junit.jupiter.api.Test;

public class MoveActionTest {
  private final Battleground terreno = new HotAsphalt();

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

    new MoveAction(golpeVenenoso(1.0)).execute(bulbaSal, charSal, terreno);

    assertInstanceOf(Poison.class, charSal.getStatusCondition());
  }

  @Test
  void golpeComChanceZeroNuncaAplicaOStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();

    new MoveAction(golpeVenenoso(0.0)).execute(bulbaSal, charSal, terreno);

    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeSemStatusNaoAplicaNenhumStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();

    new MoveAction(tackle(bulbaSal)).execute(bulbaSal, charSal, terreno);

    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeQueDerrubaNaoAplicaStatus() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    charSal.takeDamage(38.0);

    new MoveAction(golpeVenenoso(1.0)).execute(bulbaSal, charSal, terreno);

    assertEquals(0, charSal.getHp());
    assertNull(charSal.getStatusCondition());
  }

  @Test
  void golpeAvisaAPassivaDoDefensorSobreODano() {
    PokeSal bulbaSal = PokeSalCatalog.createBulbaSal();
    PokeSal charSal = PokeSalCatalog.createCharSal();
    charSal.takeDamage(23.0);

    new MoveAction(tackle(bulbaSal)).execute(bulbaSal, charSal, terreno);

    assertEquals(11, charSal.getHp());
    assertEquals(78, charSal.getAtk());
  }

  @Test
  void construtorDeveLancarExcecaoComGolpeNulo() {
    assertThrows(NullPointerException.class, () -> new MoveAction(null));
  }
}
