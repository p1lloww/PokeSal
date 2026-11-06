package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BattleTest {
  private final Battleground terreno = new HotAsphalt();
  private Trainer trainerA;
  private Trainer trainerB;
  private Battle battle;

  @BeforeEach
  void setUp() {
    trainerA = new Trainer("A", PokeSalCatalog.createCharSal());
    trainerB = new Trainer("B", PokeSalCatalog.createSquirtSal());
    battle = new Battle(trainerA, trainerB, terreno);
  }

  @Test
  @DisplayName("Construtor deve rejeitar treinadores e terreno nulos")
  void construtorRejeitaNulos() {
    assertThrows(NullPointerException.class, () -> new Battle(null, trainerB, terreno));
    assertThrows(NullPointerException.class, () -> new Battle(trainerA, null, terreno));
    assertThrows(NullPointerException.class, () -> new Battle(trainerA, trainerB, null));
  }

  @Test
  @DisplayName("Batalha nao deve ter vencedor enquanto os dois PokeSal estao de pe")
  void semVencedorComOsDoisDePe() {
    battle.checkForWinner();

    assertFalse(battle.isOver());
    assertNull(battle.getWinner());
  }

  @Test
  @DisplayName("Treinador B deve vencer quando o PokeSal de A chega a zero")
  void treinadorBVenceQuandoAEhNocauteado() {
    trainerA.getPokeSal().takeDamage(1000.0);

    battle.checkForWinner();

    assertTrue(battle.isOver());
    assertSame(trainerB, battle.getWinner());
  }

  @Test
  @DisplayName("Treinador A deve vencer quando o PokeSal de B chega a zero")
  void treinadorAVenceQuandoBEhNocauteado() {
    trainerB.getPokeSal().takeDamage(1000.0);

    battle.checkForWinner();

    assertSame(trainerA, battle.getWinner());
  }

  @Test
  @DisplayName("Vencedor definido nao deve mudar em verificacoes seguintes")
  void vencedorNaoMudaDepoisDeDefinido() {
    trainerB.getPokeSal().takeDamage(1000.0);
    battle.checkForWinner();
    trainerA.getPokeSal().takeDamage(1000.0);

    battle.checkForWinner();

    assertSame(trainerA, battle.getWinner());
  }

  @Test
  @DisplayName("Lista de turnos devolvida deve ser uma copia")
  void listaDeTurnosEhUmaCopia() {
    battle.addTurn(new Turn(1));

    battle.getTurns().clear();

    assertEquals(1, battle.getTurns().size());
    assertThrows(NullPointerException.class, () -> battle.addTurn(null));
  }
}
