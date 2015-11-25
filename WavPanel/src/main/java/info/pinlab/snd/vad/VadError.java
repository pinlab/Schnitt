package info.pinlab.snd.vad;

public enum VadError {
	
	
	//-- targ  -----
	//-- hypo  -----
	TN (0, "<sil>",    "correctly detected silence"),
	//-- targ  +++++
	//-- hypo  +++++
	TP (1, "<speech>", "correctly detected speech"),
	
	
	//-- targ  ---+++++---
	//-- hypo  -----------
	WC (2, "w-clip",  "word clip (voice not recognized at all)"),

	//-- targ  -----------
	//-- hypo  ---+++++---
	NDS_1 (3, "extra",  "noise detected as speech within silence"),
	//-- targ  +++-----+++
	//-- hypo  +++++++++++
	NDS_2 (4, "extra",  "noise detected as speech within silence"),
	
	
	//-- targ  +++++++++++
	//-- hypo  ++++---++++
	MSC  (4, "clip",   "mid speech clip: incorrect silence within speech"),
	//-- targ  +++++++----
	//-- hypo  +++++++++++
	TAIL  (5, "tail",   "overlong speech detection"),
	//-- targ  -----++++++
	//-- hypo  --+++++++++
	HEAD  (6, "head",   "too early speech detection"),
	//-- targ  ---++++++++
	//-- hypo  ------+++++
	FEC  (7, "headclip",   "front end clipping"),
	//-- targ  ++++++++++-
	//-- hypo  +++++++----
	REC  (8, "tailclip",   "rear end clipping"),
	;
	
	
	
	
	public final int ix;
	public final String label;
	public final String brief;

	
	private VadError(int ix, String label, String brief){
		this.ix = ix;
		this.label = label;
		this.brief = brief;
	}
	
	@Override
	public String toString(){
		return this.name();
	}
	
}
