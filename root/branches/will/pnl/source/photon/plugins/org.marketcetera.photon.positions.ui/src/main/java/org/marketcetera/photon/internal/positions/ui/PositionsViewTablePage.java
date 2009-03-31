package org.marketcetera.photon.internal.positions.ui;

import java.math.BigDecimal;
import java.util.List;

import net.miginfocom.swt.MigLayout;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.marketcetera.core.position.PositionEngine;
import org.marketcetera.core.position.PositionRow;
import org.marketcetera.core.position.PositionEngine.PositionData;
import org.marketcetera.photon.commons.ui.table.ColumnState;
import org.marketcetera.util.misc.ClassVersion;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swt.EventTableViewer;
import ca.odell.glazedlists.swt.TableComparatorChooser;
import ca.odell.glazedlists.swt.TableItemConfigurer;

/* $License$ */

/**
 * Flat table based positions view page.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class PositionsViewTablePage extends PositionsViewPage {

	private static final String TABLE_SORT_STATE_KEY = "tableSortState"; //$NON-NLS-1$

	private final class TableSelectionProvider extends PositionSelectionProvider {

		public TableSelectionProvider(Table table) {
			super(table);
		}

		@Override
		protected List<PositionRow> getSelectionFromWidget() {
			return mViewer.getSelected();
		}

	}

	private static final class PositionRowConfigurer implements TableItemConfigurer<PositionRow> {
		@Override
		public void configure(TableItem item, PositionRow rowValue, Object columnValue, int row,
				int column) {
			String text;
			if (column <= 2) {
				text = formatKey((String) columnValue);
			} else {
				text = formatBigDecimal((BigDecimal) columnValue);
			}
			item.setText(column, text);
		}
	}

	private static final class PositionTableFormat extends PositionMetricsFormat {

		private static final String[] COLUMN_NAMES = new String[] {
				Messages.POSITIONS_TABLE_SYMBOL_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_ACCOUNT_COLUMN_HEADING.getText(),
				Messages.POSITIONS_TABLE_TRADER_COLUMN_HEADING.getText() };

		public PositionTableFormat() {
			super(COLUMN_NAMES.length);
		}

		@Override
		public int getColumnCount() {
			return super.getColumnCount() + COLUMN_NAMES.length;
		}

		@Override
		public String getColumnName(int column) {
			if (column < COLUMN_NAMES.length) {
				return COLUMN_NAMES[column];
			} else {
				return super.getColumnName(column);
			}
		}

		@Override
		public Object getColumnValue(PositionRow baseObject, int column) {
			switch (column) {
			case 0:
				return baseObject.getSymbol();
			case 1:
				return baseObject.getAccount();
			case 2:
				return getTraderName(baseObject.getTraderId());
			default:
				return super.getColumnValue(baseObject, column);
			}
		}
	}

	private EventTableViewer<PositionRow> mViewer;
	private Table mTable;
	private TableComparatorChooser<PositionRow> mChooser;

	/**
	 * Constructor.
	 * 
	 * @param view
	 *            the view this page is part of, cannot be null
	 * @param memento
	 *            the saved page state
	 * @throws IllegalArgumentException
	 *             if view is null
	 */
	public PositionsViewTablePage(PositionsView view, IMemento memento) {
		super(view, memento);
	}

	@Override
	protected Control doCreateControl(Composite parent, IMemento memento) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new MigLayout("fill, ins 0")); //$NON-NLS-1$
		mTable = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.VIRTUAL);
		mTable.setLayoutData("dock center, hmin 100, wmin 100"); //$NON-NLS-1$
		EventList<PositionRow> positions = getPositions();
		SortedList<PositionRow> sorted = new SortedList<PositionRow>(positions, null);
		mViewer = new EventTableViewer<PositionRow>(sorted, mTable, new PositionTableFormat(),
				new PositionRowConfigurer());
		mChooser = TableComparatorChooser.install(mViewer, sorted, false);
		// make unrealized PL a bit wider
		mTable.getColumn(8).setWidth(90);
		if (memento != null) {
			ColumnState.restore(mTable, memento);
			String sortState = memento.getString(TABLE_SORT_STATE_KEY);
			if (sortState != null) {
				mChooser.fromString(sortState);
			}
		}
		for (TableColumn column : mTable.getColumns()) {
			column.setMoveable(true);
			if (column.getWidth() == 0) {
				column.setResizable(false);
			}
		}
		getSite().setSelectionProvider(new TableSelectionProvider(mTable));
		mTable.addListener(SWT.Activate, new Listener() {
		
			@Override
			public void handleEvent(Event event) {
				System.out.println("activation");
			}
		});
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().addSelectionListener(new ISelectionListener() {
		
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				System.out.println(part.getTitle() + " " + selection);
			}
		});
		return composite;
	}

	@Override
	public Control getColumnWidget() {
		return mTable;
	}

	@Override
	protected void controlDisposed() {
		mViewer.dispose();
	}

	@Override
	protected PositionData getPositionData() {
		PositionEngine engine = Activator.getDefault().getPositionEngine();
		return engine.getFlatData();
	}

	@Override
	protected TextFilterator<PositionRow> getFilterator() {
		return new TextFilterator<PositionRow>() {

			@Override
			public void getFilterStrings(List<String> baseList, PositionRow element) {
				// search symbol, account, and traderId (columns 0, 1, and 2)
				for (int i = 0; i <= 2; i++) {
					TableColumn column = mTable.getColumn(i);
					if (column.getWidth() > 0) {
						// use the column text instead of the row element value since it may be
						// decorated
						baseList.add(column.getText());
					}
				}
			}
		};
	}

	@Override
	public void saveState(IMemento memento) {
		ColumnState.save(mTable, memento);
		memento.putString(TABLE_SORT_STATE_KEY, mChooser.toString());
	}

}
