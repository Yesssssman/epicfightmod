package yesman.epicfight.api.utils.game;

public class AttackResult {
	public final ResultType resultType;
	public final float damage;
	
	public AttackResult(ResultType resultType, float damage) {
		this.resultType = resultType;
		this.damage = damage;
	}
	
	public static enum ResultType {
		SUCCESS(true, true), FAILED(false, false), BLOCKED(false, true);
		
		boolean dealtDamage;
		boolean countInMaxStrikes;
		
		ResultType(boolean dealtDamage, boolean count) {
			this.dealtDamage = dealtDamage;
			this.countInMaxStrikes = count;
		}
		
		public boolean dealtDamage() {
			return this.dealtDamage;
		}
		
		public boolean count() {
			return this.countInMaxStrikes;
		}
	}
}