package info.pinlab.schnitt.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import info.pinlab.schnitt.gui.WavTableData;
import info.pinlab.schnitt.gui.WavTableRow;

@SuppressWarnings("serial")
public class WavTableImpl extends JPanel
implements TableModelListener, ListSelectionListener{
	public static Logger LOG = LoggerFactory.getLogger(WavTableImpl.class);


	private final JTable table;

	private enum TableCols{
		ICON(0,""),
		SERIAL(1, "#"),
		NAME(2, "file"),
		DUR(3, "ms"),
		TIERS(4, "tier")
		;
		final int ix;
		final String hdr;
		static String [] colNames ;

		TableCols(int index, String header){
			ix = index;
			hdr = header;

		}
		static String getHdrName(int ix){
			return colNames[ix];
		}
		static int size(){
			return colNames.length;
		}
		static{
			colNames = new String[TableCols.values().length];
			for(TableCols t : TableCols.values()){
				colNames[t.ix] = t.hdr;
			}
		}
	}


	static class WavTableColumnFormat implements TableFormat<WavTableRow>{
		@Override
		public int getColumnCount() {
			return TableCols.size();
		}
		@Override
		public String getColumnName(int ix) {
			return TableCols.getHdrName(ix);
		}

		@Override
		public Object getColumnValue(WavTableRow row, int colIx) {
			if(colIx == TableCols.DUR.ix){
				return row.getLabelForDur();
			}
			if(colIx == TableCols.NAME.ix){
				return row.getLabelForFname();
			}
			if(colIx == TableCols.TIERS.ix){
				return ""+row.getTierN();
			}
			return "col= "+colIx;
		}

	}

	//	static class WavTableColumnFormat implements WritableTableFormat<W>{
	//
	//		@Override
	//		public int getColumnCount() {
	//			return TableCols.size();
	//		}
	//		@Override
	//		public String getColumnName(int ix) {
	//			return TableCols.getHdrName(ix);
	//		}
	//
	//		@Override
	//		public Object getColumnValue(String arg0, int arg1) {
	//			return "Val="+arg0+arg1;
	//		}
	//
	//		@Override
	//		public boolean isEditable(String arg0, int arg1) {
	//			return false;
	//		}
	//
	//		@Override
	//		public String setColumnValue(String arg0, Object arg1, int arg2) {
	//			return null;
	//		}
	//		
	//	}


	public class MyTableCellRenderer extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = -3003165669735051L;
		final Color col_oddRowBg =  Color.WHITE;
		final Color col_evenRowBg =  new Color(222, 241, 205);
		final Color col_modelRowBg =  new Color(194, 194, 194)/*=gray*/; //new Color(255, 196, 196);

		final Color missRowBg = new Color(255, 222, 173);//Color.RED; 
		final Color col_def = new Color (71, 98, 72);
		final Color col_hdr = new Color (106, 134, 79);
		final Color col_model = new Color (69, 110, 31);

		final ImageIcon icon_lock = new ImageIcon(getClass().getResource("img/lock_10x14.png"));
		//		final ImageIcon icon_lock = new ImageIcon(getClass().getResource("img/lock2_16x16.png"));
		//		final ImageIcon icon_lock = new ImageIcon(getClass().getResource("img/key01_14x14.png"));

		//		final ImageIcon icon_mic = new ImageIcon(getClass().getResource("img/mic2_14x14.png"));
		final ImageIcon icon_mic = new ImageIcon(getClass().getResource("img/headset_16x16.png"));
		final ImageIcon icon_snd = new ImageIcon(getClass().getResource("img/snd01_16x16.png"));

		Font font_DUR = new Font("Monospaced", Font.PLAIN, 10); 
		Font font_def = new Font("Tahoma", Font.PLAIN, 12);
		Font font_defBold = new Font("Tahoma", Font.BOLD, 12);
		//		Font font_hdr = new Font("Tahoma", Font.BOLD, 12);

		Border border = BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY);
		Border border2 = BorderFactory.createEmptyBorder();

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int rowIx,
				int colIx) {
			setOpaque(true);


			if(rowIx < 0){
				JTableHeader header = table.getTableHeader();
				if (header != null) {
					setForeground(col_hdr);
					setBackground(header.getBackground());
					setFont(font_defBold);
				}
				if(colIx==TableCols.NAME.ix){
					setHorizontalAlignment(LEFT);
					setText(" " + value.toString());
				}else if (colIx==TableCols.DUR.ix){
					setHorizontalAlignment(RIGHT);
					setText(value.toString() + " ");
				}else{
					setHorizontalAlignment(CENTER);
					setText(value.toString());
				}
				setBorder(border);
				return this;
			}

			WavTableRow rowObj = (WavTableRow) value;
			setIcon(null);
			setText("");
			if(colIx==TableCols.SERIAL.ix ){
				setHorizontalAlignment(JLabel.RIGHT);
				setText(""+(rowIx+1));
				setFont(font_def);
				setForeground(col_def);
			}else if(colIx==TableCols.ICON.ix ){
				//-- ICONS --//
				setIcon(null);
				setText("");
				setHorizontalAlignment(JLabel.CENTER);
				//				
				//				if(rowObj.hasAudio()){
				//                	if(rowObj.isModel()){
				//                		setIcon(icon_lock);
				//                	}else
				//                    if(aaw.isUser()){
				//                    	setIcon(icon_mic);
				//                    }else{
				//                    	setIcon(icon_snd);
				//                    }
				//				}
			}else if(colIx==TableCols.NAME.ix ){
				setIcon(null);

				setText(rowObj.getLabelForFname());
				setHorizontalAlignment(JLabel.LEFT);

				setFont(font_def);
				setForeground(col_def);
			}else if(colIx==TableCols.DUR.ix ){
				setText(rowObj.getLabelForDur());
				setHorizontalAlignment(JLabel.CENTER);
				setFont(font_DUR);
				setForeground(Color.BLACK);
			}else if(colIx==TableCols.TIERS.ix){
				setText(rowObj.getTierN()+" tiers");
				setHorizontalAlignment(JLabel.RIGHT);
				setFont(font_def);
				setForeground(col_def);
			}
			setBorder(border2);

			if(isSelected){
				setBackground(table.getSelectionBackground());
				setForeground(table.getSelectionForeground());
			}else{
				if(rowIx % 2 == 0){
					setBackground(col_evenRowBg);
				}else{
					setBackground(col_oddRowBg);
				}
			}
			return this;
		}

	}



	public WavTableImpl(WavTableData model){
		this.setLayout(new GridBagLayout());
		
		DefaultEventTableModel<WavTableRow> tableModel = 
				new DefaultEventTableModel<WavTableRow>(model.getEventList(), new WavTableColumnFormat());
		table = new JTable(tableModel);
		table.getModel().addTableModelListener(this);

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(this);

		JScrollPane scrollPane = new JScrollPane(table);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridx = 0; gbc.gridy=0;
		this.add(scrollPane, gbc);
		//		table.setColumnModel(new WavTableColumnFormat());
	}



	public static void main(String[] args) throws Exception{

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				WavTableData tableData = new WavTableData();
				String [] wavs = new String []{		
						"/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/sample.wav"
						,"/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/longsample.wav"
						,"/home/kinoko/workspace/schnitt/WavPanel/src/main/resources/info/pinlab/snd/gui/verylongsample.wav"
				};
				
				
//				WavTableData model = new WavTableData();
				for(String wav: wavs){
					try{
						tableData.addWavFile(wav);
					}catch(IOException e){};
				}

				JPanel panel = new WavTableImpl(tableData);
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.fill = GridBagConstraints.BOTH;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				gbc.gridx = 0; gbc.gridy=0;
				
				
				JFrame frame = new JFrame("WavPanel test");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(panel, null);
				frame.setSize(800, 400);

				//		frame.pack();
				frame.setVisible(true);
			}
		}
				);




	}





	@Override
	public void tableChanged(TableModelEvent e) {
		// TODO Auto-generated method stub

	}





	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		// TODO Auto-generated method stub

	}
}
