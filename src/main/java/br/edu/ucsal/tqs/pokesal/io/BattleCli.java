package br.edu.ucsal.tqs.pokesal.io;

import br.edu.ucsal.tqs.pokesal.entities.Battle;
import br.edu.ucsal.tqs.pokesal.entities.Item;
import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.Trainer;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.ItemCatalog;
import br.edu.ucsal.tqs.pokesal.entities.catalogs.PokeSalCatalog;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.ItemAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.MoveAction;
import br.edu.ucsal.tqs.pokesal.entities.turnactions.TurnAction;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Camada de entrada e saída da batalha via linha de comando: captura as escolhas do jogador através
 * de Scanner e renderiza o estado da batalha no console. Não contém regra de negócio — apenas
 * coordena a interação com o usuário.
 */
public class BattleCli {

  private static final int SEPARATOR_WIDTH = 50;

  private final Scanner scanner;

  /**
   * Cria uma nova CLI de batalha usando o Scanner informado para captura de input.
   *
   * @param scanner o Scanner usado para ler a entrada do usuário
   */
  public BattleCli(Scanner scanner) {
    this.scanner = scanner;
  }

  /**
   * Pergunta ao jogador o nome do treinador.
   *
   * @return o nome informado
   */
  public String promptTrainerName() {
    System.out.println("Digite o nome do treinador:");
    return scanner.nextLine();
  }

  /**
   * Apresenta os seis PokeSal iniciais disponíveis e retorna o escolhido pelo jogador.
   *
   * @return o PokeSal escolhido
   */
  public PokeSal promptInitialPokeSal() {
    List<Supplier<PokeSal>> options = List.of(
        PokeSalCatalog::createBulbaSal,
        PokeSalCatalog::createCharSal,
        PokeSalCatalog::createSquirtSal,
        PokeSalCatalog::createChikoSal,
        PokeSalCatalog::createCyndaSal,
        PokeSalCatalog::createTotoSal);
    List<String> names = List.of(
        "BulbaSal", "CharSal", "SquirtSal", "ChikoSal", "CyndaSal", "TotoSal");

    System.out.println("Escolha seu PokeSal inicial:");
    for (int i = 0; i < names.size(); i++) {
      System.out.println((i + 1) + " - " + names.get(i));
    }

    int choice = readValidChoice(options.size());
    return options.get(choice - 1).get();
  }

  /**
   * Apresenta os itens disponíveis no catálogo e pergunta ao jogador se deseja
   * adicionar um à mochila, ou parar de escolher.
   *
   * @param itemsAlreadyChosen a quantidade de itens já escolhidos pelo treinador
   * @return o item escolhido, ou null se o jogador optou por não escolher mais itens
   */
  public Item promptItemSelection(int itemsAlreadyChosen) {
    List<Item> options = ItemCatalog.allAvailableItems();

    System.out.println("Escolha um item para sua mochila (" + itemsAlreadyChosen
        + "/" + Trainer.MAX_ITEMS + " escolhidos):");
    for (int i = 0; i < options.size(); i++) {
      System.out.println((i + 1) + " - " + options.get(i).getName());
    }
    System.out.println("0 - Não escolher mais itens");

    int choice = readValidChoice(0, options.size());
    if (choice == 0) {
      return null;
    }

    return options.get(choice - 1);
  }

  /**
   * Pergunta ao jogador se deseja atacar ou usar um item, e retorna a ação escolhida.
   *
   * @param pokeSal           o PokeSal ativo do treinador que está escolhendo
   * @param backpack          a mochila do treinador que está escolhendo
   * @param currentTurnNumber o número do turno atual
   * @return a TurnAction correspondente à escolha do jogador
   */
  public TurnAction promptAction(PokeSal pokeSal, List<Item> backpack, int currentTurnNumber) {
    System.out.println(pokeSal.getName() + ", escolha uma ação:");
    System.out.println("1 - Atacar");

    boolean canUseItem = !backpack.isEmpty();
    if (canUseItem) {
      System.out.println("2 - Usar item");
    }

    int choice = readValidChoice(canUseItem ? 2 : 1);

    if (choice == 1) {
      Move move = promptMoveChoice(pokeSal);
      return new MoveAction(move);
    }

    Item item = promptItemChoice(backpack);
    return new ItemAction(item, currentTurnNumber);
  }

  private Move promptMoveChoice(PokeSal pokeSal) {
    List<Move> moves = pokeSal.getMoves();

    System.out.println("Escolha um movimento:");
    for (int i = 0; i < moves.size(); i++) {
      System.out.println((i + 1) + " - " + moves.get(i).name());
    }

    int choice = readValidChoice(moves.size());
    return moves.get(choice - 1);
  }

  private Item promptItemChoice(List<Item> backpack) {
    System.out.println("Escolha um item:");
    for (int i = 0; i < backpack.size(); i++) {
      System.out.println((i + 1) + " - " + backpack.get(i).getName());
    }

    int choice = readValidChoice(backpack.size());
    return backpack.get(choice - 1);
  }

  private int readValidChoice(int maxOptions) {
    return readValidChoice(1, maxOptions);
  }

  private int readValidChoice(int minOption, int maxOption) {
    int choice;
    while (true) {
      String input = scanner.nextLine();
      try {
        choice = Integer.parseInt(input);
        if (choice >= minOption && choice <= maxOption) {
          break;
        }
        System.out.println("Opção inválida, tente novamente.");
      } catch (NumberFormatException e) {
        System.out.println("Digite um número.");
      }
    }
    return choice;
  }

  /**
   * Renderiza o estado atual da batalha no console, mostrando o terreno ativo e o status de HP de
   * cada PokeSal.
   *
   * @param battle a batalha cujo estado será exibido
   */
  public void renderBattleState(Battle battle) {
    System.out.println("=".repeat(SEPARATOR_WIDTH));
    System.out.println("Terreno: " + battle.getBattleground().getName());
    System.out.println("-".repeat(SEPARATOR_WIDTH));
    renderPokeSalStatus(battle.getTrainerA().getPokeSal());
    System.out.println();
    renderPokeSalStatus(battle.getTrainerB().getPokeSal());
    System.out.println("=".repeat(SEPARATOR_WIDTH));
  }

  private void renderPokeSalStatus(PokeSal pokeSal) {
    System.out.println(pokeSal.getName() + " (" + pokeSal.getElementType() + ")");
    System.out.println("HP: " + pokeSal.getHp() + "/" + pokeSal.getMaxHp());
    if (pokeSal.getStatusCondition() != null) {
      System.out.println("Status: " + pokeSal.getStatusCondition().getName());
    }
  }

  /**
   * Anuncia o treinador vencedor da batalha.
   *
   * @param winner o treinador vencedor
   */
  public void announceWinner(Trainer winner) {
    System.out.println("Vencedor: " + winner.getName());
  }
}