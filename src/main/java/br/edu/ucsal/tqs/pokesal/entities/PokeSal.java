package br.edu.ucsal.tqs.pokesal.entities;

import br.edu.ucsal.tqs.pokesal.entities.effects.Effect;
import br.edu.ucsal.tqs.pokesal.entities.passives.Passive;
import br.edu.ucsal.tqs.pokesal.entities.statusconditions.StatusCondition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa um PokeSal em batalha, com HP, ATK, DEF e SPD atuais, tipo elemental, lista de
 * movimentos, uma passiva fixa, condição de status e efeitos temporários ativos.
 */
public class PokeSal {

  private static final int MAX_MOVES = 4;

  private final String name;
  private final String description;
  private int hp;
  private final int maxHp;
  private int atk;
  private int def;
  private int spd;
  private final ElementType elementType;
  private final List<Move> moves;
  private final Passive passive;
  private StatusCondition statusCondition;
  private final Map<Integer, List<Effect>> activeEffects = new HashMap<>();

  /**
   * Cria um novo PokeSal com os atributos, tipo, movimentos e passiva especificados.
   *
   * @param name        o nome do PokeSal
   * @param description a descrição do PokeSal
   * @param hp          os pontos de vida máximos
   * @param atk         o atributo de ataque base
   * @param def         o atributo de defesa base
   * @param spd         o atributo de velocidade base
   * @param elementType o tipo elemental
   * @param moves       a lista de movimentos, entre 1 e 4
   * @param passive     a habilidade passiva fixa deste PokeSal
   * @throws IllegalArgumentException se algum atributo numérico não for positivo, se a lista de
   *                                  movimentos for nula, vazia ou tiver mais de 4 elementos
   * @throws NullPointerException     se name, description, elementType ou passive forem nulos
   */
  public PokeSal(String name, String description, int hp, int atk, int def, int spd,
      ElementType elementType, List<Move> moves, Passive passive) {

    if (name == null) {
      throw new NullPointerException("O nome não pode ser nulo");
    }
    if (description == null) {
      throw new NullPointerException("A descrição não pode ser nula");
    }
    if (hp <= 0) {
      throw new IllegalArgumentException("HP precisa ser maior que 0");
    }
    if (atk <= 0) {
      throw new IllegalArgumentException("ATK precisa ser maior que 0");
    }
    if (def <= 0) {
      throw new IllegalArgumentException("DEF precisa ser maior que 0");
    }
    if (spd <= 0) {
      throw new IllegalArgumentException("SPD precisa ser maior que 0");
    }
    if (elementType == null) {
      throw new NullPointerException("O tipo elemental não pode ser nulo");
    }
    if (moves == null || moves.isEmpty()) {
      throw new IllegalArgumentException("O PokeSal precisa ter ao menos um movimento");
    }
    if (moves.size() > MAX_MOVES) {
      throw new IllegalArgumentException("O PokeSal pode ter no máximo 4 movimentos");
    }
    if (passive == null) {
      throw new NullPointerException("A passiva não pode ser nula");
    }

    this.name = name;
    this.description = description;
    this.hp = hp;
    this.maxHp = hp;
    this.atk = atk;
    this.def = def;
    this.spd = spd;
    this.elementType = elementType;
    this.moves = new ArrayList<>(moves);
    this.passive = passive;
  }

  /**
   * Aplica dano a este PokeSal, reduzindo o HP atual. O HP nunca fica abaixo de zero.
   *
   * @param amount quantidade de dano a ser aplicada
   * @throws IllegalArgumentException se amount for negativo
   */
  public void takeDamage(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException("A quantidade de dano nao pode ser negativa");
    }

    hp = (int) Math.max(0, hp - amount);
  }

  /**
   * Restaura HP deste PokeSal. O HP restaurado nunca ultrapassa o HP máximo.
   *
   * @param amount quantidade de HP a ser restaurada
   * @throws IllegalArgumentException se amount for negativo
   */
  public void heal(double amount) {
    if (amount < 0) {
      throw new IllegalArgumentException(
          "A quantidade de vida a ser curada nao pode ser negativa");
    }

    hp = (int) Math.min(maxHp, hp + amount);
  }

  /**
   * Aplica um multiplicador ao ATK atual deste PokeSal. Usado tanto para conceder quanto para
   * reverter buffs e debuffs de ataque, aplicando o multiplicador inverso na reversão.
   *
   * @param multiplier fator multiplicativo aplicado ao ATK atual
   * @throws IllegalArgumentException se multiplier for negativo
   */
  public void applyAttackBuff(double multiplier) {
    if (multiplier < 0) {
      throw new IllegalArgumentException("o multiplicador nao pode ser negativo");
    }

    atk = (int) (atk * multiplier);
  }

  /**
   * Aplica um multiplicador ao DEF atual deste PokeSal. Usado tanto para conceder quanto para
   * reverter buffs e debuffs de defesa, aplicando o multiplicador inverso na reversão.
   *
   * @param multiplier fator multiplicativo aplicado ao DEF atual
   * @throws IllegalArgumentException se multiplier for negativo
   */
  public void applyDefenseBuff(double multiplier) {
    if (multiplier < 0) {
      throw new IllegalArgumentException("o multiplicador nao pode ser negativa");
    }

    def = (int) (def * multiplier);
  }

  /**
   * Aplica um multiplicador ao SPD atual deste PokeSal. Usado tanto para conceder quanto para
   * reverter buffs e debuffs de velocidade, aplicando o multiplicador inverso na reversão.
   *
   * @param multiplier fator multiplicativo aplicado ao SPD atual
   * @throws IllegalArgumentException se multiplier for negativo
   */
  public void applySpeedBuff(double multiplier) {
    if (multiplier < 0) {
      throw new IllegalArgumentException("o multiplicador nao pode ser negativa");
    }

    spd = (int) (spd * multiplier);
  }

  /**
   * Aplica uma condição de status a este PokeSal, caso ele não possua nenhuma condição ativa e sua
   * passiva não bloqueie a aplicação. Dispara o efeito imediato da condição, se houver.
   *
   * @param status a condição de status a ser aplicada
   * @throws NullPointerException se status for nulo
   */
  public void applyStatus(StatusCondition status) {
    if (status == null) {
      throw new NullPointerException("A condição de status não pode ser nula");
    }
    if (this.statusCondition != null) {
      return;
    }
    if (this.passive.preventsStatusApplication(this)) {
      return;
    }

    this.statusCondition = status;
    status.onApply(this);
  }

  /**
   * Remove a condição de status atual deste PokeSal, caso exista uma. Não tem efeito caso o PokeSal
   * não possua condição de status ativa.
   */
  public void clearStatus() {
    if (statusCondition != null) {
      statusCondition.onRemove(this);
    }
    this.statusCondition = null;
  }

  /**
   * Aplica o efeito de um item a este PokeSal, agendando sua reversão ao final do turno
   * correspondente caso o efeito seja temporário.
   *
   * @param effect      o efeito a ser aplicado
   * @param currentTurn o número do turno atual, usado para calcular a expiração de efeitos
   *                    temporários
   * @throws NullPointerException se effect for nulo
   */
  public void receiveEffect(Effect effect, int currentTurn) {
    if (effect == null) {
      throw new NullPointerException("O efeito não pode ser nulo");
    }

    effect.applyEffect(this);

    if (effect.isTemporary()) {
      int expiresAt = currentTurn + 1;
      this.activeEffects.computeIfAbsent(expiresAt, key -> new ArrayList<>()).add(effect);
    }
  }

  /**
   * Reverte todos os efeitos temporários cuja expiração está agendada para o turno informado,
   * removendo-os da lista de efeitos ativos.
   *
   * @param currentTurn o número do turno a ser verificado
   */
  public void expireEffects(int currentTurn) {
    List<Effect> expiring = this.activeEffects.remove(currentTurn);

    if (expiring != null) {
      expiring.forEach(effect -> effect.removeEffect(this));
    }
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getHp() {
    return hp;
  }

  public int getMaxHp() {
    return maxHp;
  }

  public int getAtk() {
    return atk;
  }

  public int getDef() {
    return def;
  }

  public int getSpd() {
    return spd;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public Passive getPassive() {
    return passive;
  }

  public StatusCondition getStatusCondition() {
    return statusCondition;
  }

  public List<Move> getMoves() {
    return new ArrayList<>(moves);
  }

  public Map<Integer, List<Effect>> getActiveEffects() {
    return new HashMap<>(activeEffects);
  }
}
