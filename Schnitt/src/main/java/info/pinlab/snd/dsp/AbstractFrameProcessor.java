package info.pinlab.snd.dsp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractFrameProcessor implements FrameProcessor {
	public static final Logger LOG = LoggerFactory.getLogger(AbstractFrameProcessor.class);
	final ParameterSheet context ;
	private String key  = null;
	private String predecessorKey = null;


	public AbstractFrameProcessor(ParameterSheet context){
		this.context = context;
		if(context==null){
			return;
		}
		//-- set parameters
		//		Class<?> clazz = Class.forName(fqcn);
		Class<?> clazz = this.getClass();
		//		System.out.println(clazz);
		for(Field field : clazz.getFields()){
			for(Annotation anno : field.getAnnotations()){
				try {
					if(anno instanceof ParamInt){
						//-- get key
						String paramLabel = ((ParamInt)anno).label();
						if(paramLabel==null){
							paramLabel = field.getName();
						}
						//-- get value
						Integer value = context.getInteger(paramLabel);
						LOG.info("Setting " + paramLabel +"=" + value); 

						if(value!=null){
							field.setInt(this, value);
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
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
		double [] arr = frame.get(getPredecessorKey());
		double  [] feat = process(arr);
		//-- feat==null: in-place processing
		if(feat!=null){
			frame.put(feat, getKey());
		}
	}
}
