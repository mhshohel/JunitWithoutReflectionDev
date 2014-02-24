package callgraphstat.superclass;

public class Enam {
	public enum EnamCat {
		GENERATED("Generated Code"), REGULAR("Regular Class"), TEST(
				"Test Class");
		private String category;

		private EnamCat(String category) {
			this.category = category;
		}

		public String toString() {
			return this.category;
		}
	}
}
