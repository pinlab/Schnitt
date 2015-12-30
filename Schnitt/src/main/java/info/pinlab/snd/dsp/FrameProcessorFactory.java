package info.pinlab.snd.dsp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
	public static Logger LOG = LoggerFactory.getLogger(FrameProcessorFactory.class);
	
	static String paramGetterName = "getProcessorParams";
	
/*	public static ProcessorParameter[] getParameters(String fqcn) {
		try {
			Class<?> clazz = Class.forName(fqcn);
//			System.out.println(clazz);
			for(Field field : clazz.getFields()){
				for(Annotation anno : field.getAnnotations()){
					String paramLabel = field.getName();
					String paramId    = fqcn + "#" + paramLabel;
					
					System.out.println(anno instanceof ProcessorParam);
					System.out.println(fqcn + "#" + field.getName());
					if(field.getType().isAssignableFrom(Integer.TYPE)){
						System.out.println(field.getInt(null));
					}else
					if(field.getType().isAssignableFrom(Double.TYPE)){
						System.out.println(field.getDouble(null));
					}
					else{
						field.get(null);
					}
				}
			}
			Method method = clazz.getMethod(paramGetterName, new Class[]{});
			ProcessorParameter[] params = (ProcessorParameter[]) method.invoke(null, (Object[])null);
			return params;
		} catch (ClassNotFoundException e) {
			LOG.error("No such class as '" + fqcn +"'");
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			LOG.error("No such method as '" + fqcn +"#" + paramGetterName +"'");
			for(StackTraceElement trace: e.getStackTrace()){
				LOG.error(trace.toString());
			}
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	
	public static  synchronized <T extends FrameProcessor> T create(Class<T> clazz, ParameterSheet context) 
			throws NoSuchMethodException, SecurityException, InstantiationException, InvocationTargetException, 
			IllegalAccessException, IllegalArgumentException{
		Constructor<T> constructor = (Constructor<T>) clazz.getConstructor(new Class[]{ParameterSheet.class});
		T processor = constructor.newInstance(context);
		return processor;
	}
	
	
	public static synchronized FrameProcessor create(String fqcn, ParameterSheet context) throws ClassNotFoundException, InstantiationException, 
	IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Class<?> clazz = Class.forName(fqcn);
		
		@SuppressWarnings("unchecked")
		Constructor<FrameProcessor> constructor = (Constructor<FrameProcessor>) clazz.getConstructor(new Class[]{ParameterSheet.class});
		FrameProcessor processor = constructor.newInstance(context);
		return processor;
	}
	
	public static void main(String [] args){
	}
	
}
