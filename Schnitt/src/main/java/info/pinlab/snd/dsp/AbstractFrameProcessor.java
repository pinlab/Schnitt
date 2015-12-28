package info.pinlab.snd.dsp;

public abstract class AbstractFrameProcessor implements FrameProcessor {
	final AcousticContext context ;

	
	public AbstractFrameProcessor(AcousticContext context){
		this.context = context;
		init();
	}
	
	
	@Override
	public void process(DoubleFrame frame){
		synchronized (this) {
			if(context==null){
				throw new IllegalStateException("Haven't initialized yet!");
			}
		}
		double [] arr = frame.get(getPredecessorKey());
		double  [] feat = innerProcess(arr);
		//-- feat==null: in-place processing
		if(feat!=null){
			frame.put(feat, getKey());
		}
	}
}
