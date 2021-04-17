package maninthehouse.epicfight.utils.game;

public class Pair<T, E> {
	private T first;
	private E second;
	
	public Pair(T first, E second) {
		this.first = first;
		this.second = second;
	}
	
	public T first() {
		return this.first;
	}
	
	public E second() {
		return this.second;
	}
	
	public static <T, E> Pair<T, E> of(T first, E second) {
		return new Pair<T, E> (first, second);
	}
}
