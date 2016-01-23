package info.pinlab.schnitt.gui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import info.pinlab.snd.fe.FEParam;
import info.pinlab.snd.fe.ParamSheet;

public class ParamPanel extends JPanel {

	private final ParamSheet sheet;
	private final JButton btn;
	
	private static final Map<Class<?>, String> paramLabel = new HashMap<Class<?>, String>(); 
	
	
	static{
		paramLabel.put(FEParam.class, "Basic Params");
		
	}
	
	
	
	public ParamPanel(ParamSheet sheet){
		this.sheet = sheet;
		this.setLayout(new GridBagLayout());

		
		String label = ""; 
		for(Class<?> cls : sheet.getParamParentClasses()){
			label = paramLabel.get(cls);
			System.out.println(cls);
		}
		JLabel topLabel = new JLabel(label, SwingConstants.CENTER);
		
		
		int y = 0;
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 2;
		gbc.gridy = y++;
		gbc.ipady = 15;
		gbc.weightx = 1.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(topLabel, gbc);

		gbc.ipady = 0;
		gbc.ipadx = 2;
		gbc.gridwidth = 1;
		for(FEParam param : this.sheet.getParamsForParentClazz(FEParam.class)){
			gbc.gridy = y++;
			System.out.println(param.getKey());

			gbc.gridx = 0;
			this.add(new JLabel(param.getKey()), gbc);
			
			gbc.gridx = 1;
			this.add(new JTextField(4), gbc);
		}
		
		
		//-- stretch --//
		gbc.gridwidth = 2;
		gbc.gridy = y++;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(new JLabel(""), gbc);
		
		//-- bottom button --//
		btn = new JButton("Recalculate!");
		gbc.gridy = y++;
		gbc.insets = new Insets(0, 4, 4, 4);
		gbc.ipadx = 10;
		gbc.ipady = 10;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		this.add(btn, gbc);
	}
	
	
	
	public static void main(String[] args) {
		
		ParamSheet sheet = new ParamSheet.ParamSheetBuilder().build();
		
		
		JFrame frame = new JFrame("Param setters");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new ParamPanel(sheet));
		frame.setSize(200, 800);
		//		frame.pack();
		frame.setVisible(true);

		
	}

}
