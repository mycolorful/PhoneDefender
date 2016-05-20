package per.yrj.model;

public class BlackNumberInfo {
	private String number;
	private String mode;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		if ("1".equals(mode) || "2".equals(mode) || "3".equals(mode)) {
			this.mode = mode;
		}else{
			this.mode = "0";
		}
	}

}
