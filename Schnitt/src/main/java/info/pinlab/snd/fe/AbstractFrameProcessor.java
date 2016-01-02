package info.pinlab.snd.fe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFrameProcessor implements FrameProcessor {
	public static final Logger LOG = LoggerFactory.getLogger(AbstractFrameProcessor.class);
	final ParamSheet context ;
	private String key  = null;
	private String predecessorKey = null;


	public AbstractFrameProcessor(ParamSheet context){
		this.context = context;
		if(context==null){
			return;
		}
		init();
	}

	@Override
	public void setPredecessorKey(String key) {
		predecessorKey = key;
	}
	@Override
	public void setKey(String key) {
		this.key = key;
	}
	@Override
	public String getPredecessorKey() {
		return predecessorKey;
	}
	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void processWrapper(DoubleFrame frame){
		synchronized (this) {
			if(context==null){
				throw new IllegalStateException("Haven't initialized yet!");
			}
		}
		double [] arr = frame.getArray(getPredecessorKey());
		double  [] feat = process(arr);
		//-- feat==null: in-place processing
		if(feat!=null){
			frame.addArray(getKey(), feat);
		}
	}
}
