package br.edu.ucsal.tqs.pokesal.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.io.BattleCli;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ChooseItemsUseCaseTest {
  @Mock
  private BattleCli battleCli;

  @InjectMocks
  private ChooseItemsUseCase useCase;

  private Trainer trainer;

  @BeforeEach
  void setUp() {
    trainer = new Trainer("Ash", PokeSalCatalog.createCharSal());
  }

  @Test
  @DisplayName("Escolha deve parar sozinha quando a mochila chega ao limite de itens")
  void escolhaParaNoLimiteDaMochila() {
    Item potion = ItemCatalog.createPotion();
    Item superPotion = ItemCatalog.createSuperPotion();
    when(battleCli.promptItemSelection(anyInt())).thenReturn(potion, superPotion);

    useCase.execute(trainer);

    assertEquals(List.of(potion, superPotion), trainer.getBackpack());
    verify(battleCli).promptItemSelection(0);
    verify(battleCli).promptItemSelection(1);
    verify(battleCli, times(2)).promptItemSelection(anyInt());
  }

  @Test
  @DisplayName("Escolha deve parar quando o jogador decide nao levar mais itens")
  void escolhaParaQuandoJogadorDesiste() {
    Item burnHeal = ItemCatalog.createBurnHeal();
    when(battleCli.promptItemSelection(anyInt())).thenReturn(burnHeal, (Item) null);

    useCase.execute(trainer);

    assertEquals(List.of(burnHeal), trainer.getBackpack());
    verify(battleCli, times(2)).promptItemSelection(anyInt());
  }

  @Test
  @DisplayName("Mochila deve ficar vazia quando o jogador nao escolhe nenhum item")
  void mochilaVaziaSemEscolhas() {
    when(battleCli.promptItemSelection(0)).thenReturn(null);

    useCase.execute(trainer);

    assertEquals(0, trainer.getBackpack().size());
  }
}
