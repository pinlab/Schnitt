package info.pinlab.snd.dsp;

public abstract class AbstractFrameProcessor implements FrameProcessor {
	final ParameterSheet context ;

	
	public AbstractFrameProcessor(ParameterSheet context){
		this.context = context;
		init();
	}
	
	
	@Override
	public void processWrapper(DoubleFrame frame){
		synchronized (this) {
			if(context==null){
				throw new IllegalStateException("Haven't initialized yet!");
			}
		}
		double [] arr = frame.get(getPredecessorKey());
		double  [] feat = process(arr);
		//-- feat==null: in-place processing
		if(feat!=null){
			frame.put(feat, getKey());
		}
	}
}
