package info.pinlab.snd.dsp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * 
 * Thread-safe creation of FrameProcessors.
 * Uses Reflection.
 * 
 * 
 * @author Gabor Pinter
 *
 */
public class FrameProcessorFactory {

	public static  synchronized <T extends FrameProcessor> T create(Class<T> clazz, AcousticContext context) 
			throws NoSuchMethodException, SecurityException, InstantiationException, InvocationTargetException, 
			IllegalAccessException, IllegalArgumentException{
		Constructor<T> constructor = (Constructor<T>) clazz.getConstructor(new Class[]{AcousticContext.class});
		T processor = constructor.newInstance(context);
		return processor;
	}
	
	
	public static synchronized FrameProcessor create(String fqcn, AcousticContext context) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = Class.forName(fqcn);
		
		@SuppressWarnings("unchecked")
		Constructor<FrameProcessor> constructor = (Constructor<FrameProcessor>) clazz.getConstructor(new Class[]{AcousticContext.class});
		FrameProcessor processor = constructor.newInstance(context);
		return processor;
	}
	
}
