package info.pinlab.snd.vad;

public enum VadError {
	
	
	TSIL (0, "sil",    "correctly detected silence"),
	TSP  (1, "speech", "correctly detected speech"),
	NDS  (2, "false",   "noise detected as speech within silence"),
	MSC  (3, "midclip", "mid speech clipping"),
	OVER (4, "tail", "overlong speech detection"),
	FEC  (5, "clip",  "clipping front part"),
	;
	
	
	public final int ix;
	public final String label;
	public final String brief;
	
	private VadError(int ix, String label, String brief){
		this.ix = ix;
		this.label = label;
		this.brief = brief;
	}
	
	
}
