package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.ItemAction;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TrainerTest {
  private PokeSal squirtSal;
  private Trainer trainer;

  @BeforeEach
  void setUp() {
    squirtSal = PokeSalCatalog.createSquirtSal();
    trainer = new Trainer("Ash", squirtSal);
  }

  @Test
  @DisplayName("Deve lancar excecao ao tentar adicionar mais itens do que o limite")
  void testUsoLimiteDeItensExcedido() {
    trainer.addItem(ItemCatalog.createPotion());
    trainer.addItem(ItemCatalog.createSuperPotion());

    assertThrows(IllegalStateException.class,
        () -> trainer.addItem(ItemCatalog.createBurnHeal()));
    assertEquals(Trainer.MAX_ITEMS, trainer.getBackpack().size());
  }

  @Test
  void mochilaDeveAceitarItensAteOLimite() {
    Item potion = ItemCatalog.createPotion();
    Item superPotion = ItemCatalog.createSuperPotion();

    trainer.addItem(potion);
    trainer.addItem(superPotion);

    assertEquals(List.of(potion, superPotion), trainer.getBackpack());
  }

  @Test
  void addItemDeveLancarExcecaoComItemNulo() {
    assertThrows(NullPointerException.class, () -> trainer.addItem(null));
  }

  @Test
  void alterarListaRetornadaNaoAlteraAMochila() {
    trainer.addItem(ItemCatalog.createPotion());

    List<Item> backpack = trainer.getBackpack();
    backpack.clear();

    assertEquals(1, trainer.getBackpack().size());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", "   "})
  void construtorDeveLancarExcecaoComNomeVazio(String nome) {
    assertThrows(IllegalArgumentException.class, () -> new Trainer(nome, squirtSal));
  }

  @Test
  void construtorDeveLancarExcecaoComPokeSalNulo() {
    assertThrows(NullPointerException.class, () -> new Trainer("Ash", null));
  }

  @Test
  @DisplayName("Usar um item deve aplicar o efeito e remove-lo da mochila")
  void usarItemDeveAplicarEfeitoERemoverDaMochila() {
    Item potion = ItemCatalog.createPotion();
    trainer.addItem(potion);
    squirtSal.takeDamage(20.0);

    new ItemAction(potion, trainer, 1).execute(squirtSal, null, null);

    assertEquals(32, squirtSal.getHp());
    assertFalse(trainer.getBackpack().contains(potion));
  }

  @Test
  @DisplayName("Nao deve ser possivel usar o mesmo item duas vezes")
  void naoDeveUsarOMesmoItemDuasVezes() {
    Item potion = ItemCatalog.createPotion();
    trainer.addItem(potion);
    squirtSal.takeDamage(30.0);
    ItemAction action = new ItemAction(potion, trainer, 1);

    action.execute(squirtSal, null, null);
    int hpDepoisDoPrimeiroUso = squirtSal.getHp();

    assertThrows(IllegalStateException.class, () -> action.execute(squirtSal, null, null));
    assertEquals(hpDepoisDoPrimeiroUso, squirtSal.getHp());
  }

  @Test
  @DisplayName("Nao deve ser possivel usar um item que nao esta na mochila")
  void naoDeveUsarItemForaDaMochila() {
    trainer.addItem(ItemCatalog.createPotion());
    squirtSal.takeDamage(20.0);
    Item foraDaMochila = ItemCatalog.createSuperPotion();

    assertThrows(IllegalStateException.class,
        () -> new ItemAction(foraDaMochila, trainer, 1).execute(squirtSal, null, null));
    assertEquals(24, squirtSal.getHp());
    assertEquals(1, trainer.getBackpack().size());
  }

  @Test
  @DisplayName("Depois de usar todos os itens nenhum outro uso deve ser aceito")
  void naoDeveUsarItensDepoisDeEsvaziarAMochila() {
    Item potion = ItemCatalog.createPotion();
    Item superPotion = ItemCatalog.createSuperPotion();
    trainer.addItem(potion);
    trainer.addItem(superPotion);

    new ItemAction(potion, trainer, 1).execute(squirtSal, null, null);
    new ItemAction(superPotion, trainer, 2).execute(squirtSal, null, null);

    assertTrue(trainer.getBackpack().isEmpty());
    assertThrows(IllegalStateException.class,
        () -> new ItemAction(potion, trainer, 3).execute(squirtSal, null, null));
  }
}
