package br.edu.ucsal.tqs.pokesal.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.Poison;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConsoleBattleLoggerTest {
  private ByteArrayOutputStream saida;
  private ConsoleBattleLogger logger;
  private PokeSal bulbaSalA;
  private PokeSal bulbaSalB;
  private Trainer trainerA;
  private Trainer trainerB;

  @BeforeEach
  void setUp() {
    saida = new ByteArrayOutputStream();
    logger = new ConsoleBattleLogger(new PrintStream(saida, true, StandardCharsets.UTF_8));
    bulbaSalA = PokeSalCatalog.createBulbaSal();
    bulbaSalB = PokeSalCatalog.createBulbaSal();
    trainerA = new Trainer("Alan", bulbaSalA);
    trainerB = new Trainer("Balt", bulbaSalB);
  }

  private String linhas() {
    return saida.toString(StandardCharsets.UTF_8).replace(System.lineSeparator(), "\n");
  }

  @Test
  @DisplayName("Construtor deve rejeitar PrintStream nulo")
  void construtorRejeitaPrintStreamNulo() {
    assertThrows(NullPointerException.class, () -> new ConsoleBattleLogger(null));
  }

  @Test
  @DisplayName("Inicio da batalha deve anunciar os dois treinadores")
  void inicioDaBatalhaAnunciaTreinadores() {
    logger.logBattleStart(trainerA, trainerB);

    assertEquals("A batalha entre Alan e Balt começou!\n", linhas());
  }

  @Test
  @DisplayName("Apos o inicio da batalha, cada PokeSal deve aparecer com o nome do treinador")
  void pokeSalApareceComNomeDoTreinador() {
    Move tackle = bulbaSalA.getMoves().get(0);
    logger.logBattleStart(trainerA, trainerB);
    saida.reset();

    logger.logAttack(bulbaSalA, bulbaSalB, tackle);

    assertEquals("BulbaSal (Alan) usou Tackle em BulbaSal (Balt)!\n", linhas());
  }

  @Test
  @DisplayName("Antes do inicio da batalha, o PokeSal deve aparecer apenas pelo nome")
  void pokeSalSemTreinadorApareceSoPeloNome() {
    logger.logDamage(bulbaSalA, 9.0, false);

    assertEquals(String.format("BulbaSal recebeu %.1f de dano.%n", 9.0)
        .replace(System.lineSeparator(), "\n"), linhas());
  }

  @Test
  @DisplayName("Aplicacao de status e dano de status devem ter mensagens diferentes")
  void aplicacaoEDanoDeStatusSaoDiferentes() {
    logger.logBattleStart(trainerA, trainerB);
    saida.reset();

    logger.logStatusEffect(bulbaSalB, new Poison());
    logger.logStatusDamage(bulbaSalB, new Poison(), 5);

    assertEquals("BulbaSal (Balt) foi afetado por Poison!\n"
        + "BulbaSal (Balt) perdeu 5 de HP por causa de Poison.\n", linhas());
  }

  @Test
  @DisplayName("Tempo esgotado deve informar que o treinador perdeu a vez")
  void tempoEsgotadoInformaQueTreinadorPerdeuAVez() {
    logger.logActionTimeout(trainerA);

    assertEquals("Alan excedeu o tempo para escolher a ação e perdeu a vez!\n", linhas());
  }
}
