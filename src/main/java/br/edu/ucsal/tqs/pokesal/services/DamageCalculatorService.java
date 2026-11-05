package br.edu.ucsal.tqs.pokesal.services;

import br.edu.ucsal.tqs.pokesal.entities.Move;
import br.edu.ucsal.tqs.pokesal.entities.PokeSal;
import br.edu.ucsal.tqs.pokesal.entities.TypeChart;
import br.edu.ucsal.tqs.pokesal.entities.battlegrounds.Battleground;
import br.edu.ucsal.tqs.pokesal.services.battleground.BattlegroundService;
import br.edu.ucsal.tqs.pokesal.services.random.RandomGenerator;

/**
 * Serviço responsável por calcular o dano de um golpe. Primeiro decide, com base na precisão do
 * golpe, se ele acerta o alvo; depois combina o dano base (derivado de ATK, DEF e o poder do
 * golpe), o multiplicador de vantagem de tipo, o efeito do terreno (quando ativo), a sinergia da
 * passiva do atacante com esse terreno e, com uma chance de 6,25%, o multiplicador de acerto
 * crítico.
 */
public class DamageCalculatorService {

  private static final double DAMAGE_SCALE_FACTOR = 5.0;
  private static final double CRITICAL_CHANCE = 0.0625;
  private static final double CRITICAL_MULTIPLIER = 1.5;
  private static final int ACCURACY_ROLL_BOUND = 100;

  private final RandomGenerator randomGenerator;
  private final BattlegroundService battlegroundService;

  /**
   * Cria um novo serviço de cálculo de dano com os colaboradores informados.
   *
   * @param randomGenerator     o gerador usado nos sorteios de precisão e acerto crítico
   * @param battlegroundService o serviço que informa se o efeito do terreno está ativo
   * @throws NullPointerException se randomGenerator ou battlegroundService forem nulos
   */
  public DamageCalculatorService(RandomGenerator randomGenerator,
      BattlegroundService battlegroundService) {
    if (randomGenerator == null) {
      throw new NullPointerException("O gerador aleatório não pode ser nulo");
    }
    if (battlegroundService == null) {
      throw new NullPointerException("O serviço de terreno não pode ser nulo");
    }

    this.randomGenerator = randomGenerator;
    this.battlegroundService = battlegroundService;
  }

  /**
   * Calcula o dano de um golpe de attacker contra defender. O golpe acerta quando o valor sorteado
   * entre 0 e 99 é menor que sua precisão; caso erre, o dano é zero e nenhum outro sorteio é feito.
   * Caso acerte, o golpe é crítico quando o valor sorteado entre 0.0 e 1.0 é menor que 0.0625.
   *
   * @param attacker     o PokeSal que executa o golpe
   * @param defender     o PokeSal que recebe o golpe
   * @param move         o golpe utilizado
   * @param battleground o terreno da batalha
   * @return o resultado do cálculo, com o dano final e se houve acerto, crítico e efeito de terreno
   * @throws NullPointerException se attacker, defender, move ou battleground forem nulos
   */
  public DamageResult calculateDamage(PokeSal attacker, PokeSal defender, Move move,
      Battleground battleground) {
    if (attacker == null) {
      throw new NullPointerException("O atacante não pode ser nulo");
    }
    if (defender == null) {
      throw new NullPointerException("O defensor não pode ser nulo");
    }
    if (move == null) {
      throw new NullPointerException("O golpe não pode ser nulo");
    }
    if (battleground == null) {
      throw new NullPointerException("O terreno não pode ser nulo");
    }

    if (randomGenerator.nextInt(ACCURACY_ROLL_BOUND) >= move.accuracy()) {
      return DamageResult.miss();
    }

    double baseDamage = calculateBaseDamage(attacker, defender, move);
    double typeMultiplier = TypeChart.getMultiplier(move.type(), defender.getElementType());
    double damage = baseDamage * typeMultiplier;

    boolean battlegroundApplied = false;
    if (battlegroundService.isActive(battleground)) {
      double modifiedDamage = battleground.modifyDamage(damage, move.type());
      battlegroundApplied = modifiedDamage != damage;
      damage = modifiedDamage;
    }
    damage = damage * attacker.getPassive().onAttack(attacker, battleground);

    boolean critical = randomGenerator.nextDouble() < CRITICAL_CHANCE;
    if (critical) {
      damage = damage * CRITICAL_MULTIPLIER;
    }

    return new DamageResult(damage, true, critical, battlegroundApplied);
  }

  /**
   * Calcula o dano base de um golpe, a partir do ATK do atacante, do DEF do defensor e do poder do
   * golpe.
   *
   * @param attacker o PokeSal que executa o golpe
   * @param defender o PokeSal que recebe o golpe
   * @param move     o golpe utilizado
   * @return o dano base, antes da aplicação de multiplicadores de tipo, terreno e passiva
   */
  private double calculateBaseDamage(PokeSal attacker, PokeSal defender, Move move) {
    return ((double) attacker.getAtk() / defender.getDef()) * move.power() / DAMAGE_SCALE_FACTOR;
  }
}
