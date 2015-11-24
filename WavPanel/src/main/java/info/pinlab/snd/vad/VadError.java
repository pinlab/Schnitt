package info.pinlab.snd.vad;

public enum VadError {
	
	
	//-- targ  -----
	//-- hypo  -----
	TN (0, "sil",    "correctly detected silence"),
	//-- targ  +++++
	//-- hypo  +++++
	TP (1, "speech", "correctly detected speech"),
	
	
	//-- targ  ---+++++---
	//-- hypo  -----------
	WC (2, "w-clip",  "word clipe (voice not recognized at all)"),

	//-- targ  -----------
	//-- hypo  ---+++++---
	NDS_1 (2, "extra",  "noise detected as speech within silence"),
	//-- targ  +++-----+++
	//-- hypo  +++++++++++
	NDS_2 (2, "extra",  "noise detected as speech within silence"),
	
	
	//-- targ  +++++++++++
	//-- hypo  ++++---++++
	MSC  (3, "clip",   "incorrectly silence mid speech"),
	//-- targ  +++++++----
	//-- hypo  +++++++++++
	TAIL  (4, "tail",   "overlong speech detection"),
	//-- targ  -----++++++
	//-- hypo  --+++++++++
	HEAD  (5, "head",   "too early speech detection"),
	//-- targ  ---++++++++
	//-- hypo  ------+++++
	FEC  (6, "headclip",   "front end clipping"),
	//-- targ  ++++++++++-
	//-- hypo  +++++++----
	REC  (7, "tailclip",   "rear end clipping"),
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
