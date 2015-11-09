package info.pinlab.snd.vad;

public enum VadError {
	
	//-- targ  -----
	//-- hypo  -----
	TN (0, "sil",    "correctly detected silence"),
	//-- targ  +++++
	//-- hypo  +++++
	TP (1, "speech", "correctly detected speech"),
	//-- targ  -----------
	//-- hypo  ---+++++---
	FP_CLIP  (2, "extra",  "noise detected as speech within silence"),
	//-- targ  +++++++++++
	//-- hypo  ++++---++++
	FN_CLIP  (3, "clip",   "incorrectly silence mid speech"),
	//-- targ  +++++++----
	//-- hypo  +++++++++++
	FP_TAIL  (4, "tail",   "overlong speech detection"),
	//-- targ  -----++++++
	//-- hypo  --+++++++++
	FP_HEAD  (5, "head",   "too early speech detection"),
	//-- targ  ---++++++++
	//-- hypo  ------+++++
	FN_HEAD  (6, "headclip",   "clipping front part"),
	//-- targ  ++++++++++-
	//-- hypo  +++++++----
	FN_TAIL  (7, "tailclip",   "clipping end part"),
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
