package at.spot.velocityeditor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import at.spot.velocityeditor.constants.ApplicationConstants;
import at.spot.velocityeditor.constants.Translations;
import at.spot.velocityeditor.constants.UiConstants;
import at.spot.velocityeditor.contentprovider.VariablesTableLabelProvider;
import at.spot.velocityeditor.contentprovider.dto.Variable;
import at.spot.velocityeditor.service.ImageResourceService;
import at.spot.velocityeditor.service.TranslationService;

@Configuration
@ComponentScan("at.spot.velocityeditor")
@Component("application")
public class Application {
	private static final String[] DEFAULT_FILETYPES = new String[] { "*.html", "*.htm", "*.md", "*.txt" };

	@Autowired
	TranslationService translationService;
	@Autowired
	ImageResourceService imageResourceService;

	protected Shell shell;
	private Display display;

	private ToolBar toolBar;

	private Text templateScriptText;
	private TableViewer variablesTable;
	private Browser previewBrowser;
	private SashForm mainSplitter;
	private SashForm sourceSplitter;
	private Text log;

	private final List<Variable> variables = new ArrayList<>();

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		try {
			// final ApplicationContext context = new
			// ClassPathXmlApplicationContext("spring.xml");
			final ApplicationContext context = new AnnotationConfigApplicationContext(Application.class);

			final Application app = context.getBean(Application.class);
			app.open();

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();

		createContents();

		shell.open();
		shell.layout();
		shell.setMinimumSize(800, 600);
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 3;
		layout.marginWidth = 3;

		shell = new Shell();
		shell.setSize(1000, 600);
		shell.setText(ApplicationConstants.APP_NAME);
		shell.setLayout(layout);

		createToolbar();
		createSplitters();
		createTemplateScriptText();
		createVariablesTable();
		// createVariablesTableControls();
		createLogPane();
		createPreview();
	}

	private void createToolbar() {
		toolBar = shell.getToolBar();

		final ToolItem openTemplateItem = createToolbarButton(Translations.TOOLITEM_OPEN_TEMPLATE,
				UiConstants.TOOLITEM_OPEN_TEMPLATE);
		openTemplateItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent paramSelectionEvent) {
				openTemplate();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent paramSelectionEvent) {
			}
		});

		final ToolItem newTemplateItem = createToolbarButton(Translations.TOOLITEM_NEW_TEMPLATE,
				UiConstants.TOOLITEM_NEW_TEMPLATE);
		newTemplateItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent paramSelectionEvent) {
				newTemplate();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent paramSelectionEvent) {
			}
		});

		final ToolItem saveTemplateItem = createToolbarButton(Translations.TOOLITEM_SAVE_TEMPLATE,
				UiConstants.TOOLITEM_SAVE_TEMPLATE);
		saveTemplateItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent paramSelectionEvent) {
				saveTemplate();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent paramSelectionEvent) {
			}
		});

		final ToolItem saveOutputItem = createToolbarButton(Translations.TOOLITEM_SAVE_OUTPUT,
				UiConstants.TOOLITEM_SAVE_OUTPUT);
		saveOutputItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent paramSelectionEvent) {
				saveRenderedOutput();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent paramSelectionEvent) {
			}
		});

		final ToolItem checkItem = createToolbarButton(Translations.TOOLITEM_CHECK, UiConstants.TOOLITEM_CHECK);
		checkItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent paramSelectionEvent) {
				checkTemplateScript();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent paramSelectionEvent) {
			}
		});

		// ToolItem parseVariablesItem =
		// createToolbarButton(Translations.TOOLITEM_PARSE_VARIABLES,
		// UiConstants.TOOLITEM_PARSE_VARIABLES);
		// parseVariablesItem.addSelectionListener(new SelectionListener() {
		// @Override
		// public void widgetSelected(SelectionEvent paramSelectionEvent) {
		// parseVariableTokens();
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent paramSelectionEvent)
		// {
		// }
		// });
	}

	private ToolItem createToolbarButton(final String textId, final String imageId) {
		final ToolItem item = new ToolItem(toolBar, SWT.PUSH);

		if (StringUtils.isNotEmpty(textId)) {
			final Image image = imageResourceService.getImageResource(imageId, 24);
			item.setImage(image);
		}

		item.setText(translationService.getTranslation(textId));

		return item;
	}

	private void createSplitters() {
		mainSplitter = new SashForm(shell, SWT.HORIZONTAL);
		mainSplitter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		sourceSplitter = new SashForm(mainSplitter, SWT.VERTICAL);
	}

	private void createVariablesTable() {
		variablesTable = new TableViewer(sourceSplitter, SWT.BORDER);

		variablesTable.setContentProvider(new ArrayContentProvider());
		variablesTable.setLabelProvider(new VariablesTableLabelProvider());
		variablesTable.setInput(variables);

		final Table table = variablesTable.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		final String[] titles = { translationService.getTranslation(Translations.COLUMN_VARIABLE),
				translationService.getTranslation(Translations.COLUMN_VALUE) };
		for (int i = 0; i < titles.length; i++) {
			final TableColumn column = new TableColumn(table, SWT.LEFT, i);
			column.setText(titles[i]);
		}

		variablesTable.getTable().getColumn(0).setWidth(150);
		variablesTable.getTable().getColumn(1).setWidth(200);

		final TableEditor editor = new TableEditor(table);
		// The editor must have the same size as the cell and must
		// not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;
		// editing the second column
		final int EDITABLECOLUMN = 1;

		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Clean up any previous editor control
				final Control oldEditor = editor.getEditor();
				if (oldEditor != null)
					oldEditor.dispose();

				// Identify the selected row
				final TableItem item = (TableItem) e.item;
				if (item == null)
					return;

				// The control that will be the editor must be a child of the
				// Table
				final Text newEditor = new Text(table, SWT.NONE);
				newEditor.setText(item.getText(EDITABLECOLUMN));
				newEditor.addModifyListener(new ModifyListener() {
					@Override
					public void modifyText(final ModifyEvent e) {
						final Text text = (Text) editor.getEditor();
						editor.getItem().setText(EDITABLECOLUMN, text.getText());
						final Variable v = (Variable) item.getData();
						v.setValue(text.getText());
					}
				});

				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, EDITABLECOLUMN);
				variablesTable.refresh();
			}
		});
	}

	private void createLogPane() {
		log = new Text(sourceSplitter, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		log.setEditable(false);

		templateScriptText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createTemplateScriptText() {
		templateScriptText = new Text(sourceSplitter, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		templateScriptText.setLayoutData(new GridData(GridData.FILL_BOTH));
		templateScriptText.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent paramKeyEvent) {
				parseVariableTokens();
			}

			@Override
			public void keyPressed(final KeyEvent paramKeyEvent) {
			}
		});
	}

	// private void createVariablesTableControls() {
	// SashForm container = new SashForm(shell, SWT.NONE);
	// container.setCapture(false);
	//
	// Button addRow = new Button(container, SWT.BORDER | SWT.BUTTON4);
	// addRow.setText(translationService.getTranslation(Translations.ADD_VARIABLE));
	// addRow.setSize(40, 40);
	// addRow.addSelectionListener(new SelectionListener() {
	//
	// @Override
	// public void widgetSelected(SelectionEvent arg0) {
	// variables.add(new
	// Variable(translationService.getTranslation(Translations.EMPTY_VARIABLE_NAME),
	// null));
	// variablesTable.refresh();
	// }
	//
	// @Override
	// public void widgetDefaultSelected(SelectionEvent arg0) {
	// }
	// });
	//
	// Button removeRow = new Button(container, SWT.BORDER | SWT.BUTTON4);
	// removeRow.setText(translationService.getTranslation(Translations.REMOVE_VARIABLE));
	// removeRow.setSize(40, 40);
	// removeRow.addSelectionListener(new SelectionListener() {
	//
	// @Override
	// public void widgetSelected(SelectionEvent arg0) {
	// if (!variablesTable.getSelection().isEmpty()) {
	// variables.remove(variablesTable.getTable().getSelectionIndex());
	// variablesTable.refresh();
	// }
	// }
	//
	// @Override
	// public void widgetDefaultSelected(SelectionEvent arg0) {
	// }
	// });
	// }

	private void createPreview() {
		previewBrowser = new Browser(mainSplitter, SWT.BORDER);
		previewBrowser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
	}

	// ################################################################################################
	// functionality
	// ################################################################################################

	private void openTemplate() {
		final FileDialog dialog = new FileDialog(shell, SWT.OPEN); // SWT.SHEET
																	// not
																	// working
																	// :-(;
		dialog.setFilterExtensions(DEFAULT_FILETYPES);
		final String result = dialog.open();

		if (StringUtils.isNotEmpty(result)) {
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(result))) {
				String fileContent = "";
				String line = null;
				while ((line = reader.readLine()) != null) {
					fileContent += line;
				}

				templateScriptText.setText(fileContent);
			} catch (final IOException e) {
				log.setText(ExceptionUtils.getStackTrace(e));
			}
		}
	}

	private void newTemplate() {
		templateScriptText.setText("");
		variables.clear();
		variablesTable.refresh();
		log.setText("");
		previewBrowser.setUrl("about:blank");
	}

	private void saveTemplate() {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE); // SWT.SHEET
																	// not
																	// working
																	// :-(;
		dialog.setFilterExtensions(DEFAULT_FILETYPES);
		final String result = dialog.open();

		if (StringUtils.isNotEmpty(result)) {
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(result))) {
				writer.write(templateScriptText.getText());
			} catch (final IOException e) {
				log.setText(ExceptionUtils.getStackTrace(e));
			}
		}
	}

	private void saveRenderedOutput() {
		final FileDialog dialog = new FileDialog(shell, SWT.SAVE); // SWT.SHEET
																	// not
																	// working
																	// :-(;
		dialog.setFilterExtensions(DEFAULT_FILETYPES);
		final String resultPath = dialog.open();

		if (StringUtils.isNotEmpty(resultPath)) {
			try {
				renderTemplateScript(new File(resultPath));
			} catch (final Exception e) {
				log.setText(ExceptionUtils.getStackTrace(e));
			}
		}
	}

	private void checkTemplateScript() {
		try {
			log.setText("");
			final String filePath = renderTemplateScript();
			previewBrowser.setUrl(filePath);
		} catch (final Exception e) {
			log.setText(ExceptionUtils.getStackTrace(e));
		}
	}

	private String renderTemplateScript() throws IOException {
		return renderTemplateScript(getTempFile());
	}

	private String renderTemplateScript(final File outputFile) {
		final Map<String, String> context = new HashMap<>();

		for (final Variable v : variables) {
			if (!StringUtils.isEmpty(v.getName()))
				// remove the . symbolizing a property access of an object with
				// and transform the whole object with a flat variable
				// do this also on the template script before letting velocity
				// render the template
				context.put(v.getName().replace(".", "_"), v.getValue());
		}

		try {
			final Writer writer = new FileWriter(outputFile);

			final Template template = new Template();
			final RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
			runtimeServices.setProperty("path", ".");

			final StringReader reader = new StringReader(templateScriptText.getText().replace(".", "_"));
			final SimpleNode node = runtimeServices.parse(reader, "NAME");

			template.setRuntimeServices(runtimeServices);
			template.setData(node);
			template.setEncoding("UTF-8");
			template.initDocument();

			template.merge(new VelocityContext(context), writer);
			writer.flush();

			writer.close();

			return outputFile.getAbsolutePath();
		} catch (final Exception e) {
			throw new VelocityException("Parsing of the email template using runtime services goes wrong", e);
		}
	}

	private void parseVariableTokens() {
		// clear variables from the table that have no value yet
		final List<Variable> newVariables = new ArrayList<>();

		for (final Variable v : variables) {
			if (!StringUtils.isEmpty(v.getValue())) {
				newVariables.add(v);
			}
		}

		variables.clear();
		variables.addAll(newVariables);

		final String tmp = templateScriptText.getText() + " ";

		final String regex = "\\$(.*?)[:,!?\\(\\)\\[\\]\\#<>\\s]";
		final Pattern pattern = Pattern.compile(regex);
		final Matcher m = pattern.matcher(tmp);

		while (m.find()) {
			String t = tmp.substring(m.start() + 1, m.end() - 1);

			// didn't find a way to delimit by \s or \.\s but not just a dot
			if (StringUtils.endsWith(t, ".")) {
				t = StringUtils.left(t, t.length() - 1);
			}

			boolean found = false;

			for (final Variable var : variables) {
				if (StringUtils.equals(var.getName(), t)) {
					found = true;
					break;
				}
			}

			if (!found)
				variables.add(new Variable(t, ""));
		}

		variablesTable.refresh();
	}

	private File getTempFile() throws IOException {
		return File.createTempFile("velocityPreview", ".html");
	}
}
