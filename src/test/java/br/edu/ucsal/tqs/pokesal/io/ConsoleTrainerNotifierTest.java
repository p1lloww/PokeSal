package br.edu.ucsal.tqs.pokesal.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ConsoleTrainerNotifierTest {

  @Test
  @DisplayName("Construtor deve rejeitar PrintStream nulo")
  void construtorRejeitaPrintStreamNulo() {
    assertThrows(NullPointerException.class, () -> new ConsoleTrainerNotifier(null));
  }

  @Test
  @DisplayName("Alerta deve informar o treinador, o PokeSal e o HP atual em relacao ao maximo")
  void alertaInformaTreinadorPokeSalEHp() {
    ByteArrayOutputStream saida = new ByteArrayOutputStream();
    ConsoleTrainerNotifier notifier =
        new ConsoleTrainerNotifier(new PrintStream(saida, true, StandardCharsets.UTF_8));
    PokeSal charSal = PokeSalCatalog.createCharSal();
    charSal.takeDamage(31.0);

    notifier.notifyCriticalHp(new Trainer("Alan", charSal), charSal);

    assertEquals("Atenção, Alan: CharSal está com HP crítico (8/39)!",
        saida.toString(StandardCharsets.UTF_8).strip());
  }
}
