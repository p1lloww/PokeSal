package br.edu.ucsal.tqs.pokesal.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.fire.HotAsphalt;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnActionFactory;
import br.edu.ucsal.tqs.pokesal.services.DamageCalculatorService;
import br.edu.ucsal.tqs.pokesal.services.DamageResult;
import br.edu.ucsal.tqs.pokesal.services.log.BattleLogger;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TurnActionFactoryTest {
  @Mock
  private DamageCalculatorService damageCalculatorService;

  @Mock
  private RandomGenerator randomGenerator;

  @Mock
  private BattleLogger battleLogger;

  @InjectMocks
  private TurnActionFactory factory;

  @Test
  @DisplayName("Construtor deve rejeitar colaboradores nulos")
  void construtorRejeitaNulos() {
    assertThrows(NullPointerException.class,
        () -> new TurnActionFactory(null, randomGenerator, battleLogger));
    assertThrows(NullPointerException.class,
        () -> new TurnActionFactory(damageCalculatorService, null, battleLogger));
    assertThrows(NullPointerException.class,
        () -> new TurnActionFactory(damageCalculatorService, randomGenerator, null));
  }

  @Test
  @DisplayName("Acao de ataque criada pela fabrica deve usar os colaboradores injetados")
  void acaoDeAtaqueUsaOsColaboradores() {
    PokeSal charSal = PokeSalCatalog.createCharSal();
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    Move scratch = charSal.getMoves().get(0);
    HotAsphalt asfalto = new HotAsphalt();
    when(damageCalculatorService.calculateDamage(charSal, squirtSal, scratch, asfalto))
        .thenReturn(new DamageResult(5.0, true, false, false));

    TurnAction acao = factory.createMoveAction(scratch);
    acao.execute(charSal, squirtSal, asfalto);

    assertEquals(39, squirtSal.getHp());
    verify(battleLogger).logAttack(charSal, squirtSal, scratch);
    verify(battleLogger).logDamage(squirtSal, 5.0, false);
  }

  @Test
  @DisplayName("Acao de item criada pela fabrica deve registrar o uso do item")
  void acaoDeItemRegistraOUso() {
    PokeSal squirtSal = PokeSalCatalog.createSquirtSal();
    Trainer trainer = new Trainer("Ash", squirtSal);
    Item potion = ItemCatalog.createPotion();
    trainer.addItem(potion);

    TurnAction acao = factory.createItemAction(potion, trainer, 1);
    acao.execute(squirtSal, PokeSalCatalog.createCharSal(), new HotAsphalt());

    assertEquals(0, trainer.getBackpack().size());
    verify(battleLogger).logItemUse(squirtSal, potion);
  }
}
