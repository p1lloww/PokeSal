package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChooseInitialPokeSalUseCaseTest {
  @Mock
  private BattleCli battleCli;

  @InjectMocks
  private ChooseInitialPokeSalUseCase useCase;

  @Test
  @DisplayName("Treinador criado deve ter o nome e o PokeSal escolhidos na CLI")
  void treinadorTemNomeEPokeSalEscolhidos() {
    PokeSal totoSal = PokeSalCatalog.createTotoSal();
    when(battleCli.promptTrainerName()).thenReturn("Misty");
    when(battleCli.promptInitialPokeSal()).thenReturn(totoSal);

    Trainer trainer = useCase.execute();

    assertEquals("Misty", trainer.getName());
    assertSame(totoSal, trainer.getPokeSal());
    InOrder ordem = inOrder(battleCli);
    ordem.verify(battleCli).promptTrainerName();
    ordem.verify(battleCli).promptInitialPokeSal();
  }
}
