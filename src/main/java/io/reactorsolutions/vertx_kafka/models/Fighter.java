package io.reactorsolutions.vertx_kafka.models;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@NoArgsConstructor
public class Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(Fighter.class);
    private String name;
    private int maxHp;
    private int currentHp;
    private int hit;
    private int def;
    private int spd;
    private int maxStam;
    private int currentStam;
    private int regen;
    private String message;

    public Fighter(String name, int maxHp, int currentHp, int hit, int def, int spd, int maxStam, int currentStam, String message) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = currentHp;
        this.hit = hit;
        this.def = def;
        this.spd = spd;
        this.maxStam = maxStam;
        this.currentStam = currentStam;
        this.regen = (int) (maxHp * 0.1);
        this.message = message;
    }

    public JsonObject toJsonObject(){
        return JsonObject.mapFrom(this);
    }

  public int HitDmg() {
    return (int)(Math.random()*50)+1;
  }

  public int calculateDMG(int hit, int def){
    if (hit <= def)
      return 0;
    else
      return hit - def;
  }

  public void takeDmg(int dmg){
      if(currentHp > 0 && currentHp >= dmg){
        currentHp -= dmg;
      } else {
        currentHp = 0;
      }
  }

  public int aSpd(){
    return spd > 5000 ? 300 : 5000 - spd;
  }

  public void attack(){
    setHit(HitDmg());
    if (hit <= currentStam) {
      setCurrentStam(getCurrentStam() - hit);
      LOG.debug(getName().toUpperCase()+ " attacks! HIT: {}", hit);
    } else
      LOG.info(getName().toUpperCase()+ " is tired...");
  }

  public void defeatedFighter(Vertx vertx, String deploymentID) {
    if (currentHp <= 0){
      System.out.println(name.toUpperCase()+": WAS DEFEATED!");
      vertx.undeploy(deploymentID);
    }
  }

  public void checkStats(){
      recoverHealth();
      recoverStam();
  }

  public void showStatus() {
    System.out.println(getName().toUpperCase() + " [HP : " + currentHp + "/" + maxHp + "]");
    System.out.println("[STAM : " + currentStam + "/" + maxStam + "]");
    System.out.println("******************");
  }

  public String currentStatus() {
    String hp = getName().toUpperCase() + " [HP : " + getCurrentHp() + "/" + maxHp + "] [" + hpBar(getCurrentHp(), maxHp) + "]";
    String stam = "[STAM : " + getCurrentStam() + "/" + getMaxStam() + "] [" + hpBar(getCurrentStam(), getMaxStam()) + "]";
    return hp+"\n"+stam+"\n*****************";
  }

  public String hpBar(int hp, int hpMax){
    String hpBar = "";
    for (int i = 0;i < 100;i++){
      if (i%10 == 0 && i != 0){
        hpBar+="|";
      }
      if(i < (100*hp/hpMax)) {
        hpBar += "=";
      }else{
        hpBar += " ";
      }
    }
    return hpBar;
  }

  private void recoverHealth() {
    if(currentHp <= maxHp*0.35) {
      val hpRegen = regenerate(maxHp, currentHp, regen);
      LOG.debug("Broly Healed: {} +{}", currentHp, hpRegen);
      setCurrentHp(hpRegen);
    }
  }

  private void recoverStam() {
    if(currentStam <= 50) {
      val stamRegen = regenerate(maxStam, currentStam, regen);
      LOG.debug("Broly recovered stamina: {} +{}", currentStam, stamRegen);
      setCurrentStam(stamRegen);
    }
  }

  private int regenerate(int maxStat, int stat, int regen){
    return maxStat < stat + regen + (int)(Math.random()*20)+1 ? maxStat : regen + stat - (int)(Math.random()*10)+1;
  }
}
