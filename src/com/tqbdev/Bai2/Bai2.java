package com.tqbdev.Bai2;

import com.tqbdev.components.*;
import com.tqbdev.utils.*;

import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

public class Bai2 extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2580130665854469877L;
	private static final int bufferSize = 16384;

	enum Focus {
		Left, Right
	}

	enum TypeOS {
		Windows, MacOS, Unix
	}

	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private TypeOS typeOS = TypeOS.Windows;

	private static final String appName = "File manager";

	// Area Components (Layout main program)
	private JPanel contentPane;
	private JPanel leftBrowser;
	private JPanel rightBrowser;
	private JPanel toolBar;
	//

	// Components in layouts
	private DriveComboBox leftDriveCombo;
	private DriveComboBox rightDriveCombo;

	private JTextField leftInfoDrive;
	private JTextField rightInfoDrive;

	private FileTableModel leftFileTableModel;
	private FileTableModel rightFileTableModel;

	private JTable leftTableFile;
	private JTable rightTableFile;

	private JTextField leftAddressText;
	private JTextField rightAddressText;

	private JTextField leftInfoTableFile;
	private JTextField rightInfoTableFile;
	//

	//
	private int leftTableRowSelectedinModel = 0;
	private int rightTableRowSelectedinModel = 0;

	private Focus focusBrowser = Focus.Left;

	private File leftCurrentFolder;
	private File rightCurrentFolder;
	//

	// Filter file/folder
	private FilterFileHiddenAndLink filterFile = new FilterFileHiddenAndLink();
	//

	private void getTypeOS() {
		try {
			final String os = System.getProperty("os.name").toLowerCase();

			if (os.contains("win")) {
				typeOS = TypeOS.Windows;
			} else if (os.contains("mac")) {
				typeOS = TypeOS.MacOS;
			} else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
				typeOS = TypeOS.Unix;
			}
		} catch (final Exception e) {
			Object[] options = { "OK" };
			JOptionPane.showOptionDialog(this,
					"You are running in OS that we don't support now!\r\nWe just support Windows (best support), MacOS, *nix, *nux, *aix.\r\nQuit...",
					"Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
			Runtime.getRuntime().exit(1);
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}

				Bai2 frame = null;
				try {
					frame = new Bai2();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Bai2() {
		getTypeOS();

		setTitle(appName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		ImageIcon main = new ImageIcon(getClass().getResource("/icons/main.png"));
		setIconImage(main.getImage());

		initLayoutGUI();
		initToolBarButtons();

		leftAddressText = new JTextField();
		leftAddressText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(leftAddressText.getText());

				if (file.exists() && file.isDirectory()) {
					File driveCur = FileUtils.getDrive(leftCurrentFolder);
					File driveNew = FileUtils.getDrive(file);

					if (!driveCur.equals(driveNew)) {
						leftDriveCombo.setSelectedItem(driveNew);
					}

					leftCurrentFolder = file;
					leftFileTableModel.setFiles(file.listFiles(filterFile));
					leftAddressText.setText(file.getAbsolutePath());
				} else {
					showWarning("Path doesn't exist\r\nTry another path", "Warning");
					leftAddressText.setText(leftCurrentFolder.getAbsolutePath());
				}
			}
		});

		rightAddressText = new JTextField();
		rightAddressText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(rightAddressText.getText());

				if (file.exists() && file.isDirectory()) {
					File driveCur = FileUtils.getDrive(rightCurrentFolder);
					File driveNew = FileUtils.getDrive(file);

					if (!driveCur.equals(driveNew)) {
						rightDriveCombo.setSelectedItem(driveNew);
					}

					rightCurrentFolder = file;
					rightFileTableModel.setFiles(file.listFiles(filterFile));
					rightAddressText.setText(file.getAbsolutePath());
				} else {
					showWarning("Path doesn't exist\r\nTry another path", "Warning");
					rightAddressText.setText(rightCurrentFolder.getAbsolutePath());
				}
			}
		});

		leftInfoTableFile = new JTextField();
		leftInfoTableFile.setBorder(new EmptyBorder(5, 5, 5, 5));
		leftInfoTableFile.setHighlighter(null);
		leftInfoTableFile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		leftInfoTableFile.setEditable(false);

		rightInfoTableFile = new JTextField();
		rightInfoTableFile.setBorder(new EmptyBorder(5, 5, 5, 5));
		rightInfoTableFile.setHighlighter(null);
		rightInfoTableFile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		rightInfoTableFile.setEditable(false);

		JPanel leftComboPanel = new JPanel();
		leftComboPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		leftComboPanel.setLayout(new BoxLayout(leftComboPanel, BoxLayout.X_AXIS));
		leftComboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		leftInfoDrive = new JTextField();
		leftInfoDrive.setEditable(false);

		leftDriveCombo = new DriveComboBox();
		leftDriveCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					try {
						File itemFile = (File) item;
						File[] lstFiles = itemFile.listFiles(filterFile);

						if (leftFileTableModel != null) {
							leftFileTableModel.setFiles(lstFiles);
						}

						leftCurrentFolder = itemFile;
						leftInfoDrive.setText(FileUtils.genInfoDrive(itemFile));

						leftAddressText.setText(leftCurrentFolder.getAbsolutePath());
						leftInfoTableFile.setText(FileUtils.genInfoListFile(lstFiles));

						resizeColumnWidth(leftTableFile);
					} catch (Exception ex) {

					}
				}
			}
		});

		JPanel rightComboPanel = new JPanel();
		rightComboPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		rightComboPanel.setLayout(new BoxLayout(rightComboPanel, BoxLayout.X_AXIS));
		rightComboPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		rightInfoDrive = new JTextField();
		rightInfoDrive.setEditable(false);

		rightDriveCombo = new DriveComboBox();
		rightDriveCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
		rightDriveCombo.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					try {
						File itemFile = (File) item;
						File[] lstFiles = itemFile.listFiles(filterFile);
						if (rightFileTableModel != null) {
							rightFileTableModel.setFiles(lstFiles);
						}

						rightCurrentFolder = itemFile;
						rightInfoDrive.setText(FileUtils.genInfoDrive(itemFile));

						rightAddressText.setText(rightCurrentFolder.getAbsolutePath());
						rightInfoTableFile.setText(FileUtils.genInfoListFile(lstFiles));

						resizeColumnWidth(rightTableFile);
					} catch (Exception ex) {

					}
				}
			}
		});

		File[] drives = File.listRoots();

		leftDriveCombo.setMaximumSize(new Dimension(150, 30));
		leftDriveCombo.setEditable(true);
		leftDriveCombo.addItems(drives);

		rightDriveCombo.setMaximumSize(new Dimension(150, 30));
		rightDriveCombo.setEditable(true);
		rightDriveCombo.addItems(drives);

		leftComboPanel.add(leftDriveCombo);
		leftComboPanel.add(leftInfoDrive);
		rightComboPanel.add(rightDriveCombo);
		rightComboPanel.add(rightInfoDrive);
		leftBrowser.add(leftComboPanel);
		rightBrowser.add(rightComboPanel);

		JPanel leftAddressPanel = new JPanel();
		leftAddressPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		leftAddressPanel.setLayout(new BoxLayout(leftAddressPanel, BoxLayout.X_AXIS));
		leftAddressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JButton leftBackParent = new JButton();
		leftBackParent.setIcon(new ImageIcon(getClass().getResource("/icons/up.png")));
		leftAddressPanel.add(leftBackParent);
		leftBackParent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File raw = backToFolder(leftTableFile, leftCurrentFolder);
				if (raw != null)
					leftCurrentFolder = raw;

				leftInfoTableFile.setText(FileUtils.genInfoListFile(leftCurrentFolder.listFiles(filterFile)));
				leftAddressText.setText(leftCurrentFolder.getAbsolutePath());
			}
		});

		leftAddressPanel.add(leftAddressText);

		leftBrowser.add(leftAddressPanel);

		JPanel rightAddressPanel = new JPanel();
		rightAddressPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		rightAddressPanel.setLayout(new BoxLayout(rightAddressPanel, BoxLayout.X_AXIS));
		rightAddressPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

		JButton rightBackParent = new JButton();
		rightBackParent.setIcon(new ImageIcon(getClass().getResource("/icons/up.png")));
		rightAddressPanel.add(rightBackParent);
		rightBackParent.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File raw = backToFolder(rightTableFile, rightCurrentFolder);
				if (raw != null)
					rightCurrentFolder = raw;

				rightInfoTableFile.setText(FileUtils.genInfoListFile(rightCurrentFolder.listFiles(filterFile)));
				rightAddressText.setText(rightCurrentFolder.getAbsolutePath());
			}
		});

		rightAddressPanel.add(rightAddressText);

		rightBrowser.add(rightAddressPanel);

		leftFileTableModel = new FileTableModel();
		rightFileTableModel = new FileTableModel();

		leftFileTableModel.setFiles(leftCurrentFolder.listFiles(filterFile));
		rightFileTableModel.setFiles(rightCurrentFolder.listFiles(filterFile));

		leftTableFile = initTableFile(leftFileTableModel);
		resizeColumnWidth(leftTableFile);
		initTableRowSorter(leftTableFile);
		leftTableFile.setRowSelectionInterval(0, 0);

		leftTableFile.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
					JTable table = (JTable) mouseEvent.getSource();
					Point point = mouseEvent.getPoint();
					int row = table.rowAtPoint(point);
					int modelRow = table.convertRowIndexToModel(row);

					leftBrowser.setBorder(new LineBorder(Color.BLUE));
					focusBrowser = Focus.Left;

					leftTableRowSelectedinModel = modelRow;
					int rightRow = rightTableFile.getSelectedRow();

					if (rightRow != -1) {
						rightTableRowSelectedinModel = rightTableFile.convertRowIndexToModel(rightRow);
						rightTableFile.removeRowSelectionInterval(rightRow, rightRow);
						rightBrowser.setBorder(new LineBorder(Color.BLACK));
					}

					if (mouseEvent.getClickCount() == 2) {
						File file = leftFileTableModel.getFile(modelRow);
						if (file.isDirectory()) {
							leftCurrentFolder = file;
							leftAddressText.setText(leftCurrentFolder.getAbsolutePath());

							File[] lstFiles = file.listFiles(filterFile);
							leftFileTableModel.setFiles(lstFiles);
							leftInfoTableFile.setText(FileUtils.genInfoListFile(lstFiles));

							resizeColumnWidth(table);

							leftTableRowSelectedinModel = 0;
							if (lstFiles.length > 0)
								leftTableFile.setRowSelectionInterval(0, 0);
						} else if (file.isFile()) {
							try {
								switch (typeOS) {
								case Windows:
									Runtime.getRuntime()
											.exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + file.getAbsolutePath());
									break;
								case MacOS:
									Runtime.getRuntime().exec("open -a " + file.getAbsolutePath());
									break;
								case Unix:
									Runtime.getRuntime().exec(file.getAbsolutePath());
									break;
								}
							} catch (Exception ex) {

							}
						}
					}
				}
			}
		});

		rightTableFile = initTableFile(rightFileTableModel);
		resizeColumnWidth(rightTableFile);
		initTableRowSorter(rightTableFile);

		rightTableFile.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getButton() == MouseEvent.BUTTON1) {
					JTable table = (JTable) mouseEvent.getSource();
					Point point = mouseEvent.getPoint();
					int row = table.rowAtPoint(point);
					int modelRow = table.convertRowIndexToModel(row);

					rightBrowser.setBorder(new LineBorder(Color.BLUE));
					focusBrowser = Focus.Right;

					rightTableRowSelectedinModel = modelRow;
					int leftRow = leftTableFile.getSelectedRow();

					if (leftRow != -1) {
						leftTableRowSelectedinModel = leftTableFile.convertRowIndexToModel(leftRow);
						leftTableFile.removeRowSelectionInterval(leftRow, leftRow);
						leftBrowser.setBorder(new LineBorder(Color.BLACK));
					}

					if (mouseEvent.getClickCount() == 2) {
						File file = rightFileTableModel.getFile(modelRow);
						if (file.isDirectory()) {
							rightCurrentFolder = file;
							rightAddressText.setText(rightCurrentFolder.getAbsolutePath());

							File[] lstFiles = file.listFiles(filterFile);
							rightFileTableModel.setFiles(lstFiles);
							resizeColumnWidth(table);
							rightInfoTableFile.setText(FileUtils.genInfoListFile(lstFiles));

							rightTableRowSelectedinModel = 0;
							if (lstFiles.length > 0)
								rightTableFile.setRowSelectionInterval(0, 0);
						} else if (file.isFile()) {
							try {
								switch (typeOS) {
								case Windows:
									Runtime.getRuntime()
											.exec("rundll32 SHELL32.DLL,ShellExec_RunDLL " + file.getAbsolutePath());
									break;
								case MacOS:
									Runtime.getRuntime().exec("open -a " + file.getAbsolutePath());
									break;
								case Unix:
									Runtime.getRuntime().exec(file.getAbsolutePath());
									break;
								}
							} catch (Exception ex) {

							}
						}
					}
				}
			}
		});

		JScrollPane leftTableScroll = new JScrollPane(leftTableFile, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		leftTableScroll.getViewport().setBackground(Color.WHITE);

		leftTableScroll.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				int rowRight = rightTableFile.convertRowIndexToView(rightTableRowSelectedinModel);
				rightTableFile.removeRowSelectionInterval(rowRight, rowRight);
				rightBrowser.setBorder(new LineBorder(Color.BLACK));

				int rowLeft = leftTableFile.convertRowIndexToView(leftTableRowSelectedinModel);
				leftTableFile.setRowSelectionInterval(rowLeft, rowLeft);
				leftBrowser.setBorder(new LineBorder(Color.BLUE));
				focusBrowser = Focus.Left;
			}
		});

		leftBrowser.add(leftTableScroll);

		JScrollPane rightTableScroll = new JScrollPane(rightTableFile, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		rightTableScroll.getViewport().setBackground(Color.WHITE);

		rightTableScroll.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mouseEvent) {
				int rowLeft = leftTableFile.convertRowIndexToView(leftTableRowSelectedinModel);
				leftTableFile.removeRowSelectionInterval(rowLeft, rowLeft);
				leftBrowser.setBorder(new LineBorder(Color.BLACK));

				int rowRight = rightTableFile.convertRowIndexToView(rightTableRowSelectedinModel);
				rightTableFile.setRowSelectionInterval(rowRight, rowRight);
				rightBrowser.setBorder(new LineBorder(Color.BLUE));
				focusBrowser = Focus.Right;
			}
		});

		rightBrowser.add(rightTableScroll);

		//
		leftBrowser.add(leftInfoTableFile);
		rightBrowser.add(rightInfoTableFile);
	}

	private File backToFolder(JTable table, File folderCurrent) {
		FileTableModel model = (FileTableModel) table.getModel();

		if (model != null && folderCurrent != null) {
			File parent = folderCurrent.getParentFile();
			if (parent != null && !fileSystemView.isDrive(folderCurrent)) {
				model.setFiles(parent.listFiles(filterFile));
				resizeColumnWidth(table);
				return parent;
			}
		}

		return null;
	}

	private void initLayoutGUI() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		setContentPane(contentPane);

		leftBrowser = new JPanel();
		leftBrowser.setBorder(new LineBorder(Color.BLUE));
		contentPane.add(leftBrowser);
		leftBrowser.setLayout(new BoxLayout(leftBrowser, BoxLayout.Y_AXIS));

		toolBar = new JPanel();
		toolBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		toolBar.setMinimumSize(new Dimension(50, this.getHeight()));
		contentPane.add(toolBar);
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.Y_AXIS));

		rightBrowser = new JPanel();
		rightBrowser.setBorder(new LineBorder(Color.BLACK));
		contentPane.add(rightBrowser);
		rightBrowser.setLayout(new BoxLayout(rightBrowser, BoxLayout.Y_AXIS));
	}

	private JButton initToolbarButton(String pathToIcon, String toolTipText) {
		JButton button = new JButton();
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		if (pathToIcon != null)
			button.setIcon(new ImageIcon(getClass().getResource(pathToIcon)));
		if (toolTipText != null)
			button.setToolTipText(toolTipText);

		return button;
	}

	private JTable initTableFile(FileTableModel model) {
		JTable tableFile = new JTable();
		tableFile.setModel(model);
		tableFile.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableFile.setAutoCreateRowSorter(true);
		tableFile.setShowVerticalLines(false);
		tableFile.setShowHorizontalLines(false);
		tableFile.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableFile.setRowHeight(tableFile.getRowHeight() + 10);
		tableFile.getColumnModel().getColumn(0).setResizable(false);
		tableFile.getTableHeader().setReorderingAllowed(false);

		return tableFile;
	}

	private void initTableRowSorter(JTable table) {
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);
		final Comparator<String> stringComparator = new Comparator<String>() {
			@Override
			public int compare(String obj1, String obj2) {
				if (obj1 == null) {
					return -1;
				}
				if (obj2 == null) {
					return 1;
				}
				if (obj1.equals(obj2)) {
					return 0;
				}

				Boolean check1 = true, check2 = true;
				Double num1 = -1.0, num2 = -1.0;
				num1 = ConvertVolume.stringToSize(obj1);
				num2 = ConvertVolume.stringToSize(obj2);

				if (num1 < 0)
					check1 = false;
				if (num2 < 0)
					check2 = false;

				if (check1 && check2) {
					return num1.compareTo(num2);
				}

				if (check1)
					return -1;
				if (check2)
					return 1;

				return obj1.compareTo(obj2);
			}
		};

		sorter.setComparator(2, stringComparator);
		sorter.setComparator(0, new Comparator<ImageIcon>() {
			@Override
			public int compare(ImageIcon obj1, ImageIcon obj2) {
				return 0;
			}
		});
	}

	private void initToolBarButtons() {
		JButton editView = initToolbarButton("/icons/edit-view.png", "View/Edit text file");
		toolBar.add(editView);
		toolBar.add(Box.createVerticalStrut(5));

		editView.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewEditFileFunction();
			}
		});

		JButton copyBtn = initToolbarButton("/icons/copy.png", "Copy one file/folder");
		toolBar.add(copyBtn);
		toolBar.add(Box.createVerticalStrut(5));

		copyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyFunction();
			}
		});

		JButton splitBtn = initToolbarButton("/icons/split.png", "Split file");
		toolBar.add(splitBtn);
		toolBar.add(Box.createVerticalStrut(5));

		splitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				splitFileFunction();
			}
		});

		JButton joinBtn = initToolbarButton("/icons/join-files.png", "Join files");
		toolBar.add(joinBtn);
		toolBar.add(Box.createVerticalStrut(5));

		joinBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				joinFilesFunction();
			}
		});

		JButton reName = initToolbarButton("/icons/rename.png", "Rename file/folder");
		toolBar.add(reName);
		toolBar.add(Box.createVerticalStrut(35));

		reName.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				reNameFunction();
			}
		});

		JButton packBtn = initToolbarButton("/icons/pack.png", "Pack files and folders (support only zip file)");
		toolBar.add(packBtn);
		toolBar.add(Box.createVerticalStrut(5));

		packBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				packFunction();
			}
		});

		JButton unpackBtn = initToolbarButton("/icons/unpack.png", "UnPack files and folders (support only zip file)");
		toolBar.add(unpackBtn);
		toolBar.add(Box.createVerticalStrut(5));

		unpackBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				unPackFunction();
			}
		});

		JButton mkDir = initToolbarButton("/icons/mkdir.png", "Make folder (directory)");
		toolBar.add(mkDir);
		toolBar.add(Box.createVerticalStrut(5));

		mkDir.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFolderFunction();
			}
		});
	}

	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	private void showWarning(String warnMessage, String warnTitle) {
		JOptionPane.showMessageDialog(this, warnMessage, warnTitle, JOptionPane.WARNING_MESSAGE);
	}

	private void resizeColumnWidth(JTable table) {
		final TableColumnModel columnModel = table.getColumnModel();
		for (int column = 0; column < table.getColumnCount(); column++) {
			int width = 15; // Min width
			for (int row = 0; row < table.getRowCount(); row++) {
				TableCellRenderer renderer = table.getCellRenderer(row, column);
				Component comp = table.prepareRenderer(renderer, row, column);
				width = Math.max(comp.getPreferredSize().width + 1, width);
			}
			if (width < 50 && columnModel.getColumn(column).getHeaderValue() != "")
				width = 50;
			if (width > 300)
				width = 300;
			columnModel.getColumn(column).setPreferredWidth(width + 10);
		}
	}

	private File getFileSelected() {
		FileTableModel fileModel = null;

		int indexFileSelected = -1;

		if (focusBrowser == Focus.Left) {
			fileModel = (FileTableModel) leftTableFile.getModel();
			indexFileSelected = leftTableRowSelectedinModel;
		} else if (focusBrowser == Focus.Right) {
			fileModel = (FileTableModel) rightTableFile.getModel();
			indexFileSelected = rightTableRowSelectedinModel;
		}

		if (fileModel != null && fileModel.getRowCount() == 0) {
			showWarning("No file/folder is selected", "Warning");
			return null;
		}

		return fileModel.getFile(indexFileSelected);
	}

	private void viewEditFileFunction() {
		File fileSelected = getFileSelected();

		if (fileSelected == null || !fileSelected.isFile()) {
			showWarning("No file is selected", "Warning");
			return;
		}

		if (fileSelected != null && fileSelected.isFile()) {
			try {
				switch (typeOS) {
				case Windows:
					Runtime.getRuntime().exec(new String[] { "notepad", fileSelected.getAbsolutePath() });
					break;
				case MacOS:
					Runtime.getRuntime().exec(new String[] { "notepad", "-e", fileSelected.getAbsolutePath() });
					break;
				case Unix:
					Runtime.getRuntime().exec(new String[] { "gedit", fileSelected.getAbsolutePath() });
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
				showErrorMessage(e.getMessage(), "Error View/Edit File");
				return;
			}
		}
	}

	private FileTableModel getFileTableModelFrom() {
		if (focusBrowser == Focus.Left) {
			return (FileTableModel) leftTableFile.getModel();
		} else if (focusBrowser == Focus.Right) {
			return (FileTableModel) rightTableFile.getModel();
		}

		return null;
	}

	private FileTableModel getFileTableModelTo() {
		if (focusBrowser == Focus.Left) {
			return (FileTableModel) rightTableFile.getModel();
		} else if (focusBrowser == Focus.Right) {
			return (FileTableModel) leftTableFile.getModel();
		}

		return null;
	}

	private File getFolderTo() {
		if (focusBrowser == Focus.Left) {
			return rightCurrentFolder;
		} else if (focusBrowser == Focus.Right) {
			return leftCurrentFolder;
		}

		return null;
	}

	private File getFolderFrom() {
		if (focusBrowser == Focus.Left) {
			return leftCurrentFolder;
		} else if (focusBrowser == Focus.Right) {
			return rightCurrentFolder;
		}

		return null;
	}

	private int choose = -1;

	private void copyFunction() {
		choose = -1;

		final FileTableModel fileModelFrom = getFileTableModelFrom();
		final FileTableModel fileModelTo = getFileTableModelTo();

		final File folderFrom = getFolderFrom();
		final File folderTo = getFolderTo();

		final File fileFrom = getFileSelected();

		if (fileModelFrom == null || fileModelTo == null || folderTo == null || folderFrom == null) {
			return;
		}

		if (fileFrom == null) {
			showWarning("No file/folder is selected", "Warning");
			return;
		}

		int reply = JOptionPane.showConfirmDialog(this,
				"Will copy \"" + fileFrom.getAbsolutePath() + "\" to \"" + folderTo.getAbsolutePath() + "\". Confirm?",
				"Confirm", JOptionPane.INFORMATION_MESSAGE);

		if (reply == JOptionPane.YES_OPTION) {
			final ProgressDialog dlg = new ProgressDialog(this, "Copying Progress");

			if (fileFrom.isFile()) {
				File fileTo = new File(folderTo, fileFrom.getName());

				if (fileTo.exists()) {
					String[] options = new String[] { "Skip", "Replace", "Cancel" };
					String message = "File ";
					message += fileFrom.getAbsolutePath();
					message += " exist in ";
					message += fileTo.getAbsolutePath();
					int response = JOptionPane.showOptionDialog(null, message, "Confirm", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);

					switch (response) {
					case 0: // Skip
					case 2: // Cancel
						return;
					case 1: // Replace
						break;
					}
				}

				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileFrom));
							BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileTo));

							byte[] buf = new byte[bufferSize];
							int size = 0;
							long progress = 0;
							int percent = 0;
							long fileFromLength = fileFrom.length();

							while ((size = fis.read(buf)) != -1) {
								if (dlg.isCanceled()) {
									break;
								}

								fos.write(buf, 0, size);
								progress += size;
								percent = (int) (((double) progress * 100) / (double) fileFromLength);
								dlg.setValueProgress(percent);
								dlg.setText("Copying " + fileFrom.getName() + "... " + Integer.toString((int) percent)
										+ "%");
							}

							fis.close();
							fos.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							showErrorMessage(ex.getMessage(), "Error");
						}

						if (dlg.isCanceled()) {
							fileTo.delete();
						}

						fileModelTo.setFiles(fileTo.getParentFile().listFiles(filterFile));
						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);

			} else if (fileFrom.isDirectory()) {
				File[] lstFile = fileFrom.listFiles(filterFile);

				long temp = 0;

				for (File file : lstFile) {
					temp += file.length();
				}

				File dest = new File(folderTo, fileFrom.getName());

				if (!dest.exists()) {
					dest.mkdir();
				}

				final long totalLength = temp;

				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						long progressTotal = 0;
						long percentTotal = 0;

						for (File file : lstFile) {
							if (file.isDirectory()) {
								try {
									dlg.setText("Copying folder " + file.getName() + "...");
									copyDirectory(file, new File(dest, file.getName()));
								} catch (Exception e) {
									e.printStackTrace();

									dlg.setVisible(false);

									showErrorMessage(e.getMessage(), "Error View/Edit File");
									return null;
								}

							} else {
								File fileTo = new File(dest, file.getName());

								if (fileTo.exists()) {
									if (choose == -1) {
										String[] options = new String[] { "Skip", "Skip All", "Replace", "Replace All",
												"Stop Copy" };
										String message = "File ";
										message += file.getAbsolutePath();
										message += " exist in ";
										message += fileTo.getAbsolutePath();
										int response = JOptionPane.showOptionDialog(null, message, "Confirm",
												JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
												options[0]);

										switch (response) {
										case 0: // Skip
											continue;
										case 1: // Skip All
											choose = 1;
											break;
										case 3: // Replace All
											choose = 2;
											break;
										case 4: // Stop copy
											return null;
										case 2: // Replace
											break;
										}
									} else if (choose == 1) {
										continue;
									}
								}

								try {
									BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
									BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileTo));

									byte[] buf = new byte[bufferSize];
									int size = 0;
									int percent = 0;
									long progress = 0;
									long fileFromLength = file.length();

									while ((size = fis.read(buf)) != -1) {
										fos.write(buf, 0, size);
										progressTotal += size;
										percentTotal = (int) (((double) progressTotal * 100) / (double) totalLength);
										dlg.setValueProgress((int) percentTotal);

										progress += size;
										percent = (int) (((double) progress * 100) / (double) fileFromLength);
										dlg.setText("Copying " + file.getName() + "... "
												+ Integer.toString((int) percent) + "%");
									}

									fis.close();
									fos.close();
								} catch (Exception ex) {
									ex.printStackTrace();
									showErrorMessage(ex.getMessage(), "Error");
								}

								if (dlg.isCanceled()) {
									fileTo.delete();
									break;
								}
							}

							if (dlg.isCanceled()) {
								break;
							}
						}

						fileModelTo.setFiles(dest.getParentFile().listFiles(filterFile));
						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);
			}
		}
	}

	private void splitFileFunction() {
		final FileTableModel fileModelFrom = getFileTableModelFrom();
		final FileTableModel fileModelTo = getFileTableModelTo();

		final File folderTo = getFolderTo();
		final File folderFrom = getFolderFrom();

		final File fileFrom = getFileSelected();

		if (fileModelFrom == null || fileModelTo == null || folderTo == null || folderFrom == null) {
			return;
		}

		if (fileFrom == null || !fileFrom.isFile()) {
			showWarning("No file is selected", "Warning");
			return;
		}

		int splitedParts = 4;
		try {
			splitedParts = Integer.parseInt(JOptionPane.showInputDialog("Enter number of files after splited?"));
		} catch (Exception e) {
			showWarning("Error number", "Warning");
			return;
		}

		final int splitParts = splitedParts;

		int reply = JOptionPane.showConfirmDialog(this,
				"Will split \"" + fileFrom.getAbsolutePath() + "\" into " + Integer.toString(splitParts)
						+ " files in \"" + folderTo.getAbsolutePath() + "\". Confirm?",
				"Confirm", JOptionPane.INFORMATION_MESSAGE);

		if (reply == JOptionPane.YES_OPTION) {
			if (fileFrom.isFile()) {
				final ProgressDialog dlg = new ProgressDialog(this, "Spliting Progress");

				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						long fileFromLength = fileFrom.length();
						long totalReaded = 0;

						// int splitParts = 4;
						int splitSize = (int) Math.ceil((double) fileFromLength / (double) splitParts);

						try {
							BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileFrom));

							for (int i = 0; i < splitParts; i++) {
								if (dlg.isCanceled()) {
									break;
								}

								int size = 0;

								File splitFile = new File(folderTo, fileFrom.getName() + "." + i);
								BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(splitFile));

								if (i == 0) { // first file store total length
									byte[] totalLength = ByteUtils.longToBytes(fileFromLength);
									fos.write(totalLength);
								}

								byte[] buf = new byte[bufferSize];
								long readed = 0;

								while (readed < splitSize) {
									int len = (int) (splitSize - readed);
									if (len >= bufferSize)
										len = bufferSize;

									if ((size = fis.read(buf, 0, len)) != -1) {
										fos.write(buf, 0, size);
										totalReaded += size;
										readed += size;
									} else {
										break;
									}

									int percentTotal = (int) (((double) totalReaded * 100) / (double) fileFromLength);
									dlg.setValueProgress(percentTotal);

									int percent = (int) (((double) readed * 100) / (double) splitSize);
									dlg.setText("Spliting to file " + splitFile.getName() + "... "
											+ Integer.toString((int) percent) + "%");
								}

								fos.close();
							}

							if (dlg.isCanceled()) {
								for (int i = 0; i < splitParts; i++) {
									File splitFile = new File(folderTo, fileFrom.getName() + "." + i);
									splitFile.delete();
								}
							}

							fis.close();
							fileModelTo.setFiles(folderTo.listFiles(filterFile));
						} catch (Exception ex) {
							ex.printStackTrace();

							for (int i = 0; i < splitParts; i++) {
								File splitFile = new File(folderTo, fileFrom.getName() + "." + i);
								splitFile.delete();
							}
							showErrorMessage(ex.getMessage(), "Error");
						}

						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);
			}
		}
	}

	private String getOriginalFileName(File splitedFile) {
		if (splitedFile == null || !splitedFile.isFile())
			return null;
		String splitedName = splitedFile.getName();

		int index = splitedName.lastIndexOf(".");
		if (index != -1) {
			String numPart = splitedName.substring(index + 1);
			try {
				Integer.parseInt(numPart);
			} catch (Exception ex) {
				return null;
			}

			return splitedName.substring(0, index);
		}

		return null;
	}

	private void joinFilesFunction() {
		final FileTableModel fileModelFrom = getFileTableModelFrom();
		final FileTableModel fileModelTo = getFileTableModelTo();

		final File folderTo = getFolderTo();
		final File folderFrom = getFolderFrom();

		final File fileFrom = getFileSelected();

		if (fileModelFrom == null || fileModelTo == null || folderTo == null || folderFrom == null) {
			return;
		}

		if (fileFrom == null || !fileFrom.isFile()) {
			showWarning("No file is selected", "Warning");
			return;
		}

		final String originalFileName = getOriginalFileName(fileFrom);
		if (originalFileName == null) {
			showWarning("File selected invalid format splited file", "Warning");
			return;
		}

		int reply = JOptionPane
				.showConfirmDialog(this,
						"Will join files \"" + fileFrom.getAbsolutePath() + "\" into file " + originalFileName
								+ " in \"" + folderTo.getAbsolutePath() + "\". Confirm?",
						"Confirm", JOptionPane.INFORMATION_MESSAGE);

		if (reply == JOptionPane.YES_OPTION) {
			if (fileFrom.isFile()) {
				final ProgressDialog dlg = new ProgressDialog(this, "Joining Progress");
				final File fileOriginal = new File(folderTo, originalFileName);

				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						try {
							int i = 0;
							File fileSplitedI = new File(folderFrom, originalFileName + "." + i);

							BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(fileOriginal));

							long originalLength = -1;
							long totalReaded = 0;

							byte[] buf = new byte[bufferSize];
							int size = 0;
							while (fileSplitedI.exists() && !dlg.isCanceled()) {
								BufferedInputStream fis = new BufferedInputStream(new FileInputStream(fileSplitedI));

								if (i == 0) { // first file read total length
									byte[] longBytes = new byte[Long.SIZE / Byte.SIZE];
									size = fis.read(longBytes);

									if (size != 8) {
										break;
									}

									originalLength = ByteUtils.bytesToLong(longBytes);
								}

								long readed = 0;
								long fileLength = fileSplitedI.length();
								while ((size = fis.read(buf)) != -1) {
									fos.write(buf, 0, size);

									totalReaded += size;
									readed += size;

									int percentTotal = (int) (((double) totalReaded * 100) / (double) originalLength);
									dlg.setValueProgress(percentTotal);

									int percent = (int) (((double) readed * 100) / (double) fileLength);
									dlg.setText("Joining file " + fileSplitedI.getName() + "... "
											+ Integer.toString((int) percent) + "%");
								}

								i++;
								fis.close();
								fileSplitedI = new File(folderFrom, originalFileName + "." + i);
							}

							if (originalLength == -1 || totalReaded != originalLength) {
								fileOriginal.delete();

								showErrorMessage("File " + fileSplitedI.getAbsolutePath() + " is missing", "Error");
							}

							if (dlg.isCanceled()) {
								fileOriginal.delete();
							}

							fileModelTo.setFiles(folderTo.listFiles(filterFile));
							fos.close();
						} catch (Exception ex) {
							ex.printStackTrace();

							fileOriginal.delete();

							showErrorMessage(ex.getMessage(), "Error");
							return null;
						}

						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);
			}
		}
	}

	private void packFunction() {
		final FileTableModel fileModelFrom = getFileTableModelFrom();
		final FileTableModel fileModelTo = getFileTableModelTo();

		final File folderTo = getFolderTo();
		final File folderFrom = getFolderFrom();

		final File fileFrom = getFileSelected();

		if (fileModelFrom == null || fileModelTo == null || folderTo == null || folderFrom == null) {
			return;
		}

		if (fileFrom == null) {
			showWarning("No file/folder is selected", "Warning");
			return;
		}

		int reply = JOptionPane
				.showConfirmDialog(this,
						"Will zip \"" + fileFrom.getAbsolutePath() + "\" into zip file in \""
								+ folderTo.getAbsolutePath() + "\". Confirm?",
						"Confirm", JOptionPane.INFORMATION_MESSAGE);

		if (reply == JOptionPane.YES_OPTION) {
			final ProgressDialog dlg = new ProgressDialog(this, "Zipping Progress");
			dlg.hideCancelButton();
			dlg.setIndeterminate(true);

			final File fileOutput = new File(folderTo, fileFrom.getName() + ".zip");

			if (fileFrom.isDirectory()) {
				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						fileOutput.getParentFile().mkdirs();

						List<File> listFiles = new ArrayList<File>();
						try {
							FileUtils.listFile(listFiles, fileFrom);

							ZipOutputStream zipOutputStream = new ZipOutputStream(
									new BufferedOutputStream(new FileOutputStream(fileOutput)));

							for (File file : listFiles) {
								dlg.setText("Zipping file " + file.getName());

								String filePath = file.getCanonicalPath();
								int lengthDirectoryPath = fileFrom.getCanonicalPath().length();
								int lengthFilePath = file.getCanonicalPath().length();

								// Get path of files relative to input directory.
								String zipFilePath = filePath.substring(lengthDirectoryPath + 1, lengthFilePath);

								ZipEntry zipEntry = new ZipEntry(zipFilePath);
								zipOutputStream.putNextEntry(zipEntry);

								BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
								byte[] bytes = new byte[bufferSize];
								int length;
								while ((length = inputStream.read(bytes)) >= 0) {
									zipOutputStream.write(bytes, 0, length);
								}
								zipOutputStream.closeEntry();
								inputStream.close();
							}
							zipOutputStream.close();

						} catch (IOException e) {
							e.printStackTrace();
							showErrorMessage(e.getMessage(), "Error Zipping");
							return null;
						}

						fileModelTo.setFiles(folderTo.listFiles(filterFile));
						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);
			} else if (fileFrom.isFile()) {
				dlg.setIndeterminate(false);

				SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						fileOutput.getParentFile().mkdirs();

						ZipOutputStream zipOutputStream = new ZipOutputStream(
								new BufferedOutputStream(new FileOutputStream(fileOutput)));
						ZipEntry zipEntry = new ZipEntry(fileFrom.getName());
						zipOutputStream.putNextEntry(zipEntry);

						BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(fileFrom));
						byte[] bytes = new byte[bufferSize];
						int length;

						long totalReaded = 0;
						long fileLength = fileFrom.length();

						while ((length = inputStream.read(bytes)) >= 0) {
							zipOutputStream.write(bytes, 0, length);

							totalReaded += length;

							int percentTotal = (int) (((double) totalReaded * 100) / (double) fileLength);
							dlg.setValueProgress(percentTotal);

							dlg.setText("Ziping file " + fileFrom.getName() + "... " + Integer.toString(percentTotal)
									+ "%");
						}

						zipOutputStream.closeEntry();
						inputStream.close();
						zipOutputStream.close();

						fileModelTo.setFiles(folderTo.listFiles(filterFile));
						return null;
					}

					@Override
					protected void done() {
						dlg.setVisible(false);
					}
				};

				sw.execute();
				dlg.setVisible(true);
			}
		}
	}

	private void unPackFunction() {
		final FileTableModel fileModelFrom = getFileTableModelFrom();
		final FileTableModel fileModelTo = getFileTableModelTo();

		final File folderTo = getFolderTo();
		final File folderFrom = getFolderFrom();

		final File fileFrom = getFileSelected();

		if (fileModelFrom == null || fileModelTo == null || folderTo == null || folderFrom == null) {
			return;
		}

		if (fileFrom == null || !fileFrom.isFile()) {
			showWarning("No file is selected", "Warning");
			return;
		}

		int reply = JOptionPane
				.showConfirmDialog(this,
						"Will unzip \"" + fileFrom.getAbsolutePath() + "\" into folder in \""
								+ folderTo.getAbsolutePath() + "\". Confirm?",
						"Confirm", JOptionPane.INFORMATION_MESSAGE);

		if (reply == JOptionPane.YES_OPTION) {
			if (FileUtils.checkZipFile(fileFrom) == false) {
				showWarning(fileFrom.getAbsolutePath() + " is invalid zip file", "Warning");
				return;
			}

			final ProgressDialog dlg = new ProgressDialog(this, "UnZipping Progress");
			dlg.hideCancelButton();
			dlg.setIndeterminate(true);

			SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					File outputUnZippedContents = new File(folderTo, fileFrom.getName());
					outputUnZippedContents.delete();
					outputUnZippedContents.getParentFile().mkdirs();

					try {
						dlg.setText("Unzipping zip file...");

						ZipInputStream zipInputStream = new ZipInputStream(
								new BufferedInputStream(new FileInputStream(fileFrom)));
						ZipEntry zipEntry;

						while ((zipEntry = zipInputStream.getNextEntry()) != null) {
							dlg.setText("Extracting content: " + zipEntry.getName());
							File unZippedFile = new File(outputUnZippedContents, zipEntry.getName());

							// Create output directory
							unZippedFile.getParentFile().mkdirs();

							// Write contents to file
							BufferedOutputStream fileOutputStream = new BufferedOutputStream(
									new BufferedOutputStream(new FileOutputStream(unZippedFile)));
							int len;
							byte[] content = new byte[bufferSize];
							while ((len = zipInputStream.read(content)) > 0) {
								fileOutputStream.write(content, 0, len);
							}
							fileOutputStream.close();

							// Close current entry
							zipInputStream.closeEntry();
						}

						zipInputStream.close();
						fileModelTo.setFiles(folderTo.listFiles(filterFile));
					} catch (IOException e) {
						e.printStackTrace();
						showErrorMessage(e.getMessage(), "Error Unzipping File");

						return null;
					}

					fileModelTo.setFiles(folderTo.listFiles(filterFile));

					return null;
				}

				@Override
				protected void done() {
					dlg.setVisible(false);
				}
			};

			sw.execute();
			dlg.setVisible(true);
		}
	}

	private void reNameFunction() {
		final FileTableModel fileModel = getFileTableModelFrom();
		final File fileSelected = getFileSelected();

		if (fileModel == null) {
			return;
		}

		if (fileSelected == null) {
			showWarning("No file/folder is selected", "Warning");
			return;
		}

		String result = JOptionPane.showInputDialog(this,
				"Old name \"" + fileSelected.getName() + "\". Enter new name: ",
				"Rename " + fileSelected.getAbsolutePath(), -1);

		if (result != null && result.length() > 0) {
			String parentPath = fileSelected.getParent();

			parentPath += File.separator;

			parentPath += result;

			File newFolder = new File(parentPath);

			if (newFolder.exists()) {
				showWarning("Name is exist. Cancel!", "Warning");
				return;
			}

			boolean check = fileSelected.renameTo(newFolder);

			if (check == true) {
				fileModel.setFiles(fileSelected.getParentFile().listFiles(filterFile));
			} else {
				showWarning("Cannot rename", "Warning");
			}
		}
	}

	private void newFolderFunction() {
		final FileTableModel fileModel = getFileTableModelFrom();
		final File currentFolder = getFolderFrom();

		if (fileModel == null || currentFolder == null) {
			return;
		}

		String result = JOptionPane.showInputDialog(this, "Enter name of new folder",
				"New folder (directory) in " + currentFolder.getAbsolutePath(), -1);

		if (result != null && result.length() > 0) {
			File newFolder = new File(currentFolder, result);

			if (newFolder.exists()) {
				showWarning("This folder exist", "Warning");
				return;
			}

			boolean check = newFolder.mkdir();

			if (check == true) {
				fileModel.setFiles(currentFolder.listFiles(filterFile));

				if (focusBrowser == Focus.Left) {
					leftTableRowSelectedinModel = 0;
					if (fileModel.getRowCount() > 0)
						leftTableFile.setRowSelectionInterval(0, 0);
				} else if (focusBrowser == Focus.Right) {
					rightTableRowSelectedinModel = 0;
					if (fileModel.getRowCount() > 0)
						rightTableFile.setRowSelectionInterval(0, 0);
				}
			} else {
				showWarning("Cannot create folder (directory)", "Warning");
			}
		}
	}

	public void copy(File sourceLocation, File targetLocation) throws Exception {
		if (sourceLocation.isDirectory()) {
			copyDirectory(sourceLocation, targetLocation);
		} else {
			if (targetLocation.exists()) {
				if (choose == -1) {
					String[] options = new String[] { "Skip", "Skip All", "Replace", "Replace All", "Stop Copy" };
					String message = "File ";
					message += sourceLocation.getAbsolutePath();
					message += " exist in ";
					message += targetLocation.getAbsolutePath();
					int response = JOptionPane.showOptionDialog(null, message, "Confirm", JOptionPane.DEFAULT_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[0]);

					switch (response) {
					case 0: // Skip
						return;
					case 1: // Skip All
						choose = 1;
						break;
					case 3: // Replace All
						choose = 2;
						break;
					case 4: // Stop copy
						return;
					case 2: // Replace
						break;
					}
				} else if (choose == 1) {
					return;
				}
			}
			copyFile(sourceLocation, targetLocation);
		}
	}

	private void copyDirectory(File source, File target) throws Exception {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String f : source.list()) {
			copy(new File(source, f), new File(target, f));
		}
	}

	private void copyFile(File source, File target) throws Exception {
		try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(target));) {
			byte[] buf = new byte[bufferSize];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}
}
